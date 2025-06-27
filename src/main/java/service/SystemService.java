package service;

import dao.SystemDAO;
import dao.SystemImplementationDAO;
import dao.SystemOwnerDAO;
import model.ITSystem;
import model.SystemImplementation;
import dto.SystemDto;
import dto.SystemRegistrationDto;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class SystemService {
    private final SystemDAO systemDAO;
    private final SystemImplementationDAO systemImplementationDAO;
    private final SystemOwnerDAO systemOwnerDAO;
    
    public SystemService() {
        this.systemDAO = new SystemDAO();
        this.systemImplementationDAO = new SystemImplementationDAO();
        this.systemOwnerDAO = new SystemOwnerDAO();
    }
    
    public SystemImplementation registerSystem(SystemRegistrationDto registrationDto) {
        // Find or create the base ITSystem
        ITSystem system = systemDAO.findByNameAndVendor(registrationDto.getName(), registrationDto.getVendor());
        if (system == null) {
            system = new ITSystem();
            system.setId(UUID.randomUUID());
            system.setName(registrationDto.getName());
            system.setVendor(registrationDto.getVendor());
            system.setDescription(registrationDto.getDescription());
            system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            systemDAO.create(system);
        }

        // Create the SystemImplementation
        SystemImplementation implementation = new SystemImplementation();
        implementation.setId(UUID.randomUUID());
        implementation.setSystemId(system.getId());
        implementation.setVersion(registrationDto.getVersion());
        implementation.setInternetFacing(registrationDto.isInternetFacing());
        implementation.setDataClassification(registrationDto.getDataClassification());
        implementation.setCriticalityLevel(registrationDto.getCriticalityLevel());
        implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Calculate and set the risk score
        implementation.setRiskScore(calculateRiskScore(registrationDto));
        
        // Save the implementation
        SystemImplementation createdImplementation = systemImplementationDAO.create(implementation);

        // Assign the owner
        if (registrationDto.getOwnerId() != null) {
            systemOwnerDAO.assignOwner(registrationDto.getOwnerId(), createdImplementation.getId());
        }

        return createdImplementation;
    }

    private int calculateRiskScore(SystemRegistrationDto dto) {
        int score = 0;

        // Criticality score
        switch (dto.getCriticalityLevel().toUpperCase()) {
            case "CRITICAL":
                score += 50;
                break;
            case "HIGH":
                score += 30;
                break;
            case "MEDIUM":
                score += 20;
                break;
            case "LOW":
                score += 10;
                break;
        }

        // Data Classification score
        switch (dto.getDataClassification().toUpperCase()) {
            case "SENSITIVE":
                 score += 30;
                 break;
            case "RESTRICTED":
                score += 25;
                break;
            case "CONFIDENTIAL":
                score += 20;
                break;
            case "INTERNAL":
                score += 10;
                break;
            case "PUBLIC":
                score += 0;
                break;
        }

        // Internet-facing score
        if (dto.isInternetFacing()) {
            score += 40;
        }

        return Math.min(score, 100); // Cap score at 100
    }
    
    /**
     * Retrieves all systems as a list of DTOs.
     *
     * @return list of SystemDto objects representing all systems
     */
    public Map<String, Object> getAllSystems() {
        List<ITSystem> systems = systemDAO.findAll();
        int totalSystems = systemDAO.countAll();

        List<SystemDto> systemDtos = systems.stream()
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("systems", systemDtos);
        response.put("totalSystems", totalSystems);
        response.put("page", 1);
        response.put("pageSize", totalSystems);
        
        return response;
    }
    
    public Map<String, Object> getAllSystems(int page, int pageSize) {
        List<ITSystem> systems = systemDAO.findAll(page, pageSize);
        int totalSystems = systemDAO.countAll();

        List<SystemDto> systemDtos = systems.stream()
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("systems", systemDtos);
        response.put("totalSystems", totalSystems);
        response.put("page", page);
        response.put("pageSize", pageSize);
        
        return response;
    }
    
    /**
     * Finds a system by its unique ID.
     *
     * @param id the UUID of the system to retrieve
     * @return SystemDto representing the system, or null if not found
     */
    public SystemDto getSystemById(UUID id) {
        ITSystem system = systemDAO.findById(id);
        return system != null ? toEnrichedDto(system) : null;
    }
    
    /**
     * Updates the system identified by the given ID with new details.
     *
     * @param id the UUID of the system to update
     * @param systemDto data transfer object containing updated system details
     * @return the updated ITSystem object, or null if system was not found
     */
    public ITSystem updateSystem(UUID id, SystemDto systemDto) {
        ITSystem system = systemDAO.findById(id);
        if (system != null) {
            system.setName(systemDto.getName());
            system.setVendor(systemDto.getVendor());
            system.setDescription(systemDto.getDescription());
            return systemDAO.update(system);
        }
        return null;
    }
    
    /**
     * Deletes a system by its ID.
     *
     * @param id the UUID of the system to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteSystem(UUID id) {
        return systemDAO.delete(id);
    }
    
    /**
     * Retrieves all systems owned by a specific user.
     *
     * @param ownerId the UUID of the owner (user) to get systems for
     * @return list of SystemDto objects representing systems owned by the user
     */
    public List<SystemDto> getSystemsByOwner(UUID ownerId) {
        // Get all implementations owned by this user
        List<SystemImplementation> implementations = systemOwnerDAO.findImplementationsByOwner(ownerId);
        
        // Get unique systems from these implementations
        List<UUID> systemIds = implementations.stream()
                .map(SystemImplementation::getSystemId)
                .distinct()
                .collect(Collectors.toList());
        
        // Get the actual systems and convert to DTOs
        List<SystemDto> systemDtos = systemIds.stream()
                .map(systemDAO::findById)
                .filter(system -> system != null)
                .map(this::toEnrichedDto)
                .collect(Collectors.toList());
        
        return systemDtos;
    }
    
    /**
     * Converts an ITSystem model to an enriched SystemDto that includes implementation details.
     *
     * @param system the ITSystem entity to convert
     * @return the corresponding enriched SystemDto
     */
    private SystemDto toEnrichedDto(ITSystem system) {
        // Find the primary implementation for this system
        List<SystemImplementation> implementations = systemImplementationDAO.findBySystem(system.getId());
        
        if (!implementations.isEmpty()) {
            // Use the first implementation for the main system data
            SystemImplementation primaryImpl = implementations.get(0);
            
            SystemDto dto = new SystemDto(
                    system.getId(),
                    system.getName(),
                    system.getVendor(),
                    system.getDescription(),
                    system.getCreatedAt()
            );
            
            // Add implementation fields
            dto.setVersion(primaryImpl.getVersion());
            dto.setEnvironment(primaryImpl.getEnvironment());
            dto.setRiskScore(primaryImpl.getRiskScore());
            dto.setDataClassification(primaryImpl.getDataClassification());
            dto.setCriticalityLevel(primaryImpl.getCriticalityLevel());
            dto.setInternetFacing(primaryImpl.isInternetFacing());
            dto.setSensitiveCustomerData(primaryImpl.isSensitiveCustomerData());
            
            // Get owner information
            UUID ownerId = systemOwnerDAO.findOwnerBySystemImplementationId(primaryImpl.getId());
            dto.setOwnerId(ownerId);
            
            return dto;
        } else {
            // Return basic system info if no implementations exist
            return new SystemDto(
                    system.getId(),
                    system.getName(),
                    system.getVendor(),
                    system.getDescription(),
                    system.getCreatedAt()
            );
        }
    }
    
    /**
     * Converts an ITSystem model to a SystemDto.
     *
     * @param system the ITSystem entity to convert
     * @return the corresponding SystemDto
     */
    private SystemDto toDto(ITSystem system) {
        return new SystemDto(
                system.getId(),
                system.getName(),
                system.getVendor(),
                system.getDescription(),
                system.getCreatedAt()
        );
    }
}

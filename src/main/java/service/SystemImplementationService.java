package service;

import dao.SystemImplementationDAO;
import model.SystemImplementation;
import dto.SystemImplementationDto;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import dao.SystemDAO;

public class SystemImplementationService {
    private SystemImplementationDAO systemImplementationDAO;
    private SystemDAO systemDAO;
    
    public SystemImplementationService() {
        this.systemImplementationDAO = new SystemImplementationDAO();
        this.systemDAO = new SystemDAO();
    }
    
    /**
     * Creates a new system implementation record.
     * Calculates and sets the risk score, and timestamps.
     *
     * @param implementationDto the DTO containing system implementation details
     * @return the created SystemImplementationDto with assigned ID and timestamps
     */
    public SystemImplementationDto createSystemImplementation(SystemImplementationDto implementationDto) {
        SystemImplementation implementation = fromDto(implementationDto);
        int riskScore = calculateRiskScore(implementation);
        implementation.setRiskScore(riskScore);
        implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        SystemImplementation createdImpl = systemImplementationDAO.create(implementation);
        return toDto(createdImpl);
    }
    
    /**
     * Retrieves all system implementations.
     *
     * @return list of SystemImplementationDto for all system implementations
     */
    public List<SystemImplementationDto> getAllSystemImplementations() {
        return systemImplementationDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves a system implementation by its ID.
     *
     * @param id the UUID of the system implementation
     * @return the SystemImplementationDto or null if not found
     */
    public SystemImplementationDto getSystemImplementationById(UUID id) {
        SystemImplementation implementation = systemImplementationDAO.findById(id);
        return implementation != null ? toDto(implementation) : null;
    }
    
    /**
     * Updates a system implementation identified by ID with new details.
     * Recalculates risk score and updates timestamp.
     *
     * @param id the UUID of the system implementation to update
     * @param implementationDto the DTO containing updated details
     * @return the updated SystemImplementationDto or null if not found
     */
    public SystemImplementationDto updateSystemImplementation(UUID id, SystemImplementationDto implementationDto) {
        SystemImplementation implementation = systemImplementationDAO.findById(id);
        if (implementation != null) {
            implementation.setDataClassification(implementationDto.getDataClassification());
            implementation.setCriticalityLevel(implementationDto.getCriticalityLevel());
            implementation.setInternetFacing(implementationDto.isInternetFacing());
            implementation.setSensitiveCustomerData(implementationDto.isSensitiveCustomerData());
            implementation.setVersion(implementationDto.getVersion());
            implementation.setEnvironment(implementationDto.getEnvironment());
            
            implementation.setRiskScore(calculateRiskScore(implementation));
            implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            SystemImplementation updatedImpl = systemImplementationDAO.update(implementation);
            return toDto(updatedImpl);
        }
        return null;
    }
    
    /**
     * Deletes a system implementation by its ID.
     *
     * @param id the UUID of the system implementation to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSystemImplementation(UUID id) {
        return systemImplementationDAO.delete(id);
    }
    
    /**
     * Calculates the risk score of a system implementation based on various criteria.
     *
     * @param implementation the SystemImplementation entity
     * @return risk score as an integer capped at 100
     */
    public int calculateRiskScore(SystemImplementation implementation) {
        int score = 0;
        if (implementation.isInternetFacing()) score += 30;
        if (implementation.isSensitiveCustomerData()) score += 20;
        
        String dataClass = implementation.getDataClassification();
        if ("CONFIDENTIAL".equalsIgnoreCase(dataClass)) score += 40;
        else if ("SENSITIVE".equalsIgnoreCase(dataClass)) score += 25;
        else if ("INTERNAL".equalsIgnoreCase(dataClass)) score += 10;
        
        String criticality = implementation.getCriticalityLevel();
        if ("HIGH".equalsIgnoreCase(criticality)) score += 30;
        else if ("MEDIUM".equalsIgnoreCase(criticality)) score += 20;
        else if ("LOW".equalsIgnoreCase(criticality)) score += 10;
        
        return Math.min(score, 100);
    }
    
    /**
     * Retrieves system implementations by department ID.
     *
     * @param departmentId the UUID of the department
     * @return list of SystemImplementationDto belonging to the department
     */
    public List<SystemImplementationDto> getImplementationsByDepartment(UUID departmentId) {
        List<SystemImplementation> implementations = systemImplementationDAO.findByDepartment(departmentId);
        return implementations.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    /**
     * Retrieves system implementations by system ID.
     *
     * @param systemId the UUID of the system
     * @return list of SystemImplementationDto belonging to the system
     */
    public List<SystemImplementationDto> getImplementationsBySystem(UUID systemId) {
        List<SystemImplementation> implementations = systemImplementationDAO.findBySystem(systemId);
        return implementations.stream().map(this::toDto).collect(Collectors.toList());
    }
    
    /**
     * Recalculates and updates the risk score for a system implementation.
     *
     * @param implementationId the UUID of the system implementation
     * @return updated SystemImplementationDto or null if not found
     */
    public SystemImplementationDto recalculateRiskScore(UUID implementationId) {
        SystemImplementation implementation = systemImplementationDAO.findById(implementationId);
        if (implementation != null) {
            int newRiskScore = calculateRiskScore(implementation);
            implementation.setRiskScore(newRiskScore);
            systemImplementationDAO.update(implementation);
            return toDto(implementation);
        }
        return null;
    }
    
    private SystemImplementationDto toDto(SystemImplementation implementation) {
        return new SystemImplementationDto(
                implementation.getId(),
                implementation.getSystemId(),
                implementation.getDepartmentId(),
                implementation.getDataClassification(),
                implementation.getCriticalityLevel(),
                implementation.isInternetFacing(),
                implementation.isSensitiveCustomerData(),
                implementation.getRiskScore(),
                implementation.getVersion(),
                implementation.getEnvironment(),
                implementation.getCreatedAt(),
                implementation.getUpdatedAt()
        );
    }
    
    private SystemImplementation fromDto(SystemImplementationDto dto) {
        SystemImplementation implementation = new SystemImplementation();
        implementation.setId(dto.getId());
        implementation.setSystemId(dto.getSystemId());
        implementation.setDepartmentId(dto.getDepartmentId());
        implementation.setDataClassification(dto.getDataClassification());
        implementation.setCriticalityLevel(dto.getCriticalityLevel());
        implementation.setInternetFacing(dto.isInternetFacing());
        implementation.setSensitiveCustomerData(dto.isSensitiveCustomerData());
        implementation.setVersion(dto.getVersion());
        implementation.setEnvironment(dto.getEnvironment());
        return implementation;
    }
}

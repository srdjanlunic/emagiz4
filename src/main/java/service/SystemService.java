package service;

import dao.SystemDAO;
import model.ITSystem;
import dto.SystemDto;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SystemService {
    private SystemDAO systemDAO;
    
    public SystemService() {
        this.systemDAO = new SystemDAO();
    }
    
    /**
     * Creates a new system record.
     *
     * @param systemDto data transfer object containing system details
     * @return the created ITSystem object with generated ID and timestamp
     */
    public ITSystem createSystem(SystemDto systemDto) {
        ITSystem system = new ITSystem();
        system.setId(UUID.randomUUID());
        system.setName(systemDto.getName());
        system.setVendor(systemDto.getVendor());
        system.setDescription(systemDto.getDescription());
        system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return systemDAO.create(system);
    }
    
    /**
     * Retrieves all systems as a list of DTOs.
     *
     * @return list of SystemDto objects representing all systems
     */
    public List<SystemDto> getAllSystems() {
        return systemDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds a system by its unique ID.
     *
     * @param id the UUID of the system to retrieve
     * @return SystemDto representing the system, or null if not found
     */
    public SystemDto getSystemById(UUID id) {
        ITSystem system = systemDAO.findById(id);
        return system != null ? toDto(system) : null;
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

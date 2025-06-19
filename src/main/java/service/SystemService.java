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

    public ITSystem createSystem(SystemDto systemDto) {
        ITSystem system = new ITSystem();
        system.setId(UUID.randomUUID());
        system.setName(systemDto.getName());
        system.setVendor(systemDto.getVendor());
        system.setDescription(systemDto.getDescription());
        system.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return systemDAO.create(system);
    }

    public List<SystemDto> getAllSystems() {
        return systemDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SystemDto getSystemById(UUID id) {
        ITSystem system = systemDAO.findById(id);
        return system != null ? toDto(system) : null;
    }

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

    public boolean deleteSystem(UUID id) {
        return systemDAO.delete(id);
    }

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

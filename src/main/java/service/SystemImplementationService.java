package service;

import dao.SystemImplementationDAO;
import model.SystemImplementation;
import dto.SystemImplementationDto;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import dao.SystemDAO;
import service.SystemService;
import util.JsonUtil;
import dto.SystemDto;
import dto.SystemImplementationDto;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/systems")
public class SystemImplementationService {
    private SystemImplementationDAO systemImplementationDAO;
    private SystemDAO systemDAO;

    public SystemImplementationService() {
        this.systemImplementationDAO = new SystemImplementationDAO();
        this.systemDAO = new SystemDAO();
    }

    public SystemImplementationDto createSystemImplementation(SystemImplementationDto implementationDto) {
        SystemImplementation implementation = fromDto(implementationDto);
        int riskScore = calculateRiskScore(implementation);
        implementation.setRiskScore(riskScore);
        implementation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        implementation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        SystemImplementation createdImpl = systemImplementationDAO.create(implementation);
        return toDto(createdImpl);
    }

    public List<SystemImplementationDto> getAllSystemImplementations() {
        return systemImplementationDAO.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SystemImplementationDto getSystemImplementationById(UUID id) {
        SystemImplementation implementation = systemImplementationDAO.findById(id);
        return implementation != null ? toDto(implementation) : null;
    }

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

    public boolean deleteSystemImplementation(UUID id) {
        return systemImplementationDAO.delete(id);
    }

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

    public List<SystemImplementationDto> getImplementationsByDepartment(UUID departmentId) {
        List<SystemImplementation> implementations = systemImplementationDAO.findByDepartment(departmentId);
        return implementations.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SystemImplementationDto> getImplementationsBySystem(UUID systemId) {
        List<SystemImplementation> implementations = systemImplementationDAO.findBySystem(systemId);
        return implementations.stream().map(this::toDto).collect(Collectors.toList());
    }

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

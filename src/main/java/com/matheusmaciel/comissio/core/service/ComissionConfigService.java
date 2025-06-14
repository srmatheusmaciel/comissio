package com.matheusmaciel.comissio.core.service;

import com.matheusmaciel.comissio.core.dto.comissionConfig.ComissionConfigRequestDTO;
import com.matheusmaciel.comissio.core.dto.comissionConfig.ComissionConfigResponseDTO;
import com.matheusmaciel.comissio.core.model.register.ComissionConfig;
import com.matheusmaciel.comissio.core.model.register.ServiceType;
import com.matheusmaciel.comissio.core.repository.ComissionConfigRepository;
import com.matheusmaciel.comissio.core.repository.ServiceTypeRepository;
import com.matheusmaciel.comissio.infra.exception.serviceType.DuplicateResourceException;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComissionConfigService {

    private final ComissionConfigRepository comissionConfigRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public ComissionConfigService(ComissionConfigRepository comissionConfigRepository, ServiceTypeRepository serviceTypeRepository) {
        this.comissionConfigRepository = comissionConfigRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @Transactional
    public ComissionConfigResponseDTO createOrUpdateComissionConfig(ComissionConfigRequestDTO dto) {
        ServiceType serviceType = this.serviceTypeRepository.findById(dto.serviceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));

        ComissionConfig comissionConfig = comissionConfigRepository.findByServiceTypeId(dto.serviceTypeId())
                .orElse(new ComissionConfig());

        if(comissionConfig.getId() == null && comissionConfigRepository.findByServiceTypeId(dto.serviceTypeId()).isPresent()) {
            throw new DuplicateResourceException("Comission config for service type '" + serviceType.getName() + "' already exists.");
        }

        comissionConfig.setServiceType(serviceType);
        comissionConfig.setDefaultPercentage(dto.defaultPercentage());

        ComissionConfig savedComissionConfig = this.comissionConfigRepository.save(comissionConfig);
        return ComissionConfigResponseDTO.fromEntity(savedComissionConfig);
    }

    public ComissionConfigResponseDTO updateComissionConfig(UUID comissionConfigId, ComissionConfigRequestDTO dto) {
        ComissionConfig comissionConfig = this.comissionConfigRepository.findById(comissionConfigId)
                .orElseThrow(() -> new ResourceNotFoundException("ComissionConfig not found with ID: " + comissionConfigId));

        ServiceType serviceType = this.serviceTypeRepository.findById(dto.serviceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));

        comissionConfigRepository.findByServiceTypeId(dto.serviceTypeId()).ifPresent(existingComissionConfig -> {
            if (!existingComissionConfig.getId().equals(comissionConfigId)) {
                throw new DuplicateResourceException("Comission config for service type '" + serviceType.getName() + "' already exists.");
            }
        });

        comissionConfig.setServiceType(serviceType);
        comissionConfig.setDefaultPercentage(dto.defaultPercentage());

        ComissionConfig updatedComissionConfig = this.comissionConfigRepository.save(comissionConfig);
        return ComissionConfigResponseDTO.fromEntity(updatedComissionConfig);
    }

    public ComissionConfigResponseDTO getComissionConfigById(UUID id){
        return comissionConfigRepository.findById(id)
                .map(ComissionConfigResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("ComissionConfig not found with ID: " + id));
    }

    public ComissionConfigResponseDTO getComissionConfigByServiceTypeId(UUID serviceTypeId){
        return comissionConfigRepository.findByServiceTypeId(serviceTypeId)
                .map(ComissionConfigResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("ComissionConfig not found with service type ID: " + serviceTypeId));
    }

    public List<ComissionConfigResponseDTO> getAllComissionConfigs() {
        return comissionConfigRepository.findAll()
                .stream()
                .map(ComissionConfigResponseDTO::fromEntity)
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteComissionConfig(UUID id) {
        if (!comissionConfigRepository.existsById(id)) {
            throw new ResourceNotFoundException("ComissionConfig not found with ID: " + id);
        }
        this.comissionConfigRepository.deleteById(id);
    }
}

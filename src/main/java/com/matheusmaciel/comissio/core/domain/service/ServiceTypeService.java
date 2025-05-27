package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.dto.ServiceType.ServiceTypeRequestDTO;
import com.matheusmaciel.comissio.core.domain.dto.ServiceType.ServiceTypeResponseDTO;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.repository.ServiceTypeRepository;
import com.matheusmaciel.comissio.infra.exception.serviceType.DuplicateResourceException;
import com.matheusmaciel.comissio.infra.exception.serviceType.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public ServiceType execute(ServiceType serviceType) {
       return this.serviceTypeRepository.save(serviceType);
    }

    @Transactional
    public ServiceTypeResponseDTO createServiceType(ServiceTypeRequestDTO dto){
        if(serviceTypeRepository.findByName(dto.name()).isPresent()){
            throw new DuplicateResourceException("Service with name '" + dto.name() + "' already exists.");
        }

        ServiceType serviceType = new ServiceType();
        serviceType.setName(dto.name());
        serviceType.setBasePrice(dto.basePrice());

        ServiceType savedServiceType = this.serviceTypeRepository.save(serviceType);
        return ServiceTypeResponseDTO.fromEntity(savedServiceType);
    }

    public List<ServiceTypeResponseDTO> getAllServiceTypes(){
        return serviceTypeRepository.findAll().stream()
                .map(ServiceTypeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ServiceTypeResponseDTO getServiceTypeById(UUID id) {
        return this.serviceTypeRepository.findById(id)
                .map(ServiceTypeResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));
    }

    @Transactional
    public ServiceTypeResponseDTO updateServiceType(UUID id, ServiceTypeRequestDTO dto) {
        ServiceType serviceType = this.serviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found"));

        serviceTypeRepository.findByName(dto.name()).ifPresent(existingServiceType -> {
            if (!existingServiceType.getId().equals(id)) {
                throw new DuplicateResourceException("Service with name '" + dto.name() + "' already exists.");
            }
        });

        serviceType.setName(dto.name());
        serviceType.setBasePrice(dto.basePrice());

        ServiceType updatedServiceType = this.serviceTypeRepository.save(serviceType);
        return ServiceTypeResponseDTO.fromEntity(updatedServiceType);
    }

    @Transactional
    public void deleteServiceType(UUID id) {
        if (!serviceTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceType not found with ID: " + id);
        }
        this.serviceTypeRepository.deleteById(id);
    }


}

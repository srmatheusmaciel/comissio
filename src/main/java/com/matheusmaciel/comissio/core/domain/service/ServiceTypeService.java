package com.matheusmaciel.comissio.core.domain.service;

import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.repository.ServiceTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository) {
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public ServiceType execute(ServiceType serviceType) {
       return this.serviceTypeRepository.save(serviceType);
    }




}

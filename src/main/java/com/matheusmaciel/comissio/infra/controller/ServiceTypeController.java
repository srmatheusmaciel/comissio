package com.matheusmaciel.comissio.infra.controller;

import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.service.ServiceTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-types")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    public ServiceTypeController(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody ServiceType serviceType) {
        try {
            var result = this.serviceTypeService.execute(serviceType);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}

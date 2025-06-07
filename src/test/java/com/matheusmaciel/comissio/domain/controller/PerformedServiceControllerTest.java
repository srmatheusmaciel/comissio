package com.matheusmaciel.comissio.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheusmaciel.comissio.AbstractIntegrationTest; // Importa a classe base
import com.matheusmaciel.comissio.core.domain.dto.performedService.PerformedServiceRequestDTO;
import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.access.UserRole;
import com.matheusmaciel.comissio.core.domain.model.register.*;
import com.matheusmaciel.comissio.core.domain.repository.*;
import com.matheusmaciel.comissio.infra.config.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PerformedServiceControllerTest extends AbstractIntegrationTest {


    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ServiceTypeRepository serviceTypeRepository;
    @Autowired private ComissionConfigRepository comissionConfigRepository;
    @Autowired private PerformedServiceRepository performedServiceRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String generateTestTokenForUser(User user) {
        return this.tokenService.generateToken(user);
    }

    @Test
    @DisplayName("Should register a new performed service and return 201 Created")
    @Transactional
    void registerService_withDefaultComission_shouldReturn201Created() throws Exception {

        User testUser = userRepository.save(User.builder().username("test-user-for-post").password(passwordEncoder.encode("123")).role(UserRole.ADMIN).email("testpost@test.com").name("Test User Post").build());
        Employee testEmployee = employeeRepository.save(Employee.builder().user(testUser).status(StatusEmployee.ACTIVE).build());
        ServiceType testServiceType = serviceTypeRepository.save(ServiceType.builder().name("Test Service " + UUID.randomUUID()).basePrice(new BigDecimal("100")).build());

        ComissionConfig defaultConfig = comissionConfigRepository.save(ComissionConfig.builder()
                .serviceType(testServiceType)
                .defaultPercentage(new BigDecimal("10.00"))
                .build());

        PerformedServiceRequestDTO requestDTO = new PerformedServiceRequestDTO(
                testEmployee.getId(),
                testServiceType.getId(),
                new BigDecimal("200.00"),
                LocalDate.of(2025, 6, 1)
        );

        String token = generateTestTokenForUser(testUser);

        ResultActions resultActions = mockMvc.perform(
                post("/performed-services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(requestDTO))
        );

        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.employeeId").value(testEmployee.getId().toString()))
                .andExpect(jsonPath("$.serviceTypeId").value(testServiceType.getId().toString()))
                .andExpect(jsonPath("$.price").value(200.00))
                .andExpect(jsonPath("$.comissionAmount").value(20.00))
                .andExpect(jsonPath("$.status").value("COMMISSION_PENDING"));

        List<PerformedService> services = performedServiceRepository.findAll();
        assertThat(services).hasSize(1);
        assertThat(services.get(0).getComissionAmount()).isEqualByComparingTo("20.00");
    }
}
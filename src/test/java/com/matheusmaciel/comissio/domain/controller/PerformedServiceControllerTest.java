package com.matheusmaciel.comissio.domain.controller;

import com.matheusmaciel.comissio.core.domain.model.access.User;
import com.matheusmaciel.comissio.core.domain.model.access.UserRole;
import com.matheusmaciel.comissio.core.domain.model.register.Employee;
import com.matheusmaciel.comissio.core.domain.model.register.ServiceType;
import com.matheusmaciel.comissio.core.domain.model.register.StatusEmployee;
import com.matheusmaciel.comissio.infra.config.security.TokenService;

import com.matheusmaciel.comissio.core.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("tect-tc")
public class PerformedServiceControllerTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("comissio_test_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.flyway.enabled", () -> "true");

        //If your Flyway schema is different than 'public'
        //registry.add("spring.flyway.schemas", () -> "public");

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

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
    @Autowired private EmployeeComissionRepository employeeComissionRepository;
    @Autowired private PerformedServiceRepository performedServiceRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${api.security.token.secret}")
    private String testJwtSecret;

    private User adminUser;
    private Employee sampleEmployee;
    private ServiceType sampleServiceType;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        performedServiceRepository.deleteAll();
        employeeComissionRepository.deleteAll();
        comissionConfigRepository.deleteAll();
        employeeRepository.deleteAll();
        userRepository.deleteAll();
        serviceTypeRepository.deleteAll();

        adminUser = User.builder()
                .name("Admin User")
                .username("admin_tc")
                .email("admin_tc@comissio.com")
                .password(passwordEncoder.encode("password"))
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(adminUser);

        sampleEmployee = Employee.builder()
                .user(adminUser)
                .status(StatusEmployee.ACTIVE)
                .build();

        employeeRepository.save(sampleEmployee);

        sampleServiceType = ServiceType.builder()
                .name("TC Service Type " + UUID.randomUUID().toString().substring(0, 6))
                .basePrice(new BigDecimal("150.00"))
                .build();

        serviceTypeRepository.save(sampleServiceType);
    }

    private String generateTestTokenForUser(User user) {
        return this.tokenService.generateToken(user);
    }

}

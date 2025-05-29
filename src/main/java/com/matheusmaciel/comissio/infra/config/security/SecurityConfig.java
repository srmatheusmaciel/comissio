package com.matheusmaciel.comissio.infra.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    private static final String[] SWAGGER_LIST = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/webjars/**",
        "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{


        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> { auth
                        .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                        // .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/register").hasRole("ADMIN") // If the register is only for admin
                        .requestMatchers(HttpMethod.GET, "/users/list").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST, "/employees").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/employees/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/employees/{id}/status").hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(HttpMethod.POST, "/service-types").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/service-types").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/service-types/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/service-types/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/service-types/{id}").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST, "/comission-configs").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/comission-configs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/comission-configs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/comission-configs/**").hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers(HttpMethod.POST, "/employee-comissions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employee-comissions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/employee-comissions/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/employee-comissions/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/performed-services").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/performed-services").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/performed-services/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/performed-services/{id}").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/performed-services/{id}/cancel").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/performed-services/{id}").hasRole("ADMIN")


                        .requestMatchers(SWAGGER_LIST).permitAll();

                auth.anyRequest().authenticated();
    })
               .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
;


        return http.build();


    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();

    }


}

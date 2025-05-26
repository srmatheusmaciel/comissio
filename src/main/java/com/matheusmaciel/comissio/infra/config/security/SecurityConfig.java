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
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/list").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employees").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/employees/list").hasRole("ADMIN")
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

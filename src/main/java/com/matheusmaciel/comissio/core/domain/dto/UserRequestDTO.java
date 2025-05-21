package com.matheusmaciel.comissio.core.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import com.matheusmaciel.comissio.core.domain.model.access.UserRole;


public record UserRequestDTO( 
    
@NotNull(message = "Name is required") 
String name,

@NotBlank(message = "Username is required")
@Pattern(regexp = "\\S+", message = "Username must not contain spaces")
String username,

@Email(message = "Invalid email")
@NotBlank(message = "Email is required")
String email,

@Length(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
String password,

@NotNull(message = "Role is required")
UserRole role

) {}

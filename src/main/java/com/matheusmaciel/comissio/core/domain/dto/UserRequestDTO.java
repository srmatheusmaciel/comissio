package com.matheusmaciel.comissio.core.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "\\S+", message = "Username must not contain spaces")
    private String username;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Length(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    private String role;
}

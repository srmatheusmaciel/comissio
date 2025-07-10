package com.matheusmaciel.comissio.core.dto.user;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
  @NotBlank String token,
  @NotBlank @Length(min = 8, max = 100) String newPassword
) {}

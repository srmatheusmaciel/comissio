package com.matheusmaciel.comissio.core.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;

    @Schema(example="Gavin Belson")
    private String name;

    @Schema(example="gavin")
    private String username;

    @Schema(example="gavin@me.com")
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

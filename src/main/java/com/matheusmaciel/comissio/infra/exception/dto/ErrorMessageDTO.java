package com.matheusmaciel.comissio.infra.exception.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {


    private String message;
    private String field;

}

package com.textforge.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenerationResponse {
    private Long id;
    private String mode;
    private String inputText;
    private String outputText;
    private LocalDateTime createdAt;
}
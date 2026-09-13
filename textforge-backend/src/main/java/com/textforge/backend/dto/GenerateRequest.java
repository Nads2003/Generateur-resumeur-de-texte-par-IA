package com.textforge.backend.dto;

import com.textforge.backend.entity.Generation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateRequest {

    @NotBlank(message = "Le texte est obligatoire")
    private String text;

    @NotBlank(message = "Le mode est obligatoire")
    private String mode; // "SUMMARIZE", "REWRITE", ou "EXPAND"
}
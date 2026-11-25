package br.com.elovital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CondicaoSaudeRequest(
        @NotBlank @Size(max = 255) String descricao,
        @Size(max = 150) String medicoNome,
        @PastOrPresent LocalDate dataDiagnostico
) {
}

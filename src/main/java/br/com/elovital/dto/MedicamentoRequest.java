package br.com.elovital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;

public record MedicamentoRequest(
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 100) String dosagem,
        @Size(max = 150) String medicoNome,
        @Size(max = 500) String fotoUrl,
        List<LocalTime> horarios
) {
}

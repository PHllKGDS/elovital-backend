package br.com.elovital.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record LembreteAguaCriarRequest(
        @NotNull LocalTime horario,
        @Min(50) Integer metaMl
) {
}

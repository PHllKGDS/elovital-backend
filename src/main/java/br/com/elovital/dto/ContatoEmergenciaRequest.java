package br.com.elovital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContatoEmergenciaRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 20) String telefone,
        @Size(max = 50) String parentesco
) {
}

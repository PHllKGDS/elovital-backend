package br.com.elovital.dto;

import br.com.elovital.model.enums.StatusVinculo;
import br.com.elovital.model.enums.TipoRelacao;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record VinculoAcessoRequest(
        @NotNull Long usuarioId,
        @NotNull TipoRelacao tipoRelacao,
        StatusVinculo status,
        Instant dataExpiracao
) {
}

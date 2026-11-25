package br.com.elovital.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        String tipoToken,
        Instant autenticadoEm,
        UsuarioResponse usuario,
        Long idosoId
) {
}

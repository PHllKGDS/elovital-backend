package br.com.elovital.dto;

import br.com.elovital.model.VinculoAcesso;
import br.com.elovital.model.enums.StatusVinculo;
import br.com.elovital.model.enums.TipoRelacao;

import java.time.Instant;

public record VinculoAcessoResponse(
        Long id,
        Long idosoId,
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        String telefoneUsuario,
        TipoRelacao tipoRelacao,
        StatusVinculo status,
        Instant dataExpiracao,
        Instant createdAt
) {
    public static VinculoAcessoResponse de(VinculoAcesso v) {
        return new VinculoAcessoResponse(
                v.getId(),
                v.getIdoso().getId(),
                v.getUsuario().getId(),
                v.getUsuario().getNome(),
                v.getUsuario().getEmail(),
                v.getUsuario().getTelefone(),
                v.getTipoRelacao(),
                v.getStatus(),
                v.getDataExpiracao(),
                v.getCreatedAt()
        );
    }
}

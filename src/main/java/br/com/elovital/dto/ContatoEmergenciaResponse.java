package br.com.elovital.dto;

import br.com.elovital.model.ContatoEmergencia;

import java.time.Instant;

public record ContatoEmergenciaResponse(
        Long id,
        Long idosoId,
        String nome,
        String telefone,
        String parentesco,
        Instant createdAt,
        Instant updatedAt
) {
    public static ContatoEmergenciaResponse de(ContatoEmergencia c) {
        return new ContatoEmergenciaResponse(
                c.getId(),
                c.getIdoso().getId(),
                c.getNome(),
                c.getTelefone(),
                c.getParentesco(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}

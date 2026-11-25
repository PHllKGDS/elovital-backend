package br.com.elovital.dto;

import br.com.elovital.model.CondicaoSaude;

import java.time.Instant;
import java.time.LocalDate;

public record CondicaoSaudeResponse(
        Long id,
        Long idosoId,
        String descricao,
        String medicoNome,
        LocalDate dataDiagnostico,
        Instant createdAt,
        Instant updatedAt
) {
    public static CondicaoSaudeResponse de(CondicaoSaude c) {
        return new CondicaoSaudeResponse(
                c.getId(),
                c.getIdoso().getId(),
                c.getDescricao(),
                c.getMedicoNome(),
                c.getDataDiagnostico(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}

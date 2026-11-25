package br.com.elovital.dto;

public record LembreteItemResponse(
        String id,
        String horario,
        String tipo,
        String descricao,
        Long referenciaId,
        Long registroId,
        String status
) {
}

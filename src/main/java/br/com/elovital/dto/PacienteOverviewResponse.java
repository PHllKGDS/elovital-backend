package br.com.elovital.dto;

import java.util.List;

public record PacienteOverviewResponse(
        IdosoResumoResponse paciente,
        List<CondicaoSaudeResponse> condicoesSaude,
        List<AlergiaResponse> alergias,
        List<ContatoEmergenciaResponse> contatosEmergencia,
        List<MedicamentoResponse> medicamentos,
        List<LembreteItemResponse> lembretesHoje,
        List<MedicaoResponse> medicoes,
        List<RelatorioDiarioResponse> relatoriosDiarios,
        List<VinculoAcessoResponse> equipeCuidado
) {
}

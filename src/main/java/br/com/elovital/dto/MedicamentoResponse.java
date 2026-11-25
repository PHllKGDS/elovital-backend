package br.com.elovital.dto;

import br.com.elovital.model.LembreteMedicamento;
import br.com.elovital.model.Medicamento;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public record MedicamentoResponse(
        Long id,
        Long idosoId,
        String nome,
        String dosagem,
        String medicoNome,
        String fotoUrl,
        String frequencia,
        List<String> horarios,
        Instant createdAt,
        Instant updatedAt
) {
    public static MedicamentoResponse de(Medicamento m, List<LembreteMedicamento> lembretes) {
        List<String> times = lembretes.stream()
                .filter(LembreteMedicamento::isAtivo)
                .map(LembreteMedicamento::getHorario)
                .sorted(Comparator.naturalOrder())
                .map(LocalTime::toString)
                .map(t -> t.length() >= 5 ? t.substring(0, 5) : t)
                .toList();

        String freq = times.isEmpty()
                ? "Conforme orientação médica"
                : times.size() + "x ao dia";

        return new MedicamentoResponse(
                m.getId(),
                m.getIdoso().getId(),
                m.getNome(),
                m.getDosagem(),
                m.getMedicoNome(),
                m.getFotoUrl(),
                freq,
                times,
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}

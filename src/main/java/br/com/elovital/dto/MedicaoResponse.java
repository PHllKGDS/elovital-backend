package br.com.elovital.dto;

import br.com.elovital.model.enums.TipoMedicao;

import java.time.Instant;

public record MedicaoResponse(
        Long id,
        Long idosoId,
        TipoMedicao tipo,
        Instant dataHora,
        String data,
        Integer sistolica,
        Integer diastolica,
        Integer glicemia
) {
}

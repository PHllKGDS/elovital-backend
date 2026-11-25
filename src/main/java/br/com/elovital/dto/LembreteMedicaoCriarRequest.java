package br.com.elovital.dto;

import br.com.elovital.model.enums.TipoMedicao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record LembreteMedicaoCriarRequest(
        @NotNull TipoMedicao tipo,
        @NotNull LocalTime horario
) {
}

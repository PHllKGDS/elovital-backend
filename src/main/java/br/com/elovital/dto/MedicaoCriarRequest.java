package br.com.elovital.dto;

import br.com.elovital.model.enums.TipoMedicao;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MedicaoCriarRequest(
        @NotNull TipoMedicao tipo,
        Instant dataHora,
        @Min(40) @Max(300) Integer sistolicaMmhg,
        @Min(30) @Max(200) Integer diastolicaMmhg,
        @Min(20) @Max(800) Integer valorMgdl
) {
}

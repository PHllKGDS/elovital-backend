package br.com.elovital.dto;

import java.time.LocalDate;

public record RelatorioDiarioResponse(
        LocalDate data,
        int total,
        int concluidos,
        int naoRealizados,
        int pendentes
) {
}

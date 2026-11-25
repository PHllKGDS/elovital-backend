package br.com.elovital.dto;

import br.com.elovital.model.Idoso;

import java.time.LocalDate;
import java.time.Period;

public record IdosoResumoResponse(
        Long id,
        Long usuarioId,
        String nome,
        LocalDate dataNascimento,
        int idade,
        String tipoSanguineo
) {
    public static IdosoResumoResponse de(Idoso i) {
        int idadeCalculada = i.getDataNascimento() != null
                ? Math.max(0, Period.between(i.getDataNascimento(), LocalDate.now()).getYears())
                : 0;
        return new IdosoResumoResponse(
                i.getId(),
                i.getUsuario().getId(),
                i.getUsuario().getNome(),
                i.getDataNascimento(),
                idadeCalculada,
                i.getTipoSanguineo()
        );
    }
}

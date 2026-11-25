package br.com.elovital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LembreteStatusRequest(
        @NotBlank
        @Pattern(regexp = "^(done|pending|missed)$", message = "Status deve ser: done, pending ou missed")
        String status
) {
}

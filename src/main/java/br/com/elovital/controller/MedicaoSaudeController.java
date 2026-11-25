package br.com.elovital.controller;

import br.com.elovital.dto.MedicaoCriarRequest;
import br.com.elovital.dto.MedicaoResponse;
import br.com.elovital.service.MedicaoSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/idosos/{idosoId}/medicoes")
public class MedicaoSaudeController {

    private final MedicaoSaudeService medicaoSaudeService;

    public MedicaoSaudeController(MedicaoSaudeService medicaoSaudeService) {
        this.medicaoSaudeService = medicaoSaudeService;
    }

    @GetMapping
    public List<MedicaoResponse> listar(@PathVariable Long idosoId,
                                        @RequestParam(defaultValue = "all") String periodo,
                                        @RequestParam(defaultValue = "true") boolean consolidado) {
        if (consolidado) {
            return medicaoSaudeService.listarConsolidadoPorDia(idosoId, periodo);
        }
        return medicaoSaudeService.listar(idosoId, periodo);
    }

    @PostMapping
    public ResponseEntity<MedicaoResponse> registrar(@PathVariable Long idosoId,
                                                     @Valid @RequestBody MedicaoCriarRequest req) {
        MedicaoResponse criada = medicaoSaudeService.registrar(idosoId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idosoId, @PathVariable Long id) {
        medicaoSaudeService.remover(idosoId, id);
    }
}

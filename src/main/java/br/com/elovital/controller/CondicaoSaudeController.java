package br.com.elovital.controller;

import br.com.elovital.dto.CondicaoSaudeRequest;
import br.com.elovital.dto.CondicaoSaudeResponse;
import br.com.elovital.service.CondicaoSaudeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/idosos/{idosoId}/condicoes-saude")
public class CondicaoSaudeController {

    private final CondicaoSaudeService condicaoSaudeService;

    public CondicaoSaudeController(CondicaoSaudeService condicaoSaudeService) {
        this.condicaoSaudeService = condicaoSaudeService;
    }

    @GetMapping
    public List<CondicaoSaudeResponse> listar(@PathVariable Long idosoId) {
        return condicaoSaudeService.listar(idosoId);
    }

    @GetMapping("/{id}")
    public CondicaoSaudeResponse buscar(@PathVariable Long idosoId, @PathVariable Long id) {
        return condicaoSaudeService.buscar(idosoId, id);
    }

    @PostMapping
    public ResponseEntity<CondicaoSaudeResponse> criar(@PathVariable Long idosoId,
                                                       @Valid @RequestBody CondicaoSaudeRequest req) {
        CondicaoSaudeResponse criada = condicaoSaudeService.criar(idosoId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public CondicaoSaudeResponse atualizar(@PathVariable Long idosoId,
                                           @PathVariable Long id,
                                           @Valid @RequestBody CondicaoSaudeRequest req) {
        return condicaoSaudeService.atualizar(idosoId, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idosoId, @PathVariable Long id) {
        condicaoSaudeService.remover(idosoId, id);
    }
}

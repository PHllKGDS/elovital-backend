package br.com.elovital.controller;

import br.com.elovital.dto.ContatoEmergenciaRequest;
import br.com.elovital.dto.ContatoEmergenciaResponse;
import br.com.elovital.service.ContatoEmergenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/idosos/{idosoId}/contatos-emergencia")
public class ContatoEmergenciaController {

    private final ContatoEmergenciaService contatoEmergenciaService;

    public ContatoEmergenciaController(ContatoEmergenciaService contatoEmergenciaService) {
        this.contatoEmergenciaService = contatoEmergenciaService;
    }

    @GetMapping
    public List<ContatoEmergenciaResponse> listar(@PathVariable Long idosoId) {
        return contatoEmergenciaService.listar(idosoId);
    }

    @GetMapping("/{id}")
    public ContatoEmergenciaResponse buscar(@PathVariable Long idosoId, @PathVariable Long id) {
        return contatoEmergenciaService.buscar(idosoId, id);
    }

    @PostMapping
    public ResponseEntity<ContatoEmergenciaResponse> criar(@PathVariable Long idosoId,
                                                           @Valid @RequestBody ContatoEmergenciaRequest req) {
        ContatoEmergenciaResponse criado = contatoEmergenciaService.criar(idosoId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ContatoEmergenciaResponse atualizar(@PathVariable Long idosoId,
                                               @PathVariable Long id,
                                               @Valid @RequestBody ContatoEmergenciaRequest req) {
        return contatoEmergenciaService.atualizar(idosoId, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idosoId, @PathVariable Long id) {
        contatoEmergenciaService.remover(idosoId, id);
    }
}

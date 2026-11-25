package br.com.elovital.controller;

import br.com.elovital.dto.VinculoAcessoRequest;
import br.com.elovital.dto.VinculoAcessoResponse;
import br.com.elovital.service.VinculoAcessoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/idosos/{idosoId}/equipe-cuidado")
public class VinculoAcessoController {

    private final VinculoAcessoService vinculoAcessoService;

    public VinculoAcessoController(VinculoAcessoService vinculoAcessoService) {
        this.vinculoAcessoService = vinculoAcessoService;
    }

    @GetMapping
    public List<VinculoAcessoResponse> listar(@PathVariable Long idosoId) {
        return vinculoAcessoService.listarPorIdoso(idosoId);
    }

    @PostMapping
    public ResponseEntity<VinculoAcessoResponse> criar(@PathVariable Long idosoId,
                                                       @Valid @RequestBody VinculoAcessoRequest req) {
        VinculoAcessoResponse criado = vinculoAcessoService.criar(idosoId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idosoId, @PathVariable Long id) {
        vinculoAcessoService.remover(idosoId, id);
    }
}

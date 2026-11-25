package br.com.elovital.controller;

import br.com.elovital.dto.MedicamentoRequest;
import br.com.elovital.dto.MedicamentoResponse;
import br.com.elovital.service.MedicamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/idosos/{idosoId}/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public List<MedicamentoResponse> listar(@PathVariable Long idosoId) {
        return medicamentoService.listar(idosoId);
    }

    @GetMapping("/{id}")
    public MedicamentoResponse buscar(@PathVariable Long idosoId, @PathVariable Long id) {
        return medicamentoService.buscar(idosoId, id);
    }

    @PostMapping
    public ResponseEntity<MedicamentoResponse> criar(@PathVariable Long idosoId,
                                                     @Valid @RequestBody MedicamentoRequest req) {
        MedicamentoResponse criado = medicamentoService.criar(idosoId, req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public MedicamentoResponse atualizar(@PathVariable Long idosoId,
                                         @PathVariable Long id,
                                         @Valid @RequestBody MedicamentoRequest req) {
        return medicamentoService.atualizar(idosoId, id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long idosoId, @PathVariable Long id) {
        medicamentoService.remover(idosoId, id);
    }
}

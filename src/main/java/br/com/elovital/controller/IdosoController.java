package br.com.elovital.controller;

import br.com.elovital.dto.*;
import br.com.elovital.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/idosos")
public class IdosoController {

    private final IdosoService idosoService;
    private final CondicaoSaudeService condicaoSaudeService;
    private final AlergiaService alergiaService;
    private final ContatoEmergenciaService contatoEmergenciaService;
    private final MedicamentoService medicamentoService;
    private final RotinaRelatorioService rotinaRelatorioService;
    private final MedicaoSaudeService medicaoSaudeService;
    private final VinculoAcessoService vinculoAcessoService;

    public IdosoController(IdosoService idosoService,
                           CondicaoSaudeService condicaoSaudeService,
                           AlergiaService alergiaService,
                           ContatoEmergenciaService contatoEmergenciaService,
                           MedicamentoService medicamentoService,
                           RotinaRelatorioService rotinaRelatorioService,
                           MedicaoSaudeService medicaoSaudeService,
                           VinculoAcessoService vinculoAcessoService) {
        this.idosoService = idosoService;
        this.condicaoSaudeService = condicaoSaudeService;
        this.alergiaService = alergiaService;
        this.contatoEmergenciaService = contatoEmergenciaService;
        this.medicamentoService = medicamentoService;
        this.rotinaRelatorioService = rotinaRelatorioService;
        this.medicaoSaudeService = medicaoSaudeService;
        this.vinculoAcessoService = vinculoAcessoService;
    }

    @GetMapping
    public List<IdosoResumoResponse> listar(@RequestParam(required = false) Long usuarioId) {
        return idosoService.listar(usuarioId);
    }

    @PostMapping
    public ResponseEntity<IdosoResponse> criar(@Valid @RequestBody IdosoCriarRequest req) {
        IdosoResponse criado = idosoService.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping("/{id}")
    public IdosoResponse buscar(@PathVariable Long id) {
        return idosoService.buscarPorId(id);
    }

    @GetMapping("/{id}/overview")
    public PacienteOverviewResponse buscarOverview(@PathVariable Long id) {
        IdosoResumoResponse paciente = idosoService.buscarResumoPorId(id);
        return new PacienteOverviewResponse(
                paciente,
                condicaoSaudeService.listar(id),
                alergiaService.listar(id),
                contatoEmergenciaService.listar(id),
                medicamentoService.listar(id),
                rotinaRelatorioService.listarLembretesDoDia(id, LocalDate.now()),
                medicaoSaudeService.listarConsolidadoPorDia(id, "all"),
                rotinaRelatorioService.gerarRelatoriosDiarios(id, 7),
                vinculoAcessoService.listarPorIdoso(id)
        );
    }

    @PutMapping("/{id}")
    public IdosoResponse atualizar(@PathVariable Long id, @Valid @RequestBody IdosoAtualizarRequest req) {
        return idosoService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        idosoService.remover(id);
    }
}

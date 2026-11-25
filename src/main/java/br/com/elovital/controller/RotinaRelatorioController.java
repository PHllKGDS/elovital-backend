package br.com.elovital.controller;

import br.com.elovital.dto.*;
import br.com.elovital.model.LembreteAgua;
import br.com.elovital.model.LembreteMedicao;
import br.com.elovital.service.RotinaRelatorioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/idosos/{idosoId}")
public class RotinaRelatorioController {

    private final RotinaRelatorioService rotinaRelatorioService;

    public RotinaRelatorioController(RotinaRelatorioService rotinaRelatorioService) {
        this.rotinaRelatorioService = rotinaRelatorioService;
    }

    @GetMapping("/lembretes")
    public List<LembreteItemResponse> listarLembretes(@PathVariable Long idosoId,
                                                      @RequestParam(required = false) LocalDate data) {
        return rotinaRelatorioService.listarLembretesDoDia(idosoId, data);
    }

    @PatchMapping("/lembretes/{lembreteCodigo}/status")
    public LembreteItemResponse atualizarStatus(@PathVariable Long idosoId,
                                                @PathVariable String lembreteCodigo,
                                                @Valid @RequestBody LembreteStatusRequest req) {
        return rotinaRelatorioService.atualizarStatusLembrete(idosoId, lembreteCodigo, req.status());
    }

    @PostMapping("/lembretes-agua")
    public ResponseEntity<Map<String, Object>> criarLembreteAgua(@PathVariable Long idosoId,
                                                                 @Valid @RequestBody LembreteAguaCriarRequest req) {
        LembreteAgua salvo = rotinaRelatorioService.criarLembreteAgua(idosoId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", salvo.getId(),
                "idosoId", idosoId,
                "horario", salvo.getHorario().toString(),
                "metaMl", salvo.getMetaMl()
        ));
    }

    @PostMapping("/lembretes-medicao")
    public ResponseEntity<Map<String, Object>> criarLembreteMedicao(@PathVariable Long idosoId,
                                                                    @Valid @RequestBody LembreteMedicaoCriarRequest req) {
        LembreteMedicao salvo = rotinaRelatorioService.criarLembreteMedicao(idosoId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", salvo.getId(),
                "idosoId", idosoId,
                "tipo", salvo.getTipo().name(),
                "horario", salvo.getHorario().toString(),
                "ativo", salvo.isAtivo()
        ));
    }

    @GetMapping("/relatorios-diarios")
    public List<RelatorioDiarioResponse> listarRelatoriosDiarios(@PathVariable Long idosoId,
                                                                 @RequestParam(defaultValue = "7") int dias) {
        return rotinaRelatorioService.gerarRelatoriosDiarios(idosoId, dias);
    }
}

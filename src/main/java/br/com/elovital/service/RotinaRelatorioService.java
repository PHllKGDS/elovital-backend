package br.com.elovital.service;

import br.com.elovital.dto.*;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.exception.RegraNegocioException;
import br.com.elovital.model.*;
import br.com.elovital.model.enums.StatusRegistroMedicamento;
import br.com.elovital.model.enums.TipoMedicao;
import br.com.elovital.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class RotinaRelatorioService {

    private final IdosoRepository idosoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LembreteMedicamentoRepository lembreteMedicamentoRepository;
    private final RegistroMedicamentoRepository registroMedicamentoRepository;
    private final LembreteAguaRepository lembreteAguaRepository;
    private final RegistroAguaRepository registroAguaRepository;
    private final LembreteMedicaoRepository lembreteMedicaoRepository;
    private final MedicaoSaudeRepository medicaoSaudeRepository;

    public RotinaRelatorioService(IdosoRepository idosoRepository,
                                  MedicamentoRepository medicamentoRepository,
                                  LembreteMedicamentoRepository lembreteMedicamentoRepository,
                                  RegistroMedicamentoRepository registroMedicamentoRepository,
                                  LembreteAguaRepository lembreteAguaRepository,
                                  RegistroAguaRepository registroAguaRepository,
                                  LembreteMedicaoRepository lembreteMedicaoRepository,
                                  MedicaoSaudeRepository medicaoSaudeRepository) {
        this.idosoRepository = idosoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.lembreteMedicamentoRepository = lembreteMedicamentoRepository;
        this.registroMedicamentoRepository = registroMedicamentoRepository;
        this.lembreteAguaRepository = lembreteAguaRepository;
        this.registroAguaRepository = registroAguaRepository;
        this.lembreteMedicaoRepository = lembreteMedicaoRepository;
        this.medicaoSaudeRepository = medicaoSaudeRepository;
    }

    public LembreteAgua criarLembreteAgua(Long idosoId, LembreteAguaCriarRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));
        LembreteAgua la = new LembreteAgua();
        la.setIdoso(idoso);
        la.setHorario(req.horario());
        la.setMetaMl(req.metaMl() != null ? req.metaMl() : 250);
        return lembreteAguaRepository.save(la);
    }

    public LembreteMedicao criarLembreteMedicao(Long idosoId, LembreteMedicaoCriarRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));
        LembreteMedicao lm = new LembreteMedicao();
        lm.setIdoso(idoso);
        lm.setTipo(req.tipo());
        lm.setHorario(req.horario());
        lm.setAtivo(true);
        return lembreteMedicaoRepository.save(lm);
    }

    @Transactional(readOnly = true)
    public List<LembreteItemResponse> listarLembretesDoDia(Long idosoId, LocalDate data) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }

        LocalDate dia = data != null ? data : LocalDate.now(ZoneOffset.UTC);
        Instant inicio = dia.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant fim = dia.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);
        boolean diaPassado = dia.isBefore(LocalDate.now(ZoneOffset.UTC));

        List<LembreteItemResponse> itens = new ArrayList<>();

        // 1. Lembretes de Medicamentos
        List<Medicamento> medicamentos = medicamentoRepository.findByIdosoId(idosoId);
        List<RegistroMedicamento> regsMed = registroMedicamentoRepository
                .findByMedicamentoIdosoIdAndDataHoraBetween(idosoId, inicio, fim);

        for (Medicamento med : medicamentos) {
            List<LembreteMedicamento> lembretesMed = lembreteMedicamentoRepository.findByMedicamentoId(med.getId());
            for (LembreteMedicamento lm : lembretesMed) {
                if (!lm.isAtivo()) continue;

                RegistroMedicamento reg = regsMed.stream()
                        .filter(r -> r.getLembreteMedicamento() != null
                                && r.getLembreteMedicamento().getId().equals(lm.getId()))
                        .findFirst()
                        .orElse(null);

                String status = "pending";
                if (reg != null) {
                    if (reg.getStatus() == StatusRegistroMedicamento.TOMADO) status = "done";
                    else if (reg.getStatus() == StatusRegistroMedicamento.PULADO) status = "missed";
                } else if (diaPassado) {
                    status = "missed";
                }

                String label = med.getDosagem() != null && !med.getDosagem().isBlank()
                        ? med.getNome() + " (" + med.getDosagem() + ")"
                        : med.getNome();

                itens.add(new LembreteItemResponse(
                        "MED-" + lm.getId(),
                        formatarHora(lm.getHorario()),
                        "medication",
                        label,
                        lm.getId(),
                        reg != null ? reg.getId() : null,
                        status
                ));
            }
        }

        // 2. Lembretes de Água
        List<LembreteAgua> lembretesAgua = lembreteAguaRepository.findByIdosoId(idosoId);
        List<RegistroAgua> regsAgua = registroAguaRepository.findByIdosoIdAndDataHoraBetween(idosoId, inicio, fim);

        for (LembreteAgua la : lembretesAgua) {
            RegistroAgua reg = regsAgua.stream()
                    .filter(r -> r.getLembreteAgua() != null && r.getLembreteAgua().getId().equals(la.getId()))
                    .findFirst()
                    .orElse(null);

            String status = reg != null ? "done" : (diaPassado ? "missed" : "pending");
            String desc = la.getMetaMl() != null ? "Beber água (" + la.getMetaMl() + " ml)" : "Beber água";

            itens.add(new LembreteItemResponse(
                    "AGUA-" + la.getId(),
                    formatarHora(la.getHorario()),
                    "water",
                    desc,
                    la.getId(),
                    reg != null ? reg.getId() : null,
                    status
            ));
        }

        // 3. Lembretes de Medição
        List<LembreteMedicao> lembretesMedicao = lembreteMedicaoRepository.findByIdosoId(idosoId);
        List<MedicaoSaude> medicoesDia = medicaoSaudeRepository.findByIdosoIdAndDataHoraBetween(idosoId, inicio, fim);

        for (LembreteMedicao lmed : lembretesMedicao) {
            if (!lmed.isAtivo()) continue;

            MedicaoSaude medicaoFeita = medicoesDia.stream()
                    .filter(m -> m.getTipo() == lmed.getTipo())
                    .findFirst()
                    .orElse(null);

            String status = medicaoFeita != null ? "done" : (diaPassado ? "missed" : "pending");
            String desc = lmed.getTipo() == TipoMedicao.PRESSAO
                    ? "Medir pressão arterial"
                    : "Medir glicemia";

            itens.add(new LembreteItemResponse(
                    "MEDICAO-" + lmed.getId(),
                    formatarHora(lmed.getHorario()),
                    "measurement",
                    desc,
                    lmed.getId(),
                    medicaoFeita != null ? medicaoFeita.getId() : null,
                    status
            ));
        }

        itens.sort(Comparator.comparing(LembreteItemResponse::horario));
        return itens;
    }

    public LembreteItemResponse atualizarStatusLembrete(Long idosoId, String lembreteCodigo, String novoStatus) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));

        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        Instant inicio = hoje.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant fim = hoje.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);

        if (lembreteCodigo.startsWith("MED-")) {
            Long lmId = parseId(lembreteCodigo, "MED-");
            LembreteMedicamento lm = lembreteMedicamentoRepository.findById(lmId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Lembrete de medicamento", lmId));
            if (!lm.getMedicamento().getIdoso().getId().equals(idosoId)) {
                throw new RecursoNaoEncontradoException("Lembrete de medicamento", lmId);
            }

            RegistroMedicamento reg = registroMedicamentoRepository
                    .findByMedicamentoIdosoIdAndDataHoraBetween(idosoId, inicio, fim).stream()
                    .filter(r -> r.getLembreteMedicamento() != null && r.getLembreteMedicamento().getId().equals(lmId))
                    .findFirst()
                    .orElse(null);

            if ("pending".equals(novoStatus)) {
                if (reg != null) registroMedicamentoRepository.delete(reg);
            } else {
                if (reg == null) {
                    reg = new RegistroMedicamento();
                    reg.setMedicamento(lm.getMedicamento());
                    reg.setLembreteMedicamento(lm);
                    reg.setDataHora(Instant.now());
                }
                reg.setStatus("done".equals(novoStatus)
                        ? StatusRegistroMedicamento.TOMADO
                        : StatusRegistroMedicamento.PULADO);
                registroMedicamentoRepository.save(reg);
            }
        } else if (lembreteCodigo.startsWith("AGUA-")) {
            Long laId = parseId(lembreteCodigo, "AGUA-");
            LembreteAgua la = lembreteAguaRepository.findByIdAndIdosoId(laId, idosoId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Lembrete de água", laId));

            RegistroAgua reg = registroAguaRepository.findByIdosoIdAndDataHoraBetween(idosoId, inicio, fim).stream()
                    .filter(r -> r.getLembreteAgua() != null && r.getLembreteAgua().getId().equals(laId))
                    .findFirst()
                    .orElse(null);

            if ("done".equals(novoStatus)) {
                if (reg == null) {
                    reg = new RegistroAgua();
                    reg.setIdoso(idoso);
                    reg.setLembreteAgua(la);
                    reg.setDataHora(Instant.now());
                    reg.setQuantidadeMl(la.getMetaMl() != null ? la.getMetaMl() : 250);
                    registroAguaRepository.save(reg);
                }
            } else if (reg != null) {
                registroAguaRepository.delete(reg);
            }
        } else {
            throw new RegraNegocioException("Código de lembrete inválido. Use o formato MED-{id} ou AGUA-{id}.");
        }

        return listarLembretesDoDia(idosoId, hoje).stream()
                .filter(i -> i.id().equals(lembreteCodigo))
                .findFirst()
                .orElse(new LembreteItemResponse(lembreteCodigo, "--:--", "medication", "", null, null, novoStatus));
    }

    @Transactional(readOnly = true)
    public List<RelatorioDiarioResponse> gerarRelatoriosDiarios(Long idosoId, int dias) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }

        int qtdDias = Math.max(1, Math.min(dias, 30));
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        List<RelatorioDiarioResponse> relatorios = new ArrayList<>();

        for (int i = 0; i < qtdDias; i++) {
            LocalDate dia = hoje.minusDays(i);
            List<LembreteItemResponse> itens = listarLembretesDoDia(idosoId, dia);
            int total = itens.size();
            int concluidos = (int) itens.stream().filter(x -> "done".equals(x.status())).count();
            int naoRealizados = (int) itens.stream().filter(x -> "missed".equals(x.status())).count();
            int pendentes = (int) itens.stream().filter(x -> "pending".equals(x.status())).count();

            relatorios.add(new RelatorioDiarioResponse(dia, total, concluidos, naoRealizados, pendentes));
        }

        return relatorios;
    }

    private Long parseId(String codigo, String prefixo) {
        try {
            return Long.parseLong(codigo.substring(prefixo.length()));
        } catch (NumberFormatException ex) {
            throw new RegraNegocioException("Identificador numérico inválido em: " + codigo);
        }
    }

    private String formatarHora(LocalTime horario) {
        String s = horario.toString();
        return s.length() >= 5 ? s.substring(0, 5) : s;
    }
}

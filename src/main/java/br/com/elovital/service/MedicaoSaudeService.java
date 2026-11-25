package br.com.elovital.service;

import br.com.elovital.dto.MedicaoCriarRequest;
import br.com.elovital.dto.MedicaoResponse;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.exception.RegraNegocioException;
import br.com.elovital.model.Glicemia;
import br.com.elovital.model.Idoso;
import br.com.elovital.model.MedicaoSaude;
import br.com.elovital.model.PressaoArterial;
import br.com.elovital.model.enums.TipoMedicao;
import br.com.elovital.repository.GlicemiaRepository;
import br.com.elovital.repository.IdosoRepository;
import br.com.elovital.repository.MedicaoSaudeRepository;
import br.com.elovital.repository.PressaoArterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MedicaoSaudeService {

    private final MedicaoSaudeRepository medicaoSaudeRepository;
    private final PressaoArterialRepository pressaoArterialRepository;
    private final GlicemiaRepository glicemiaRepository;
    private final IdosoRepository idosoRepository;

    public MedicaoSaudeService(MedicaoSaudeRepository medicaoSaudeRepository,
                               PressaoArterialRepository pressaoArterialRepository,
                               GlicemiaRepository glicemiaRepository,
                               IdosoRepository idosoRepository) {
        this.medicaoSaudeRepository = medicaoSaudeRepository;
        this.pressaoArterialRepository = pressaoArterialRepository;
        this.glicemiaRepository = glicemiaRepository;
        this.idosoRepository = idosoRepository;
    }

    public MedicaoResponse registrar(Long idosoId, MedicaoCriarRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));

        if (req.tipo() == TipoMedicao.PRESSAO) {
            if (req.sistolicaMmhg() == null || req.diastolicaMmhg() == null) {
                throw new RegraNegocioException("Para medição de PRESSAO, sistolicaMmhg e diastolicaMmhg são obrigatórios.");
            }
        } else if (req.tipo() == TipoMedicao.GLICEMIA) {
            if (req.valorMgdl() == null) {
                throw new RegraNegocioException("Para medição de GLICEMIA, valorMgdl é obrigatório.");
            }
        }

        MedicaoSaude medicao = new MedicaoSaude();
        medicao.setIdoso(idoso);
        medicao.setTipo(req.tipo());
        medicao.setDataHora(req.dataHora() != null ? req.dataHora() : Instant.now());
        MedicaoSaude salva = medicaoSaudeRepository.save(medicao);

        Integer sistolica = null;
        Integer diastolica = null;
        Integer glicemia = null;

        if (req.tipo() == TipoMedicao.PRESSAO) {
            PressaoArterial pa = new PressaoArterial();
            pa.setMedicao(salva);
            pa.setSistolicaMmhg(req.sistolicaMmhg());
            pa.setDiastolicaMmhg(req.diastolicaMmhg());
            pressaoArterialRepository.save(pa);
            sistolica = req.sistolicaMmhg();
            diastolica = req.diastolicaMmhg();
        } else {
            Glicemia gl = new Glicemia();
            gl.setMedicao(salva);
            gl.setValorMgdl(req.valorMgdl());
            glicemiaRepository.save(gl);
            glicemia = req.valorMgdl();
        }

        String dataDia = LocalDate.ofInstant(salva.getDataHora(), ZoneOffset.UTC).toString();
        return new MedicaoResponse(
                salva.getId(),
                idosoId,
                salva.getTipo(),
                salva.getDataHora(),
                dataDia,
                sistolica,
                diastolica,
                glicemia
        );
    }

    @Transactional(readOnly = true)
    public List<MedicaoResponse> listar(Long idosoId, String periodo) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }

        Instant limite = null;
        if ("7d".equalsIgnoreCase(periodo)) {
            limite = Instant.now().minus(7, ChronoUnit.DAYS);
        } else if ("30d".equalsIgnoreCase(periodo)) {
            limite = Instant.now().minus(30, ChronoUnit.DAYS);
        }

        final Instant limiteFinal = limite;
        List<MedicaoSaude> medicoes = medicaoSaudeRepository.findByIdosoIdOrderByDataHoraAsc(idosoId).stream()
                .filter(m -> limiteFinal == null || !m.getDataHora().isBefore(limiteFinal))
                .toList();

        return medicoes.stream().map(this::mapearDetalhe).toList();
    }

    /**
     * Consolida medições por dia para alimentar os gráficos de Pressão + Glicemia do Histórico.
     */
    @Transactional(readOnly = true)
    public List<MedicaoResponse> listarConsolidadoPorDia(Long idosoId, String periodo) {
        List<MedicaoResponse> todas = listar(idosoId, periodo);
        Map<String, MedicaoResponse> porDia = new LinkedHashMap<>();

        for (MedicaoResponse m : todas) {
            MedicaoResponse existente = porDia.get(m.data());
            if (existente == null) {
                porDia.put(m.data(), m);
            } else {
                porDia.put(m.data(), new MedicaoResponse(
                        m.id(),
                        idosoId,
                        m.tipo(),
                        m.dataHora(),
                        m.data(),
                        m.sistolica() != null ? m.sistolica() : existente.sistolica(),
                        m.diastolica() != null ? m.diastolica() : existente.diastolica(),
                        m.glicemia() != null ? m.glicemia() : existente.glicemia()
                ));
            }
        }

        return porDia.values().stream()
                .sorted(Comparator.comparing(MedicaoResponse::data))
                .toList();
    }

    public void remover(Long idosoId, Long id) {
        MedicaoSaude medicao = medicaoSaudeRepository.findByIdAndIdosoId(id, idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medição", id));
        if (medicao.getTipo() == TipoMedicao.PRESSAO) {
            pressaoArterialRepository.findById(id).ifPresent(pressaoArterialRepository::delete);
        } else {
            glicemiaRepository.findById(id).ifPresent(glicemiaRepository::delete);
        }
        medicaoSaudeRepository.delete(medicao);
    }

    private MedicaoResponse mapearDetalhe(MedicaoSaude m) {
        Integer sistolica = null;
        Integer diastolica = null;
        Integer glicemia = null;

        if (m.getTipo() == TipoMedicao.PRESSAO) {
            PressaoArterial pa = pressaoArterialRepository.findById(m.getId()).orElse(null);
            if (pa != null) {
                sistolica = pa.getSistolicaMmhg();
                diastolica = pa.getDiastolicaMmhg();
            }
        } else {
            Glicemia gl = glicemiaRepository.findById(m.getId()).orElse(null);
            if (gl != null) {
                glicemia = gl.getValorMgdl();
            }
        }

        String dataDia = LocalDate.ofInstant(m.getDataHora(), ZoneOffset.UTC).toString();
        return new MedicaoResponse(
                m.getId(),
                m.getIdoso().getId(),
                m.getTipo(),
                m.getDataHora(),
                dataDia,
                sistolica,
                diastolica,
                glicemia
        );
    }
}

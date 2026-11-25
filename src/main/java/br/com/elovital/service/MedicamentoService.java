package br.com.elovital.service;

import br.com.elovital.dto.MedicamentoRequest;
import br.com.elovital.dto.MedicamentoResponse;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.model.Idoso;
import br.com.elovital.model.LembreteMedicamento;
import br.com.elovital.model.Medicamento;
import br.com.elovital.repository.IdosoRepository;
import br.com.elovital.repository.LembreteMedicamentoRepository;
import br.com.elovital.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final LembreteMedicamentoRepository lembreteMedicamentoRepository;
    private final IdosoRepository idosoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository,
                              LembreteMedicamentoRepository lembreteMedicamentoRepository,
                              IdosoRepository idosoRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.lembreteMedicamentoRepository = lembreteMedicamentoRepository;
        this.idosoRepository = idosoRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponse> listar(Long idosoId) {
        garantirIdosoExiste(idosoId);
        return medicamentoRepository.findByIdosoId(idosoId).stream()
                .map(m -> MedicamentoResponse.de(m, lembreteMedicamentoRepository.findByMedicamentoId(m.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoResponse buscar(Long idosoId, Long id) {
        Medicamento med = obterOuFalhar(idosoId, id);
        return MedicamentoResponse.de(med, lembreteMedicamentoRepository.findByMedicamentoId(med.getId()));
    }

    public MedicamentoResponse criar(Long idosoId, MedicamentoRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));

        Medicamento med = new Medicamento();
        med.setIdoso(idoso);
        med.setNome(req.nome().trim());
        med.setDosagem(req.dosagem() != null ? req.dosagem().trim() : null);
        med.setMedicoNome(req.medicoNome() != null ? req.medicoNome().trim() : null);
        med.setFotoUrl(req.fotoUrl());
        Medicamento salvo = medicamentoRepository.save(med);

        if (req.horarios() != null) {
            for (LocalTime horario : req.horarios()) {
                LembreteMedicamento lembrete = new LembreteMedicamento();
                lembrete.setMedicamento(salvo);
                lembrete.setHorario(horario);
                lembrete.setAtivo(true);
                lembreteMedicamentoRepository.save(lembrete);
            }
        }

        return MedicamentoResponse.de(salvo, lembreteMedicamentoRepository.findByMedicamentoId(salvo.getId()));
    }

    public MedicamentoResponse atualizar(Long idosoId, Long id, MedicamentoRequest req) {
        Medicamento med = obterOuFalhar(idosoId, id);
        med.setNome(req.nome().trim());
        med.setDosagem(req.dosagem() != null ? req.dosagem().trim() : null);
        med.setMedicoNome(req.medicoNome() != null ? req.medicoNome().trim() : null);
        med.setFotoUrl(req.fotoUrl());

        if (req.horarios() != null) {
            List<LembreteMedicamento> existentes = lembreteMedicamentoRepository.findByMedicamentoId(med.getId());
            lembreteMedicamentoRepository.deleteAll(existentes);
            for (LocalTime horario : req.horarios()) {
                LembreteMedicamento lembrete = new LembreteMedicamento();
                lembrete.setMedicamento(med);
                lembrete.setHorario(horario);
                lembrete.setAtivo(true);
                lembreteMedicamentoRepository.save(lembrete);
            }
        }

        return MedicamentoResponse.de(med, lembreteMedicamentoRepository.findByMedicamentoId(med.getId()));
    }

    public void remover(Long idosoId, Long id) {
        Medicamento med = obterOuFalhar(idosoId, id);
        List<LembreteMedicamento> lembretes = lembreteMedicamentoRepository.findByMedicamentoId(med.getId());
        lembreteMedicamentoRepository.deleteAll(lembretes);
        medicamentoRepository.delete(med);
    }

    private void garantirIdosoExiste(Long idosoId) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }
    }

    private Medicamento obterOuFalhar(Long idosoId, Long id) {
        return medicamentoRepository.findByIdAndIdosoId(id, idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medicamento", id));
    }
}

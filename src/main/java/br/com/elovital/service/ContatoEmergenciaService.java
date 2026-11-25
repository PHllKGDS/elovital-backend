package br.com.elovital.service;

import br.com.elovital.dto.ContatoEmergenciaRequest;
import br.com.elovital.dto.ContatoEmergenciaResponse;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.model.ContatoEmergencia;
import br.com.elovital.model.Idoso;
import br.com.elovital.repository.ContatoEmergenciaRepository;
import br.com.elovital.repository.IdosoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContatoEmergenciaService {

    private final ContatoEmergenciaRepository contatoEmergenciaRepository;
    private final IdosoRepository idosoRepository;

    public ContatoEmergenciaService(ContatoEmergenciaRepository contatoEmergenciaRepository,
                                    IdosoRepository idosoRepository) {
        this.contatoEmergenciaRepository = contatoEmergenciaRepository;
        this.idosoRepository = idosoRepository;
    }

    @Transactional(readOnly = true)
    public List<ContatoEmergenciaResponse> listar(Long idosoId) {
        garantirIdosoExiste(idosoId);
        return contatoEmergenciaRepository.findByIdosoId(idosoId).stream()
                .map(ContatoEmergenciaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContatoEmergenciaResponse buscar(Long idosoId, Long id) {
        return ContatoEmergenciaResponse.de(obterOuFalhar(idosoId, id));
    }

    public ContatoEmergenciaResponse criar(Long idosoId, ContatoEmergenciaRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));
        ContatoEmergencia contato = new ContatoEmergencia();
        contato.setIdoso(idoso);
        contato.setNome(req.nome().trim());
        contato.setTelefone(req.telefone().trim());
        contato.setParentesco(req.parentesco() != null ? req.parentesco().trim() : null);
        return ContatoEmergenciaResponse.de(contatoEmergenciaRepository.save(contato));
    }

    public ContatoEmergenciaResponse atualizar(Long idosoId, Long id, ContatoEmergenciaRequest req) {
        ContatoEmergencia contato = obterOuFalhar(idosoId, id);
        contato.setNome(req.nome().trim());
        contato.setTelefone(req.telefone().trim());
        contato.setParentesco(req.parentesco() != null ? req.parentesco().trim() : null);
        return ContatoEmergenciaResponse.de(contato);
    }

    public void remover(Long idosoId, Long id) {
        contatoEmergenciaRepository.delete(obterOuFalhar(idosoId, id));
    }

    private void garantirIdosoExiste(Long idosoId) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }
    }

    private ContatoEmergencia obterOuFalhar(Long idosoId, Long id) {
        return contatoEmergenciaRepository.findByIdAndIdosoId(id, idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato de emergência", id));
    }
}

package br.com.elovital.service;

import br.com.elovital.dto.CondicaoSaudeRequest;
import br.com.elovital.dto.CondicaoSaudeResponse;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.model.CondicaoSaude;
import br.com.elovital.model.Idoso;
import br.com.elovital.repository.CondicaoSaudeRepository;
import br.com.elovital.repository.IdosoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CondicaoSaudeService {

    private final CondicaoSaudeRepository condicaoSaudeRepository;
    private final IdosoRepository idosoRepository;

    public CondicaoSaudeService(CondicaoSaudeRepository condicaoSaudeRepository,
                                IdosoRepository idosoRepository) {
        this.condicaoSaudeRepository = condicaoSaudeRepository;
        this.idosoRepository = idosoRepository;
    }

    @Transactional(readOnly = true)
    public List<CondicaoSaudeResponse> listar(Long idosoId) {
        garantirIdosoExiste(idosoId);
        return condicaoSaudeRepository.findByIdosoId(idosoId).stream()
                .map(CondicaoSaudeResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public CondicaoSaudeResponse buscar(Long idosoId, Long id) {
        return CondicaoSaudeResponse.de(obterOuFalhar(idosoId, id));
    }

    public CondicaoSaudeResponse criar(Long idosoId, CondicaoSaudeRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));
        CondicaoSaude condicao = new CondicaoSaude();
        condicao.setIdoso(idoso);
        condicao.setDescricao(req.descricao().trim());
        condicao.setMedicoNome(req.medicoNome() != null ? req.medicoNome().trim() : null);
        condicao.setDataDiagnostico(req.dataDiagnostico());
        return CondicaoSaudeResponse.de(condicaoSaudeRepository.save(condicao));
    }

    public CondicaoSaudeResponse atualizar(Long idosoId, Long id, CondicaoSaudeRequest req) {
        CondicaoSaude condicao = obterOuFalhar(idosoId, id);
        condicao.setDescricao(req.descricao().trim());
        condicao.setMedicoNome(req.medicoNome() != null ? req.medicoNome().trim() : null);
        condicao.setDataDiagnostico(req.dataDiagnostico());
        return CondicaoSaudeResponse.de(condicao);
    }

    public void remover(Long idosoId, Long id) {
        condicaoSaudeRepository.delete(obterOuFalhar(idosoId, id));
    }

    private void garantirIdosoExiste(Long idosoId) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }
    }

    private CondicaoSaude obterOuFalhar(Long idosoId, Long id) {
        return condicaoSaudeRepository.findByIdAndIdosoId(id, idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Condição de saúde", id));
    }
}

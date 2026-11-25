package br.com.elovital.service;

import br.com.elovital.dto.VinculoAcessoRequest;
import br.com.elovital.dto.VinculoAcessoResponse;
import br.com.elovital.exception.ConflitoException;
import br.com.elovital.exception.RecursoNaoEncontradoException;
import br.com.elovital.model.Idoso;
import br.com.elovital.model.Usuario;
import br.com.elovital.model.VinculoAcesso;
import br.com.elovital.model.enums.StatusVinculo;
import br.com.elovital.repository.IdosoRepository;
import br.com.elovital.repository.UsuarioRepository;
import br.com.elovital.repository.VinculoAcessoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VinculoAcessoService {

    private final VinculoAcessoRepository vinculoAcessoRepository;
    private final IdosoRepository idosoRepository;
    private final UsuarioRepository usuarioRepository;

    public VinculoAcessoService(VinculoAcessoRepository vinculoAcessoRepository,
                                IdosoRepository idosoRepository,
                                UsuarioRepository usuarioRepository) {
        this.vinculoAcessoRepository = vinculoAcessoRepository;
        this.idosoRepository = idosoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<VinculoAcessoResponse> listarPorIdoso(Long idosoId) {
        if (!idosoRepository.existsById(idosoId)) {
            throw new RecursoNaoEncontradoException("Idoso", idosoId);
        }
        return vinculoAcessoRepository.findByIdosoId(idosoId).stream()
                .map(VinculoAcessoResponse::de)
                .toList();
    }

    public VinculoAcessoResponse criar(Long idosoId, VinculoAcessoRequest req) {
        Idoso idoso = idosoRepository.findById(idosoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Idoso", idosoId));
        Usuario usuario = usuarioRepository.findById(req.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", req.usuarioId()));

        if (vinculoAcessoRepository.existsByIdosoIdAndUsuarioId(idosoId, req.usuarioId())) {
            throw new ConflitoException("Este usuário já possui vínculo com o idoso informado.");
        }

        VinculoAcesso vinculo = new VinculoAcesso();
        vinculo.setIdoso(idoso);
        vinculo.setUsuario(usuario);
        vinculo.setTipoRelacao(req.tipoRelacao());
        vinculo.setStatus(req.status() != null ? req.status() : StatusVinculo.ATIVO);
        vinculo.setDataExpiracao(req.dataExpiracao());
        return VinculoAcessoResponse.de(vinculoAcessoRepository.save(vinculo));
    }

    public void remover(Long idosoId, Long id) {
        VinculoAcesso vinculo = vinculoAcessoRepository.findById(id)
                .filter(v -> v.getIdoso().getId().equals(idosoId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo de acesso", id));
        vinculoAcessoRepository.delete(vinculo);
    }
}

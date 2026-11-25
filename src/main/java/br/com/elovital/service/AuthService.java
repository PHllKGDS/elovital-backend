package br.com.elovital.service;

import br.com.elovital.dto.LoginRequest;
import br.com.elovital.dto.LoginResponse;
import br.com.elovital.dto.UsuarioResponse;
import br.com.elovital.exception.CredenciaisInvalidasException;
import br.com.elovital.model.Idoso;
import br.com.elovital.model.Usuario;
import br.com.elovital.repository.IdosoRepository;
import br.com.elovital.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final IdosoRepository idosoRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       IdosoRepository idosoRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.idosoRepository = idosoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }

        Long idosoId = idosoRepository.findByUsuarioId(usuario.getId())
                .map(Idoso::getId)
                .orElse(null);

        String rawToken = usuario.getId() + ":" + usuario.getEmail() + ":" + UUID.randomUUID();
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));

        return new LoginResponse(
                token,
                "Bearer",
                Instant.now(),
                UsuarioResponse.de(usuario),
                idosoId
        );
    }
}

package com.umbra.backend.service;

import com.umbra.backend.dto.RegistroRequest;
import com.umbra.backend.dto.UsuarioResponse;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo electrónico ya está registrado"
            );
        }

        Usuario usuario = new Usuario(
                request.nombreUsuario().trim(),
                email,
                passwordEncoder.encode(request.password()),
                null,
                "oscuro",
                "USER"
        );

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                guardado.getIdUsuario(),
                guardado.getNombreUsuario(),
                guardado.getEmail(),
                guardado.getRol()
        );
    }
}
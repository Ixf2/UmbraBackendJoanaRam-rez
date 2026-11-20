
package com.umbra.backend.service;

import com.umbra.backend.model.Personaje;
import com.umbra.backend.repository.PersonajeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImagenPersonajeConsultaService {

    private final JdbcTemplate jdbcTemplate;
    private final PersonajeRepository personajeRepository;

    public ImagenPersonajeConsultaService(
            JdbcTemplate jdbcTemplate,
            PersonajeRepository personajeRepository) {

        this.jdbcTemplate = jdbcTemplate;
        this.personajeRepository = personajeRepository;
    }

    @Transactional(readOnly = true)
    public byte[] obtenerMiniatura(
            Integer idPersonaje,
            Authentication authentication) {

        comprobarAcceso(idPersonaje, authentication);

        byte[] imagen = jdbcTemplate.query(
                """
                SELECT miniatura
                FROM imagen_personaje
                WHERE id_personaje = ?
                """,
                rs -> rs.next() ? rs.getBytes("miniatura") : null,
                idPersonaje
        );

        if (imagen == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El personaje no tiene imagen"
            );
        }

        return imagen;
    }

    @Transactional(readOnly = true)
    public byte[] obtenerDetalle(
            Integer idPersonaje,
            Authentication authentication) {

        comprobarAcceso(idPersonaje, authentication);

        byte[] imagen = jdbcTemplate.query(
                """
                SELECT imagen_detalle
                FROM imagen_personaje
                WHERE id_personaje = ?
                """,
                rs -> rs.next() ? rs.getBytes("imagen_detalle") : null,
                idPersonaje
        );

        if (imagen == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El personaje no tiene imagen"
            );
        }

        return imagen;
    }

    private void comprobarAcceso(
            Integer idPersonaje,
            Authentication authentication) {

        Personaje personaje = personajeRepository
                .findById(idPersonaje)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personaje no encontrado"
                ));

        // Los personajes públicos se pueden consultar sin iniciar sesión.
        if ("publico".equals(personaje.getVisibilidad())) {
            return;
        }

        boolean autenticado =
                authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication instanceof AnonymousAuthenticationToken);

        // Un invitado no puede consultar imágenes privadas.
        if (!autenticado) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Imagen no encontrada"
            );
        }

        String email = authentication.getName();

        // El propietario puede consultar su imagen privada.
        if (email.equals(
                personaje.getUsuarioCreador().getEmail())) {
            return;
        }

        // Un usuario con acceso compartido también puede verla.
        boolean compartido = personaje.getUsuariosConAcceso()
                .stream()
                .anyMatch(usuario -> email.equals(usuario.getEmail()));

        if (!compartido) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Imagen no encontrada"
            );
        }
    }
}

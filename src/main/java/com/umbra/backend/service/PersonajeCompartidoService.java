package com.umbra.backend.service;

import com.umbra.backend.dto.UsuarioCompartidoResponse;
import com.umbra.backend.model.Personaje;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.PersonajeRepository;
import com.umbra.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PersonajeCompartidoService {

    private final PersonajeRepository personajeRepository;
    private final UsuarioRepository usuarioRepository;

    public PersonajeCompartidoService(
            PersonajeRepository personajeRepository,
            UsuarioRepository usuarioRepository) {

        this.personajeRepository = personajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario obtenerUsuarioAutenticado(String email) {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.FORBIDDEN,
                                "La identidad autenticada no corresponde a un usuario de UMBRA"
                        )
                );
    }

    private Personaje buscarPersonaje(Integer idPersonaje) {

        return personajeRepository.findById(idPersonaje)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Personaje no encontrado"
                        )
                );
    }

    private void comprobarPropietario(
            Personaje personaje,
            Usuario usuarioAutenticado) {

        Integer idPropietario =
                personaje.getUsuarioCreador().getIdUsuario();

        if (!idPropietario.equals(usuarioAutenticado.getIdUsuario())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso sobre este personaje"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioCompartidoResponse> obtenerUsuariosConAcceso(
            Integer idPersonaje,
            String emailAutenticado) {

        Usuario autenticado = obtenerUsuarioAutenticado(emailAutenticado);

        Personaje personaje = buscarPersonaje(idPersonaje);

        comprobarPropietario(personaje, autenticado);

        return personaje.getUsuariosConAcceso()
                .stream()
                .map(usuario -> new UsuarioCompartidoResponse(
                        usuario.getIdUsuario(),
                        usuario.getNombreUsuario()
                ))
                .toList();
    }

    @Transactional
    public void compartirPersonaje(
            Integer idPersonaje,
            Integer idDestinatario,
            String emailAutenticado) {

        Usuario autenticado = obtenerUsuarioAutenticado(emailAutenticado);

        Personaje personaje = buscarPersonaje(idPersonaje);

        comprobarPropietario(personaje, autenticado);

        Usuario destinatario = usuarioRepository.findById(idDestinatario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario destinatario no encontrado"
                        )
                );

        if (destinatario.getIdUsuario().equals(autenticado.getIdUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes compartir un personaje contigo mismo"
            );
        }

        if (destinatario.getPersonajesCompartidos().contains(personaje)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El personaje ya está compartido con este usuario"
            );
        }

        destinatario.getPersonajesCompartidos().add(personaje);
    }

    @Transactional
    public boolean establecerAcceso(
            Integer idPersonaje,
            Integer idDestinatario,
            String emailAutenticado) {

        Usuario autenticado = obtenerUsuarioAutenticado(emailAutenticado);

        Personaje personaje = buscarPersonaje(idPersonaje);

        comprobarPropietario(personaje, autenticado);

        Usuario destinatario = usuarioRepository.findById(idDestinatario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario destinatario no encontrado"
                        )
                );

        if (destinatario.getIdUsuario().equals(autenticado.getIdUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes compartir un personaje contigo mismo"
            );
        }

        if (destinatario.getPersonajesCompartidos().contains(personaje)) {
            return false;
        }

        destinatario.getPersonajesCompartidos().add(personaje);

        return true;
    }

    @Transactional
    public void quitarAcceso(
            Integer idPersonaje,
            Integer idDestinatario,
            String emailAutenticado) {

        Usuario autenticado = obtenerUsuarioAutenticado(emailAutenticado);

        Personaje personaje = buscarPersonaje(idPersonaje);

        comprobarPropietario(personaje, autenticado);

        Usuario destinatario = usuarioRepository.findById(idDestinatario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario destinatario no encontrado"
                        )
                );

        if (!destinatario.getPersonajesCompartidos().remove(personaje)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe esa compartición"
            );
        }
    }
}
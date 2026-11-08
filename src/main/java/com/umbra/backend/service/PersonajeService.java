package com.umbra.backend.service;
import com.umbra.backend.dto.PersonajeResponse;
import com.umbra.backend.model.Personaje;
import com.umbra.backend.repository.PersonajeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonajeService {

    private final PersonajeRepository personajeRepository;

    public PersonajeService(PersonajeRepository personajeRepository) {
        this.personajeRepository = personajeRepository;
    }

    @Transactional(readOnly = true)
    public List<Personaje> obtenerTodos() {
        return personajeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Personaje obtenerPorId(Integer id) {
        return personajeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un personaje con ID: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Personaje> obtenerPublicos() {
        return personajeRepository.findByVisibilidad("publico");
    }

    @Transactional(readOnly = true)
    public List<Personaje> obtenerPorUsuario(Integer idUsuario) {
        return personajeRepository.findByUsuarioCreador_IdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Personaje> obtenerCompartidosConUsuario(Integer idUsuario) {
        return personajeRepository.findByUsuariosConAcceso_IdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<PersonajeResponse> obtenerPublicosDTO() {

        return personajeRepository.findByVisibilidad("publico")
                .stream()
                .map(personaje -> new PersonajeResponse(
                        personaje.getIdPersonaje(),
                        personaje.getNombre(),
                        personaje.getHistoria(),
                        personaje.getPersonalidad(),
                        personaje.getImagen(),
                        personaje.getFuerza(),
                        personaje.getDestreza(),
                        personaje.getInteligencia(),
                        personaje.getCarisma(),
                        personaje.getVisibilidad()
                ))
                .toList();
    }
}
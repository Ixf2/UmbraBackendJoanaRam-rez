package com.umbra.backend.service;
import com.umbra.backend.dto.PersonajeResponse;
import com.umbra.backend.model.Personaje;
import com.umbra.backend.repository.PersonajeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umbra.backend.dto.PersonajeRequest;
import com.umbra.backend.model.Categoria;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.CategoriaRepository;
import com.umbra.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PersonajeService {

    private final PersonajeRepository personajeRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;


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
    public PersonajeService(
            PersonajeRepository personajeRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.personajeRepository = personajeRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
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

    @Transactional
    public PersonajeResponse crearPersonaje(
            PersonajeRequest request,
            String emailAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"
                ));

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));

        Personaje personaje = new Personaje(
                request.nombre(),
                request.historia(),
                request.personalidad(),
                request.imagen(),
                request.fuerza(),
                request.destreza(),
                request.inteligencia(),
                request.carisma(),
                request.visibilidad(),
                usuario,
                categoria
        );

        Personaje guardado = personajeRepository.save(personaje);

        return new PersonajeResponse(
                guardado.getIdPersonaje(),
                guardado.getNombre(),
                guardado.getHistoria(),
                guardado.getPersonalidad(),
                guardado.getImagen(),
                guardado.getFuerza(),
                guardado.getDestreza(),
                guardado.getInteligencia(),
                guardado.getCarisma(),
                guardado.getVisibilidad()
        );
    }
    @Transactional(readOnly = true)
    public PersonajeResponse obtenerPublicoPorId(Integer id) {

        Personaje personaje = personajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personaje no encontrado"
                ));

        if (!"publico".equals(personaje.getVisibilidad())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Personaje no encontrado"
            );
        }

        return new PersonajeResponse(
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
        );
    }
}
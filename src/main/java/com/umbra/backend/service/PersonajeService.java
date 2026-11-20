
package com.umbra.backend.service;

import com.umbra.backend.dto.PersonajeRequest;
import com.umbra.backend.dto.PersonajeResponse;
import com.umbra.backend.model.Categoria;
import com.umbra.backend.model.Personaje;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.CategoriaRepository;
import com.umbra.backend.repository.PersonajeRepository;
import com.umbra.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.List;

@Service
public class PersonajeService {

    private final PersonajeRepository personajeRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JsonMapper jsonMapper;

    public PersonajeService(
            PersonajeRepository personajeRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            JsonMapper jsonMapper) {

        this.personajeRepository = personajeRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional(readOnly = true)
    public List<Personaje> obtenerTodos() {
        return personajeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Personaje obtenerPorId(Integer id) {
        return personajeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personaje no encontrado"
                ));
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
                .map(this::convertirDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PersonajeResponse obtenerPublicoPorId(Integer id) {

        Personaje personaje = obtenerPorId(id);

        if (!"publico".equals(personaje.getVisibilidad())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Personaje no encontrado"
            );
        }

        return convertirDTO(personaje);
    }

    @Transactional(readOnly = true)
    public List<PersonajeResponse> obtenerMisPersonajes(
            String emailAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"
                ));

        return personajeRepository
                .findByUsuarioCreador_IdUsuario(usuario.getIdUsuario())
                .stream()
                .map(this::convertirDTO)
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

        Categoria categoria = categoriaRepository
                .findById(request.idCategoria())
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

        personaje.setClase(request.clase());
        personaje.setRaza(request.raza());
        personaje.setNivel(request.nivel());
        personaje.setOrigen(request.origen());
        personaje.setRasgos(convertirRasgosAJson(request.rasgos()));
        personaje.setRetrato(request.retrato());
        personaje.setFavorito(request.favorito());

        Personaje guardado = personajeRepository.save(personaje);

        return convertirDTO(guardado);
    }

    private Personaje comprobarPropietario(
            Integer idPersonaje,
            String emailAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"
                ));

        Personaje personaje = personajeRepository.findById(idPersonaje)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personaje no encontrado"
                ));

        if (!personaje.getUsuarioCreador().getIdUsuario()
                .equals(usuario.getIdUsuario())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No eres el propietario de este personaje"
            );
        }

        return personaje;
    }

    private String convertirRasgosAJson(List<String> rasgos) {
        return jsonMapper.writeValueAsString(
                rasgos != null ? rasgos : List.of()
        );
    }

    private List<String> convertirJsonARasgos(String rasgosJson) {

        if (rasgosJson == null || rasgosJson.isBlank()) {
            return List.of();
        }

        try {
            return jsonMapper.readValue(
                    rasgosJson,
                    new TypeReference<List<String>>() {}
            );
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Los rasgos almacenados no contienen un JSON válido",
                    e
            );
        }
    }

    private PersonajeResponse convertirDTO(Personaje personaje) {

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
                personaje.getVisibilidad(),
                personaje.getCategoria().getIdCategoria(),
                personaje.getClase(),
                personaje.getRaza(),
                personaje.getNivel(),
                personaje.getOrigen(),
                convertirJsonARasgos(personaje.getRasgos()),
                personaje.getRetrato(),
                personaje.getFavorito()
        );
    }

    @Transactional
    public PersonajeResponse actualizarPersonaje(
            Integer idPersonaje,
            PersonajeRequest request,
            String emailAutenticado) {

        Personaje personaje = comprobarPropietario(
                idPersonaje,
                emailAutenticado
        );

        Categoria categoria = categoriaRepository
                .findById(request.idCategoria())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));

        personaje.setNombre(request.nombre());
        personaje.setHistoria(request.historia());
        personaje.setPersonalidad(request.personalidad());
        personaje.setImagen(request.imagen());
        personaje.setFuerza(request.fuerza());
        personaje.setDestreza(request.destreza());
        personaje.setInteligencia(request.inteligencia());
        personaje.setCarisma(request.carisma());
        personaje.setVisibilidad(request.visibilidad());
        personaje.setCategoria(categoria);

        personaje.setClase(request.clase());
        personaje.setRaza(request.raza());
        personaje.setNivel(request.nivel());
        personaje.setOrigen(request.origen());
        personaje.setRasgos(convertirRasgosAJson(request.rasgos()));
        personaje.setRetrato(request.retrato());
        personaje.setFavorito(request.favorito());

        return convertirDTO(personaje);
    }

    @Transactional
    public void eliminarPersonaje(
            Integer idPersonaje,
            String emailAutenticado) {

        Personaje personaje = comprobarPropietario(
                idPersonaje,
                emailAutenticado
        );

        for (Usuario usuario :
                new HashSet<>(personaje.getUsuariosConAcceso())) {

            usuario.getPersonajesCompartidos().remove(personaje);
        }

        personaje.getUsuariosConAcceso().clear();

        usuarioRepository.flush();

        personajeRepository.delete(personaje);
    }
}

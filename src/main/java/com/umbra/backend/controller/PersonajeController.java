package com.umbra.backend.controller;

import com.umbra.backend.dto.PersonajeRequest;
import com.umbra.backend.dto.PersonajeResponse;
import com.umbra.backend.service.PersonajeService;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personajes")
public class PersonajeController {

    private final PersonajeService personajeService;

    public PersonajeController(PersonajeService personajeService) {
        this.personajeService = personajeService;
    }

    @GetMapping("/publicos")
    public List<PersonajeResponse> obtenerPublicos() {
        return personajeService.obtenerPublicosDTO();
    }
    @GetMapping("/{id}")
    public PersonajeResponse obtenerPorId(@PathVariable Integer id) {
        return personajeService.obtenerPublicoPorId(id);
    }
    @PostMapping
    public ResponseEntity<PersonajeResponse> crearPersonaje(
            @Valid @RequestBody PersonajeRequest request,
            Authentication authentication) {

        PersonajeResponse personaje = personajeService.crearPersonaje(
                request,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(personaje);
    }
}
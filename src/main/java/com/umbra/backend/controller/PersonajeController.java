package com.umbra.backend.controller;

import com.umbra.backend.dto.PersonajeResponse;
import com.umbra.backend.service.PersonajeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
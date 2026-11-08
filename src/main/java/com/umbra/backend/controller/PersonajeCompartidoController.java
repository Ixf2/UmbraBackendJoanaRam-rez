package com.umbra.backend.controller;
import com.umbra.backend.dto.UsuarioCompartidoResponse;
import com.umbra.backend.dto.CompartirPersonajeRequest;
import com.umbra.backend.dto.UsuarioCompartidoResponse;
import com.umbra.backend.service.PersonajeCompartidoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/personajes/{idPersonaje}/compartidos")
public class PersonajeCompartidoController {

    private final PersonajeCompartidoService compartidoService;

    public PersonajeCompartidoController(
            PersonajeCompartidoService compartidoService) {

        this.compartidoService = compartidoService;
    }

    @GetMapping
    public List<UsuarioCompartidoResponse> obtenerCompartidos(
            @PathVariable @Positive Integer idPersonaje,
            Authentication authentication) {

        return compartidoService.obtenerUsuariosConAcceso(
                idPersonaje,
                authentication.getName()
        );
    }

    @PostMapping
    public ResponseEntity<Void> compartirPersonaje(
            @PathVariable @Positive Integer idPersonaje,
            @Valid @RequestBody CompartirPersonajeRequest request,
            Authentication authentication) {

        compartidoService.compartirPersonaje(
                idPersonaje,
                request.idUsuario(),
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<Void> establecerAcceso(
            @PathVariable @Positive Integer idPersonaje,
            @PathVariable @Positive Integer idUsuario,
            Authentication authentication) {

        boolean creado = compartidoService.establecerAcceso(
                idPersonaje,
                idUsuario,
                authentication.getName()
        );

        if (creado) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> quitarAcceso(
            @PathVariable @Positive Integer idPersonaje,
            @PathVariable @Positive Integer idUsuario,
            Authentication authentication) {

        compartidoService.quitarAcceso(
                idPersonaje,
                idUsuario,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
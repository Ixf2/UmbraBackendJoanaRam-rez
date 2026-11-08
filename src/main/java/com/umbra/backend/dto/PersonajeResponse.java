package com.umbra.backend.dto;

public record PersonajeResponse(
        Integer idPersonaje,
        String nombre,
        String historia,
        String personalidad,
        String imagen,
        Integer fuerza,
        Integer destreza,
        Integer inteligencia,
        Integer carisma,
        String visibilidad
) {
}
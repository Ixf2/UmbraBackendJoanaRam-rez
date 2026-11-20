
package com.umbra.backend.dto;

import java.util.List;

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
        String visibilidad,
        Integer idCategoria,
        String clase,
        String raza,
        Integer nivel,
        String origen,
        List<String> rasgos,
        Integer retrato,
        Boolean favorito

) {
}

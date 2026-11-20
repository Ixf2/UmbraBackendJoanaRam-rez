
package com.umbra.backend.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record PersonajeRequest(

        @NotBlank
        @Size(max = 50)
        String nombre,

        @NotBlank
        String historia,

        @NotBlank
        String personalidad,

        @Size(max = 100)
        String imagen,

        @NotNull
        @Min(1)
        @Max(20)
        Integer fuerza,

        @NotNull
        @Min(1)
        @Max(20)
        Integer destreza,

        @NotNull
        @Min(1)
        @Max(20)
        Integer inteligencia,

        @NotNull
        @Min(1)
        @Max(20)
        Integer carisma,

        @NotBlank
        @Pattern(regexp = "publico|privado")
        String visibilidad,

        @NotNull
        @Positive
        Integer idCategoria,

        @Size(max = 50)
        String clase,

        @Size(max = 50)
        String raza,

        @NotNull
        @Min(1)
        @Max(20)
        Integer nivel,

        @Size(max = 100)
        String origen,

        @Size(max = 6)
        List<@NotBlank @Size(max = 40) String> rasgos,

        @NotNull
        @Min(0)
        @Max(5)
        Integer retrato,

        @NotNull
        Boolean favorito

) {
}

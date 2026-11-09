package com.umbra.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        @Min(1)
        Integer idCategoria

) {}
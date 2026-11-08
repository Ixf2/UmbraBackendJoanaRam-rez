package com.umbra.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompartirPersonajeRequest(

        @NotNull(message = "El usuario destinatario es obligatorio")
        @Positive(message = "El ID debe ser positivo")
        Integer idUsuario

) {
}
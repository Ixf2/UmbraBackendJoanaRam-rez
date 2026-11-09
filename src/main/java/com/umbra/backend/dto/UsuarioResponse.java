package com.umbra.backend.dto;

public record UsuarioResponse(
        Integer idUsuario,
        String nombreUsuario,
        String email,
        String rol
) {}
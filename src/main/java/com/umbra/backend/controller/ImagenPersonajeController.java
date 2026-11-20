
package com.umbra.backend.controller;

import com.umbra.backend.service.ImagenPersonajeService;
import com.umbra.backend.service.ImagenPersonajeConsultaService;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
@RequestMapping("/api/personajes/{idPersonaje}/imagen")
public class ImagenPersonajeController {

    private final ImagenPersonajeService imagenService;
    private final ImagenPersonajeConsultaService consultaService;

    public ImagenPersonajeController(
            ImagenPersonajeService imagenService,
            ImagenPersonajeConsultaService consultaService) {

        this.imagenService = imagenService;
        this.consultaService = consultaService;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> subirImagen(
            @PathVariable Integer idPersonaje,
            @RequestParam("archivo") MultipartFile archivo,
            Authentication authentication) {

        imagenService.guardarImagen(
                idPersonaje,
                archivo,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(
            value = "/miniatura",
            produces = "image/webp"
    )
    public ResponseEntity<byte[]> obtenerMiniatura(
            @PathVariable Integer idPersonaje,
            Authentication authentication) {

        byte[] imagen = consultaService.obtenerMiniatura(
                idPersonaje,
                authentication
        );

        return construirRespuesta(imagen, idPersonaje);
    }

    @GetMapping(
            value = "/detalle",
            produces = "image/webp"
    )
    public ResponseEntity<byte[]> obtenerDetalle(
            @PathVariable Integer idPersonaje,
            Authentication authentication) {

        byte[] imagen = consultaService.obtenerDetalle(
                idPersonaje,
                authentication
        );

        return construirRespuesta(imagen, idPersonaje);
    }

    private ResponseEntity<byte[]> construirRespuesta(
            byte[] imagen,
            Integer idPersonaje) {

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/webp"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"personaje-"
                                + idPersonaje + ".webp\""
                )
                .cacheControl(CacheControl.noStore())
                .contentLength(imagen.length)
                .body(imagen);
    }
}


package com.umbra.backend.service;

import com.umbra.backend.model.ImagenPersonaje;
import com.umbra.backend.model.Personaje;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.ImagenPersonajeRepository;
import com.umbra.backend.repository.PersonajeRepository;
import com.umbra.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

@Service
public class ImagenPersonajeService {

    private static final long MAX_ARCHIVO = 5L * 1024 * 1024;
    private static final long MAX_PIXELES = 12_000_000L;
    private static final int MAX_DIMENSION_ORIGINAL = 5000;

    private static final int MINI_ANCHO = 256;
    private static final int MINI_ALTO = 320;
    private static final int DETALLE_ANCHO = 768;
    private static final int DETALLE_ALTO = 1024;

    private static final int MAX_MINI_BYTES = 50 * 1024;
    private static final int MAX_DETALLE_BYTES = 250 * 1024;

    private final ImagenPersonajeRepository imagenRepository;
    private final PersonajeRepository personajeRepository;
    private final UsuarioRepository usuarioRepository;

    public ImagenPersonajeService(
            ImagenPersonajeRepository imagenRepository,
            PersonajeRepository personajeRepository,
            UsuarioRepository usuarioRepository) {

        this.imagenRepository = imagenRepository;
        this.personajeRepository = personajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void guardarImagen(
            Integer idPersonaje,
            MultipartFile archivo,
            String emailAutenticado) {

        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"
                ));

        Personaje personaje = personajeRepository.findById(idPersonaje)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Personaje no encontrado"
                ));

        if (!personaje.getUsuarioCreador().getIdUsuario()
                .equals(usuario.getIdUsuario())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes modificar este personaje"
            );
        }

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes seleccionar una imagen"
            );
        }

        if (archivo.getSize() > MAX_ARCHIVO) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "La imagen supera el límite de 5 MB"
            );
        }

        try {
            BufferedImage original = leerImagen(archivo);

            BufferedImage miniatura = redimensionar(
                    original, MINI_ANCHO, MINI_ALTO
            );

            BufferedImage detalle = redimensionar(
                    original, DETALLE_ANCHO, DETALLE_ALTO
            );

            byte[] miniaturaWebp = comprimirWebp(
                    miniatura, MAX_MINI_BYTES
            );

            byte[] detalleWebp = comprimirWebp(
                    detalle, MAX_DETALLE_BYTES
            );

            ImagenPersonaje imagen = imagenRepository
                    .findByPersonaje_IdPersonaje(idPersonaje)
                    .orElseGet(() -> {
                        ImagenPersonaje nueva = new ImagenPersonaje();
                        nueva.setPersonaje(personaje);
                        return nueva;
                    });

            imagen.setMiniatura(miniaturaWebp);
            imagen.setImagenDetalle(detalleWebp);

            imagenRepository.save(imagen);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo procesar la imagen",
                    e
            );
        }
    }

    private BufferedImage leerImagen(MultipartFile archivo)
            throws IOException {

        try (ByteArrayInputStream entrada =
                     new ByteArrayInputStream(archivo.getBytes());
             ImageInputStream flujo =
                     ImageIO.createImageInputStream(entrada)) {

            if (flujo == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Archivo de imagen no válido"
                );
            }

            Iterator<ImageReader> lectores =
                    ImageIO.getImageReaders(flujo);

            if (!lectores.hasNext()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Formato de imagen no compatible"
                );
            }

            ImageReader lector = lectores.next();

            try {
                lector.setInput(flujo);

                String formato = lector.getFormatName()
                        .toLowerCase(Locale.ROOT);

                if (!formato.equals("png")
                        && !formato.equals("jpeg")
                        && !formato.equals("jpg")
                        && !formato.equals("webp")) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Solo se permiten PNG, JPG y WebP"
                    );
                }

                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);

                if (ancho <= 0 || alto <= 0
                        || ancho > MAX_DIMENSION_ORIGINAL
                        || alto > MAX_DIMENSION_ORIGINAL
                        || (long) ancho * alto > MAX_PIXELES) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "La resolución de la imagen es demasiado grande"
                    );
                }

                BufferedImage imagen = lector.read(0);

                if (imagen == null) {
                    throw new IOException("No se pudo leer la imagen");
                }

                return imagen;

            } finally {
                lector.dispose();
            }
        }
    }

    private BufferedImage redimensionar(
            BufferedImage original,
            int maxAncho,
            int maxAlto) {

        double escala = Math.min(
                1.0,
                Math.min(
                        (double) maxAncho / original.getWidth(),
                        (double) maxAlto / original.getHeight()
                )
        );

        int ancho = Math.max(
                1,
                (int) Math.round(original.getWidth() * escala)
        );

        int alto = Math.max(
                1,
                (int) Math.round(original.getHeight() * escala)
        );

        BufferedImage resultado = new BufferedImage(
                ancho,
                alto,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graficos = resultado.createGraphics();

        try {
            graficos.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graficos.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            graficos.drawImage(
                    original,
                    0,
                    0,
                    ancho,
                    alto,
                    null
            );
        } finally {
            graficos.dispose();
        }

        return resultado;
    }

    private byte[] comprimirWebp(
            BufferedImage imagen,
            int maxBytes) throws IOException {

        float[] calidades = {0.80f, 0.65f, 0.50f, 0.35f};

        byte[] resultado = null;

        for (float calidad : calidades) {

            resultado = escribirWebp(imagen, calidad);

            if (resultado.length <= maxBytes) {
                return resultado;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "La imagen no se pudo optimizar al tamaño permitido"
        );
    }

    private byte[] escribirWebp(
            BufferedImage imagen,
            float calidad) throws IOException {

        Iterator<ImageWriter> escritores =
                ImageIO.getImageWritersByFormatName("webp");

        if (!escritores.hasNext()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No hay un codificador WebP disponible"
            );
        }

        ImageWriter escritor = escritores.next();

        try (ByteArrayOutputStream salida =
                     new ByteArrayOutputStream();
             ImageOutputStream flujo =
                     ImageIO.createImageOutputStream(salida)) {

            escritor.setOutput(flujo);

            ImageWriteParam parametros =
                    escritor.getDefaultWriteParam();

            if (parametros.canWriteCompressed()) {
                parametros.setCompressionMode(
                        ImageWriteParam.MODE_EXPLICIT
                );

                String[] tipos = parametros.getCompressionTypes();

                if (tipos != null && tipos.length > 0) {
                    String seleccionado = tipos[0];

                    for (String tipo : tipos) {
                        if (tipo.toLowerCase(Locale.ROOT)
                                .contains("lossy")) {
                            seleccionado = tipo;
                            break;
                        }
                    }

                    parametros.setCompressionType(seleccionado);
                }

                parametros.setCompressionQuality(calidad);
            }

            escritor.write(
                    null,
                    new IIOImage(imagen, null, null),
                    parametros
            );

            flujo.flush();

            byte[] bytes = salida.toByteArray();

            if (bytes.length < 12
                    || bytes[0] != 'R'
                    || bytes[1] != 'I'
                    || bytes[2] != 'F'
                    || bytes[3] != 'F'
                    || bytes[8] != 'W'
                    || bytes[9] != 'E'
                    || bytes[10] != 'B'
                    || bytes[11] != 'P') {

                throw new IOException(
                        "El codificador no produjo un WebP válido"
                );
            }

            return bytes;

        } finally {
            escritor.dispose();
        }
    }
}

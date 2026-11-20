
package com.umbra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "imagen_personaje")
public class ImagenPersonaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer idImagen;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_personaje",
            nullable = false,
            unique = true
    )
    private Personaje personaje;

    @Lob
    @Column(
            name = "miniatura",
            nullable = false,
            columnDefinition = "MEDIUMBLOB"
    )
    private byte[] miniatura;

    @Lob
    @Column(
            name = "imagen_detalle",
            nullable = false,
            columnDefinition = "MEDIUMBLOB"
    )
    private byte[] imagenDetalle;

    @Column(
            name = "fecha_actualizacion",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime fechaActualizacion;

    public ImagenPersonaje() {
    }

    public ImagenPersonaje(
            Personaje personaje,
            byte[] miniatura,
            byte[] imagenDetalle) {

        this.personaje = personaje;
        this.miniatura = miniatura;
        this.imagenDetalle = imagenDetalle;
    }

    public Integer getIdImagen() {
        return idImagen;
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public byte[] getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(byte[] miniatura) {
        this.miniatura = miniatura;
    }

    public byte[] getImagenDetalle() {
        return imagenDetalle;
    }

    public void setImagenDetalle(byte[] imagenDetalle) {
        this.imagenDetalle = imagenDetalle;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

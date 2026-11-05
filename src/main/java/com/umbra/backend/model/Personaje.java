
package com.umbra.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personaje")
public class Personaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personaje")
    private Integer idPersonaje;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "historia", nullable = false, columnDefinition = "TEXT")
    private String historia;

    @Column(name = "personalidad", nullable = false, columnDefinition = "TEXT")
    private String personalidad;

    @Column(name = "imagen", length = 100)
    private String imagen;

    @Column(name = "fuerza", nullable = false)
    private Integer fuerza;

    @Column(name = "destreza", nullable = false)
    private Integer destreza;

    @Column(name = "inteligencia", nullable = false)
    private Integer inteligencia;

    @Column(name = "carisma", nullable = false)
    private Integer carisma;

    @Column(name = "visibilidad", nullable = false, length = 50)
    private String visibilidad = "privado";

    @Column(name = "fecha_creacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_creador", nullable = false)
    private Usuario usuarioCreador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    public Personaje(){}

    public Personaje(String nombre, String historia, String personalidad, String imagen, Integer fuerza, Integer destreza, Integer inteligencia, Integer carisma, String visibilidad, Usuario usuarioCreador, Categoria categoria){
        this.nombre = nombre;
        this.historia = historia;
        this.personalidad = personalidad;
        this.imagen = imagen;
        this.fuerza = fuerza;
        this.destreza = destreza;
        this.inteligencia = inteligencia;
        this.carisma = carisma;
        this.visibilidad = visibilidad != null ? visibilidad : "privado";
        this.usuarioCreador = usuarioCreador;
        this.categoria = categoria;
    }

    public Integer getIdPersonaje() {
        return idPersonaje;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getPersonalidad() {
        return personalidad;
    }

    public void setPersonalidad(String personalidad) {
        this.personalidad = personalidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImage(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFuerza() {
        return fuerza;
    }

    public void setFuerza(Integer fuerza) {
        this.fuerza = fuerza;
    }

    public Integer getDestreza() {
        return destreza;
    }

    public void setDestreza(Integer destreza) {
        this.destreza = destreza;
    }

    public Integer getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(Integer inteligencia) {
        this.inteligencia = inteligencia;
    }

    public Integer getCarisma() {
        return carisma;
    }

    public void setCarisma(Integer carisma) {
        this.carisma = carisma;
    }

    public String getVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(String visibilidad) {
        this.visibilidad = visibilidad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }


    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}

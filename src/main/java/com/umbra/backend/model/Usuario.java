package com.umbra.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "avatar", length = 200)
    private String avatar;

    @Column(name = "fecha_registro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "tema", length = 50)
    private String tema;

    @OneToMany(mappedBy = "usuarioCreador")
    private List<Personaje> personajes = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Configuracion configuracion;

    @OneToMany(mappedBy = "usuario")
    private List<Notificacion> notificaciones = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "personaje_compartido", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_personaje"))
    private Set<Personaje> personajesCompartidos = new HashSet<>();


    public Usuario() {
    }

    public Usuario(String nombreUsuario, String email,
                   String password, String avatar, String tema) {

        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.tema = tema;
    }

    public Set<Personaje> getPersonajesCompartidos() {
        return personajesCompartidos;
    }

    public void setPersonajesCompartidos(Set<Personaje> personajesCompartidos) {
        this.personajesCompartidos = personajesCompartidos;
    }
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public List<Personaje> getPersonajes() {
        return personajes;
    }

    public void setPersonajes(List<Personaje> personajes) {
        this.personajes = personajes;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }




}

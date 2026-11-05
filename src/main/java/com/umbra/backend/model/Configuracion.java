
package com.umbra.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion")
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @Column(name = "idioma", nullable = false, length = 50)
    private String idioma;

    @Column(name = "notificaciones_activadas", nullable = false)
    private Boolean notificacionesActivadas = true;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    public Configuracion(){}
    public Configuracion(String idioma, Boolean notificacionesActivadas, Usuario usuario){
        this.idioma = idioma;
        this.notificacionesActivadas = notificacionesActivadas != null ? notificacionesActivadas : true;
        this.usuario = usuario;
    }

    public Integer getIdConfiguracion() {
        return idConfiguracion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Boolean getNotificacionesActivadas() {
        return notificacionesActivadas;
    }

    public void setNotificacionesActivadas(Boolean notificacionesActivadas) {
        this.notificacionesActivadas = notificacionesActivadas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

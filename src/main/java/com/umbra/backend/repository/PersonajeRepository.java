package com.umbra.backend.repository;

import com.umbra.backend.model.Personaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonajeRepository extends JpaRepository<Personaje, Integer> {

    List<Personaje> findByVisibilidad(String visibilidad);

    List<Personaje> findByUsuarioCreador_IdUsuario(Integer idUsuario);

    List<Personaje> findByUsuariosConAcceso_IdUsuario(Integer idUsuario);
}
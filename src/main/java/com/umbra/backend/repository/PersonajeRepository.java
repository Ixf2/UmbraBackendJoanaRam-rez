package com.umbra.backend.repository;

import com.umbra.backend.model.Personaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonajeRepository extends JpaRepository<Personaje, Integer> {
}
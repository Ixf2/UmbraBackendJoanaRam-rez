
package com.umbra.backend.repository;

import com.umbra.backend.model.ImagenPersonaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagenPersonajeRepository
        extends JpaRepository<ImagenPersonaje, Integer> {

    Optional<ImagenPersonaje> findByPersonaje_IdPersonaje(
            Integer idPersonaje
    );
}

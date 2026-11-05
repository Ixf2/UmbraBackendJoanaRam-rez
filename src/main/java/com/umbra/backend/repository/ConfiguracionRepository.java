package com.umbra.backend.repository;

import com.umbra.backend.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {
}
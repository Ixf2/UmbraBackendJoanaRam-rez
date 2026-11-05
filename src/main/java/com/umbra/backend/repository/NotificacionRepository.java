package com.umbra.backend.repository;

import com.umbra.backend.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
}
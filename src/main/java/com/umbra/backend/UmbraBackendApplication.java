package com.umbra.backend;

import com.umbra.backend.model.Personaje;
import com.umbra.backend.model.Usuario;
import com.umbra.backend.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class UmbraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmbraBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner probarCompartidos(
            PruebaCompartidosService servicio) {

        return args -> servicio.comprobarCompartidos();
    }

    @Service
    public static class PruebaCompartidosService {

        private final UsuarioRepository usuarioRepository;

        public PruebaCompartidosService(UsuarioRepository usuarioRepository) {
            this.usuarioRepository = usuarioRepository;
        }

        @Transactional(readOnly = true)
        public void comprobarCompartidos() {

            Usuario alex = usuarioRepository.findById(2)
                    .orElseThrow();

            System.out.println("Personajes compartidos con Alex:");

            for (Personaje personaje : alex.getPersonajesCompartidos()) {
                System.out.println(personaje.getNombre());
            }
        }
    }
}
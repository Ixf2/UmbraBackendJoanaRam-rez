package com.umbra.backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

public class GeneradorPassword {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Contraseña del administrador: ");

        String password = scanner.nextLine();

        String hash = encoder.encode(password);

        System.out.println("Hash BCrypt: " + hash);
    }
}
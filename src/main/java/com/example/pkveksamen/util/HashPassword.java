package com.example.pkveksamen.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashPassword {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HashPassword <password>");
            System.exit(1);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(args[0]);
        System.out.println(hashedPassword);
    }
}

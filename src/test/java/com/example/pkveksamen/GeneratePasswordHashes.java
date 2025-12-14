package com.example.pkveksamen;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminHash = encoder.encode("admin123");
        String devHash = encoder.encode("dev123");

        System.out.println("admin123: " + adminHash);
        System.out.println("dev123: " + devHash);
    }
}


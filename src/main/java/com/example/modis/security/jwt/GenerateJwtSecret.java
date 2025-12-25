package com.example.modis.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        var key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println(secret);
    }
}

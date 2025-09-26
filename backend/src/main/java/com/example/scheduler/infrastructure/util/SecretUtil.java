package com.example.scheduler.infrastructure.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;


public class SecretUtil {

    public static void main(String[] args) {
        System.out.println(generateSecretKey());
        System.out.println(generateSecretKey());
    }

    /**
     * Метод генерирует закодированный случайный ключ,
     * который можно использовать для подписания токенов
     */
    private static String generateSecretKey() {
        int byteLength = 64;
        SecretKey secretKey = Keys
                .builder(Keys.hmacShaKeyFor(new SecureRandom().generateSeed(byteLength)))
                .build();
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    public static SecretKey hmacShaKeyFor(String secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}

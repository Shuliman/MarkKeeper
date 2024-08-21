package com.example.fulbrincjava.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class PropertyLoadingTest {

    @Autowired
    private Environment environment;

    @Test
    public void testPropertyLoading() {
        String secretKey = environment.getProperty("security.jwt.secret-key");
        String jwtExpirationTime = environment.getProperty("security.jwt.expiration-time");
        System.out.println("Secret Key: " + secretKey);
        System.out.println("JWT Expiration Time: " + jwtExpirationTime);

        assertNotNull(secretKey, "Secret Key should not be null");
        assertNotNull(jwtExpirationTime, "JWT Expiration Time should not be null");
    }
}


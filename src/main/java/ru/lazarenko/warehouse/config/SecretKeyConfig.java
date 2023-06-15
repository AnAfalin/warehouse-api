package ru.lazarenko.warehouse.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class SecretKeyConfig {

    @Bean
    public SecretKey secretKey(JwtConfig jwtConfig) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtConfig.getSecret()));
    }
}

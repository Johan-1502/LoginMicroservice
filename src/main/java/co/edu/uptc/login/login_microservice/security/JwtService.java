package co.edu.uptc.login.login_microservice.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Llave secreta (debes moverla a application.properties)
    private final SecretKey secretKey = Keys.hmacShaKeyFor("MiClaveSecretaSuperLargaParaJWT1234567890".getBytes());

    public String generateToken(String userId) {
        long expirationTime = 1000 * 60 * 60 * 24;

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}

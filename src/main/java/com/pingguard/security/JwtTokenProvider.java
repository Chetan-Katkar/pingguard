package com.pingguard.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // A secret password used to digitally sign the tokens.
    // We hardcode it here for now, but in Week 4 we will move this to a hidden .env file!
    private final String jwtSecret = "ThisIsAMassiveSecretKeyThatIsAtLeast32BytesLongForSecurity";

    // Tokens expire after 24 hours (in milliseconds)
    private final long jwtExpirationDate = 86400000;

    // Helper method to convert our String secret into a cryptographic Key object
    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. GENERATE TOKEN: The user logged in successfully, print them a VIP pass!
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    // 2. READ TOKEN: Look at the VIP pass and read the email written on it
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 3. VALIDATE TOKEN: Check if the VIP pass is fake, expired, or tampered with
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
}

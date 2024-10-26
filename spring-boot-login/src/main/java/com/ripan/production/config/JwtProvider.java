package com.ripan.production.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;


public class JwtProvider {

    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET_KEY.getBytes());

    public static String generateToken(Authentication authentication){
        return Jwts.builder()
                .setIssuer("ripan baidya")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+8640000))
                .claim("email", authentication.getName())
                .signWith(key)
                .compact();
    }

    public static String getEmailFromJwtToken(String jwt) {
        if(jwt.startsWith("Bearer ")) jwt.substring(7).trim();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(jwt)
                .getBody();
        return String.valueOf(claims.get("email"));
    }
}

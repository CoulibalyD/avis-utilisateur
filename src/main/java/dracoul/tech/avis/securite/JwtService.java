package dracoul.tech.avis.securite;

import dracoul.tech.avis.entite.Utilisateur;
import dracoul.tech.avis.service.UtilisateurService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;


@AllArgsConstructor
@Service
public class JwtService {
    private final String ENCRYPTION_KEY = "4527aafcd5ca9013e42b4ee99ce02f890682dee9f5e5b604b2fbe5316807667c";
    private UtilisateurService utilisateurService;

    public Map<String, String> generate(String email){
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        return this.generateJwt(utilisateur);
    }

    public String extractEmail (String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token) {
        Date expirationDate = this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
         Claims claims = getAllClaims(token);
         return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())  // Utilisation de `parserBuilder()` pour une compatibilité accrue
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;

        Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                 Claims.EXPIRATION, new Date(expirationTime),
                 Claims.SUBJECT, utilisateur.getEmail()
                );
       final String bearer = builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer", bearer);
    }


    private Key getKey() {
       final byte[] decoder =Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }


}

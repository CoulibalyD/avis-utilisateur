package dracoul.tech.avis.securite;

import dracoul.tech.avis.entite.Utilisateur;
import dracoul.tech.avis.service.UtilisateurService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;


@AllArgsConstructor
@Service
public class JwtService {
    private final String ENCRYPTION_KEY = "6a070393aaea24b61044e70b73660ee3602b30e0abcc7e3981357c3c3fda99b0";
    private UtilisateurService utilisateurService;
    public Map<String, String> generate(String email){
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        return this.generateJwt(utilisateur);
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {

        Map<String, String> claims = Map.of(
                "nom", utilisateur.getNom(),
                "email", utilisateur.getEmail()
        );
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;
       final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.ES512)
                .compact();
        return Map.of("token", bearer);
    }

    private Key getKey() {
        byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

}

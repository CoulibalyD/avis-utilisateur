package dracoul.tech.avis.security;

import dracoul.tech.avis.entity.Jwt;
import dracoul.tech.avis.entity.RefreshToken;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.repository.JwtRepository;
import dracoul.tech.avis.service.UtilisateurService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.*;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class JwtService {
    public static final String BEARER = "bearer";
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INCORRECT = "Token incorrect";
    private final String ENCRYPTION_KEY = "4527aafcd5ca9013e42b4ee99ce02f890682dee9f5e5b604b2fbe5316807667c";
    private UtilisateurService utilisateurService;
    private JwtRepository jwtRepository;

    public Jwt tokenByValue(String value) {
        return this.jwtRepository.findByValueAndDesactivatedAndExpired(
                value,
                false,
                false
        ) .orElseThrow(() -> new RuntimeException("Token invalid ou incorrect"));
    }
   /**
    * public Jwt tokenByValue(String value) {
       return this.jwtRepository.findByValue(value)
               .orElseThrow(() -> new RuntimeException("Token incorrect"));
   }*/
    private void disableTokens(Utilisateur utilisateur){
        final List <Jwt> jwtList = this.jwtRepository.findUserByEmail(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setDesactivated(true);
                    jwt.setExpired(true);
                }
        ).collect(Collectors.toList());
        this.jwtRepository.saveAll(jwtList);
    }



    public Map<String, String> generate(String email){
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        this.disableTokens(utilisateur);
        Map<String, String> jwtMap = new java.util.HashMap<>(this.generateJwt(utilisateur));

        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expired(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30 * 60 * 1000))
                .build();

        Jwt jtw = Jwt
                .builder()
                .value(jwtMap.get(BEARER))
                .desactivated(false)
                .expired(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();

        this.jwtRepository.save(jtw);
        jwtMap.put(REFRESH, refreshToken.getValeur());
        return jwtMap;
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
        return Map.of(BEARER, bearer);
    }

    private Key getKey() {
       final byte[] decoder =Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void deconnexion() {
         Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findUserValidToken(
                utilisateur.getEmail(),
                false,
                false).orElseThrow(() -> new RuntimeException(TOKEN_INCORRECT)
        );
        jwt.setExpired(true);
        jwt.setDesactivated(true);
        this.jwtRepository.save(jwt);
    }
   // @Scheduled(cron = "@daily")

   // @Scheduled(cron = "0 */1 * * * *")
    public void removeUselessJwt(){
        log.info("Suppression des token à {}", Instant.now());
        this.jwtRepository.deleteAllByExpiredAndDesactivated(true, true);
    }

    public Map<String, String> refreshToken(Map<String, String> refreshTokenRequest) {
        Jwt jwt = this.jwtRepository.findRefreshTokenValeur(refreshTokenRequest.get(REFRESH))
                .orElseThrow(() -> new RuntimeException(TOKEN_INCORRECT));
        if(jwt.getRefreshToken().isExpired() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())){
            throw new RuntimeException(TOKEN_INCORRECT);
        }
        Map<String, String> tokens = this.generate(jwt.getUtilisateur().getEmail());
        this.disableTokens(jwt.getUtilisateur());
       return tokens;
    }
}

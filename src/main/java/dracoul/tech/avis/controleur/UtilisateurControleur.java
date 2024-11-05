package dracoul.tech.avis.controleur;

import dracoul.tech.avis.Dto.AuthentificationDto;
import dracoul.tech.avis.entite.Utilisateur;
import dracoul.tech.avis.securite.JwtService;
import dracoul.tech.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurControleur {

    private final AuthenticationManager authenticationManager;
    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        this.utilisateurService.saves(utilisateur);
        log.info("____Inscription Success____");
    }
    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        this.utilisateurService.activation(activation);
        log.info("____Activation Success____");
    }

    @PostMapping(path = "connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDto authentificationDto) {
       final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDto.email(), authentificationDto.password())
        );

       if(authenticate.isAuthenticated()) {
           return this.jwtService.generate(authentificationDto.email());
       }
        log.info("____Connexion Success____");
        return null;
    }
}

package dracoul.tech.avis.controller;

import dracoul.tech.avis.Dto.AuthentificationDto;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.security.JwtService;
import dracoul.tech.avis.service.UtilisateurService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {

    private final AuthenticationManager authenticationManager;
    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        try {
            this.utilisateurService.save(utilisateur);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
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
           log.info("____Generation token Success____");
           return this.jwtService.generate(authentificationDto.email());
       }
        log.info("____Connexion Success____");
        return null;
    }
    @PostMapping(path = "delete/{id}")
    public void delete(@PathVariable("id") int id) {
        utilisateurService.deleteUser(id);
    }
}

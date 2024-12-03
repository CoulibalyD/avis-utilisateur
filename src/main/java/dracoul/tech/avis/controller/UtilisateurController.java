package dracoul.tech.avis.controller;


import dracoul.tech.avis.entity.Avis;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.service.AvisService;
import dracoul.tech.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("utilisateur")
@RestController
public class UtilisateurController {

    UtilisateurService utilisateurService;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR_READ')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Utilisateur> list(){
        return utilisateurService.list();
    }
}

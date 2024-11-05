package dracoul.tech.avis.service;

import dracoul.tech.avis.TypeRole;
import dracoul.tech.avis.entite.Role;
import dracoul.tech.avis.entite.Utilisateur;
import dracoul.tech.avis.entite.Validation;
import dracoul.tech.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;
    private ValidationService validationService;
    private BCryptPasswordEncoder passwordEncoder;

    public void saves(Utilisateur utilisateur) {
        if(!utilisateur.getEmail().contains("@")){
            throw new RuntimeException("Votre Email est invalide");
        }
        if(!utilisateur.getEmail().contains(".")){
            throw new RuntimeException("Votre Email est invalide");
        }
        Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());
        if(utilisateurOptional.isPresent()){
            throw new RuntimeException("Votre Email est utilisé");
        }
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        String mdpCrypter = this.passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setMdp(mdpCrypter);
        utilisateur = this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);

    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireEnfonctionDuConde(activation.get("code"));

        if(Instant.now().isAfter(validation.getExpiration())){
            throw new RuntimeException("Votre code a expiré");
        }
       Utilisateur utilisateurActiver =  this.utilisateurRepository.findById(validation.getUtilisateur().getId())
                .orElseThrow(()-> new RuntimeException("Utilisateur n'existe pas"));
        utilisateurActiver.setActive(true);
        this.utilisateurRepository.save(utilisateurActiver);
    }

    @Override
    public UserDetails loadUserByUsername(String email)  throws UsernameNotFoundException {

        return this.utilisateurRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant"));
    }
}

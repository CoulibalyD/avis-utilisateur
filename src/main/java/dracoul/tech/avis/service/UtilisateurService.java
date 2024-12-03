package dracoul.tech.avis.service;

import dracoul.tech.avis.enums.TypeRole;
import dracoul.tech.avis.entity.Role;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.entity.Validation;
import dracoul.tech.avis.repository.UtilisateurRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {

    private UtilisateurRepository utilisateurRepository;
    private ValidationService validationService;
    private BCryptPasswordEncoder passwordEncoder;

    public void save(Utilisateur utilisateur) throws MessagingException {
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

        String mdpCrypter = this.passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setMdp(mdpCrypter);

        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeRole.UTILISATEUR);
        if(utilisateur.getRole() != null && utilisateur.getRole().getLibelle().equals(TypeRole.ADMINISTRATEUR)){
            roleUtilisateur.setLibelle(TypeRole.ADMINISTRATEUR);
            utilisateur.setActive(true);
        }

        utilisateur.setRole(roleUtilisateur);

        utilisateur = this.utilisateurRepository.save(utilisateur);

        if(roleUtilisateur.getLibelle().equals(TypeRole.UTILISATEUR)){
            this.validationService.enregistrer(utilisateur);
        }

    }
    public Optional<Utilisateur> findUser(String email) {
        return Optional.ofNullable(this.utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("email n'existe pas")));
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

    public UserDetails loadUserByUsername(String email)  throws UsernameNotFoundException {

        return this.utilisateurRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant"));
    }

    public void modifierMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur = (Utilisateur) this.loadUserByUsername(parametres.get("email"));
            this.validationService.enregistrer(utilisateur);
    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur =(Utilisateur) this.loadUserByUsername(parametres.get("email"));
        Validation validation = validationService.lireEnfonctionDuConde(parametres.get("code"));
        if(validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())){
            String mdpCrypter = this.passwordEncoder.encode(parametres.get("password"));
            utilisateur.setMdp(mdpCrypter);
            this.utilisateurRepository.save(utilisateur);
        }
    }

    public void deleteUser(int id){
        utilisateurRepository.deleteUtilisateurById(id)
                .orElseThrow(()-> new RuntimeException("Utilisateur "+id+" n'existe pas"));
    }


    public List<Utilisateur> list() {
        Iterable<Utilisateur> utilisateurIterable = this.utilisateurRepository.findAll();
        List utilisateurs = new ArrayList();

        utilisateurIterable.forEach(utilisateur -> utilisateurs.add(utilisateur));
        return utilisateurs;
    }
}

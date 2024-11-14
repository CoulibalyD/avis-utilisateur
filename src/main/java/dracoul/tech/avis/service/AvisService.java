package dracoul.tech.avis.service;

import dracoul.tech.avis.TypeRole;
import dracoul.tech.avis.entite.Avis;
import dracoul.tech.avis.entite.Role;
import dracoul.tech.avis.entite.Utilisateur;
import dracoul.tech.avis.repository.AvisRepository;
import dracoul.tech.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor

@Service
public class AvisService {

    private AvisRepository avisRepository;

    public void creer(Avis avis) {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        avis.setUtilisateur(utilisateur);
        this.avisRepository.save(avis);
    }

    public Avis findById(int id) {
        Optional<Avis> avis = this.avisRepository.findById(id);
        return avis.orElse(null);
    }
}

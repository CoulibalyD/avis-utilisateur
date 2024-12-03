package dracoul.tech.avis.service;

import dracoul.tech.avis.entity.Avis;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.exception.AvisNotFoundException;
import dracoul.tech.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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
       return this.avisRepository.findById(id)
               .orElseThrow(() -> new AvisNotFoundException("Avis avec l'ID" +id + "n'a pas été trouvé "));
    }

    public void deleteAvis(int id) {
        avisRepository.deleteAvisById(id);
    }

    public List<Avis> liste() {
        return this.avisRepository.findAll();
    }
}

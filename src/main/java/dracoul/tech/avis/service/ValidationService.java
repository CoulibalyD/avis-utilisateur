package dracoul.tech.avis.service;

import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.entity.Validation;
import dracoul.tech.avis.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@AllArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private NotificationService notificationService;

    public void enregistrer(Utilisateur utilisateur) {
        // Vérifie si une validation existe déjà pour cet utilisateur
        Validation validation = validationRepository.findByUtilisateur(utilisateur)
                .orElse(new Validation()); // Si aucune validation, en crée une nouvelle

        // Génère les nouvelles valeurs
        Instant creation = Instant.now();
        validation.setUtilisateur(utilisateur);
        validation.setCreation(creation);
        validation.setExpiration(creation.plus(10, ChronoUnit.MINUTES));

        // Génère un code unique
        String code;
        do {
            code = String.format("%06d", new Random().nextInt(999999));
        } while (validationRepository.findByCode(code).isPresent());
        validation.setCode(code);

        // Sauvegarde ou met à jour
        validationRepository.save(validation);

        // Envoie la notification
        this.notificationService.envoyer(validation);
    }


    public Validation lireEnfonctionDuConde(String code) {
       return this.validationRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Votre code est invalide"));
    }
}

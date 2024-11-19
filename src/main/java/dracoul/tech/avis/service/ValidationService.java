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
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        validation.setCreation(creation);
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);
        validation.setExpiration(expiration);

        Random random = new Random();
        int randomInt = random.nextInt(999999);
        String code = String.format("%04d", randomInt);

        validation.setCode(code);
        this.validationRepository.save(validation);
        this.notificationService.envoyer(validation);
    }

    public Validation lireEnfonctionDuConde(String code) {
       return this.validationRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Votre code est invalide"));
    }
}

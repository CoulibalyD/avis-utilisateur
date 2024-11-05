package dracoul.tech.avis.service;

import dracoul.tech.avis.entite.Validation;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    JavaMailSender mailSender;
    public void envoyer (Validation validation){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre Code d'activation");

        String texte = String.format(
                "Bonjour %s, <br/> Votre d'activation est %s; A bientôt!",
                validation.getUtilisateur().getNom(),
                validation.getCode()
        );
        mailMessage.setText(texte);
        mailSender.send(mailMessage);
    }
}

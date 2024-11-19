package dracoul.tech.avis.service;

import dracoul.tech.avis.entity.Validation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class NotificationService {

    JavaMailSender mailSender;
    TemplateEngine TemplateEngine;

    public void envoyer (Validation validation){
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("no-reply@gmail.com");
            helper.setTo(validation.getUtilisateur().getEmail());
            helper.setSubject("Votre Code d'activation");
            //Creation de Contexte
            Context context = new Context();
            context.setVariable("lastName", validation.getUtilisateur().getNom());
            context.setVariable("code", validation.getCode());

            //Géneration le contenu HTML de l'email
            String html = TemplateEngine.process("mail-template", context);
            helper.setText(html, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
       /** SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre Code d'activation");

        String texte = String.format(
                "Bonjour %s, <br/> Votre d'activation est %s; A bientôt!",
                validation.getUtilisateur().getNom(),
                validation.getCode()
        );
        mailMessage.setText(texte);
        mailSender.send(mailMessage);*/
    }
}

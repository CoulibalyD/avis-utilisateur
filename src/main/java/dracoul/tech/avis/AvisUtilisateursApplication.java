package dracoul.tech.avis;

import dracoul.tech.avis.entity.Role;
import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.enums.TypeRole;
import dracoul.tech.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AvisUtilisateursApplication implements CommandLineRunner  {
	private static final Logger log = LoggerFactory.getLogger(AvisUtilisateursApplication.class);
	UtilisateurRepository utilisateurRepository;
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AvisUtilisateursApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		createOrUpdateUser(
				"admin@gmail.com",
				"Admin",
				"admin",
				TypeRole.ADMINISTRATEUR
		);

		createOrUpdateUser(
				"manager@gmail.com",
				"Manager",
				"manager",
				TypeRole.MANAGER
		);
	}

	/**
	 * Méthode générique pour créer ou mettre à jour un utilisateur.
	 */
	private void createOrUpdateUser(String email, String nom, String rawPassword, TypeRole typeRole) {
		Utilisateur utilisateur = Utilisateur.builder()
				.email(email)
				.nom(nom)
				.active(true)
				.mdp(passwordEncoder.encode(rawPassword))
				.role(Role.builder()
						.libelle(typeRole)
						.build())
				.build();

		utilisateur = utilisateurRepository.findByEmail(email).orElse(utilisateur);
		utilisateurRepository.save(utilisateur);
		log.info("Utilisateur {} créé ou mis à jour avec succès !", utilisateur);
	}

}

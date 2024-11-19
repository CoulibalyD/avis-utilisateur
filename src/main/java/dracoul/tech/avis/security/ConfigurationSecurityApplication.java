package dracoul.tech.avis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Classe de configuration de la sécurité pour l'application.
 * Cette classe utilise Spring Security pour configurer l'authentification
 * et les autorisations pour les requêtes HTTP de l'application.
 */
@Configuration
@EnableWebSecurity
public class ConfigurationSecurityApplication {

    private final JwtFilter jwtFilter;  // Filtre pour les tokens JWT pour l'authentification
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // Encoder pour sécuriser les mots de passe

    /**
     * Constructeur injectant les dépendances pour le filtre JWT et l'encodeur de mots de passe.
     *
     * @param jwtFilter Le filtre JWT pour valider les tokens d'authentification.
     * @param bCryptPasswordEncoder Encodeur de mots de passe pour sécuriser les mots de passe.
     */
    public ConfigurationSecurityApplication(JwtFilter jwtFilter, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtFilter = jwtFilter;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Bean de configuration de la chaîne de filtres de sécurité.
     * Ce bean configure les autorisations des requêtes HTTP, la gestion de session, et le filtre JWT.
     *
     * @param http Objet HttpSecurity utilisé pour configurer la sécurité HTTP.
     * @return La chaîne de filtres de sécurité configurée.
     * @throws Exception En cas d'erreur de configuration de sécurité.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)  // Désactive la protection CSRF pour cette API
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(POST, "/inscription", "/activation", "/connexion", "delete/**").permitAll()  // Autorise sans authentification les POST sur ces endpoints
                                        .requestMatchers(GET, "/avis/find/**").permitAll()  // Autorise les GET vers "/avis/find/**"
                                        .anyRequest().authenticated()  // Exige une authentification pour toute autre requête
                )
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Utilise une politique de session sans état (utile pour les API REST)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)  // Ajoute le filtre JWT avant le filtre de l'authentification par nom d'utilisateur et mot de passe
                .build();
    }

    /**
     * Bean pour l'AuthenticationManager, qui gère l'authentification des utilisateurs.
     *
     * @param authenticationConfiguration Configuration de l'authentification.
     * @return L'AuthenticationManager configuré.
     * @throws Exception En cas d'erreur de configuration de l'authentification.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean pour le fournisseur d'authentification utilisant une source de données utilisateur (UserDetailsService).
     * Configure le fournisseur pour utiliser un encodeur de mots de passe.
     *
     * @param userDetailsService Service fournissant les détails des utilisateurs pour l'authentification.
     * @return Le fournisseur d'authentification configuré.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);  // Associe le service pour récupérer les détails utilisateurs
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);  // Associe l'encodeur de mots de passe
        return daoAuthenticationProvider;
    }
}

package dracoul.tech.avis.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "jwt")
public class Jwt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String value;
    private boolean desactivated;
    private boolean expired;
    @ManyToOne (cascade = {CascadeType.DETACH, CascadeType.MERGE} )
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}

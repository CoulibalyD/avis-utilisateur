package dracoul.tech.avis.repository;

import dracoul.tech.avis.entity.Utilisateur;
import dracoul.tech.avis.entity.Validation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidationRepository extends CrudRepository<Validation, Integer> {

    Optional<Validation> findByCode(String code);
    Optional<Validation> findByUtilisateur(Utilisateur utilisateur);

}

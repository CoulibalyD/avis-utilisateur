package dracoul.tech.avis.repository;

import dracoul.tech.avis.entite.Avis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AvisRepository extends CrudRepository<Avis, Integer> {
    Optional<Avis> findById(int id);
}

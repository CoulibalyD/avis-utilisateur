package dracoul.tech.avis.repository;

import dracoul.tech.avis.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AvisRepository extends JpaRepository<Avis, Integer> {
    Optional<Avis> findById(int id);
    Optional<Avis> deleteAvisById(int id);
}

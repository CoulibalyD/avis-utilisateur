package dracoul.tech.avis.repository;

import dracoul.tech.avis.entity.Jwt;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    //Optional<Jwt> findByToken(String value);
    Optional<Jwt> findByValue(String value);
}

package dracoul.tech.avis.repository;

import dracoul.tech.avis.entity.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {

    Optional<Jwt> findByValueAndDesactivatedAndExpired(String value, boolean desactivated, boolean expired);

    @Query("FROM Jwt j WHERE j.expired = :expired AND j.desactivated = :desactivated AND j.utilisateur.email = :email")
    Optional<Jwt>findUserValidToken(String email, boolean desactivated, boolean expired );

    @Query("FROM Jwt j WHERE j.utilisateur.email = :email ")
    Stream<Jwt> findUserByEmail(String email);

    @Query("FROM Jwt j WHERE j.refreshToken.valeur = :valeur ")
    Optional<Jwt> findRefreshTokenValeur(String valeur);

    void deleteAllByExpiredAndDesactivated(boolean expired, boolean desactivated);

}

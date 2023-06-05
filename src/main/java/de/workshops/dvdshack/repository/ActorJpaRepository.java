package de.workshops.dvdshack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActorJpaRepository extends JpaRepository<Actor, Integer>, JpaSpecificationExecutor<Actor> {

    List<Actor> findActorsByLastName(String lastName);

    Optional<Actor> findFirstAsOptionalByFirstNameAndLastName(String firstName, String lastName);

    Actor findFirstAsActorByFirstNameAndLastName(String firstName, String lastName);

    List<Actor> findAllByFirstNameAndLastName(String mike, String cox);

    List<Actor> findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc(String lastName, String firstName);

    @Query("SELECT a FROM Actor a WHERE a.firstName = :firstName AND a.lastName = :lastName")
    List<Actor> queryActors(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );
}

package de.workshops.dvdshack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorJpaRepository extends JpaRepository<Actor, Integer> {

    List<Actor> findActorsByLastName(String lastName);

    Optional<Actor> findFirstAsOptionalByFirstNameAndLastName(String firstName, String lastName);

    Actor findFirstAsActorByFirstNameAndLastName(String firstName, String lastName);

    List<Actor> findAllByFirstNameAndLastName(String firstName, String lastName);

    List<Actor> findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc(String lastName, String firstName);
}

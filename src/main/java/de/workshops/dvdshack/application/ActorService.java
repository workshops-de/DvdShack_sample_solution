package de.workshops.dvdshack.application;


import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorJpaRepository actorJpaRepository;

    public List<Actor> findActorsByLastName(String lastName) {
        return actorJpaRepository.findActorsByLastName(lastName);
    }

    public List<Actor> findActorsByFirstNameSpecification(String firstName) {
        return actorJpaRepository.findAll(where(firstName(firstName)));
    }

    private Specification<Actor> firstName(String firstName) {
        return (actor, query, criteriaBuilder) ->
                criteriaBuilder.equal(actor.get("firstName"), firstName);
    }
}

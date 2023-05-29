package de.workshops.dvdshack.application;


import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorJpaRepository actorJpaRepository;

    public List<Actor> findActorsByLastName(String lastName) {
        return actorJpaRepository.findActorsByLastName(lastName);
    }
}

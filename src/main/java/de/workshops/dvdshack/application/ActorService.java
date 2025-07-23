package de.workshops.dvdshack.application;

import de.workshops.dvdshack.application.exception.ActorNotFoundException;
import de.workshops.dvdshack.application.exception.ActorValidationException;
import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJdbcTemplateRepository;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActorService {

    private final ActorJpaRepository actorJpaRepository;
    private final ActorJdbcTemplateRepository actorJdbcTemplateRepository;

    // CRUD Operations

    public Actor createActor(Actor actor) {
        validateActor(actor);
        log.info("Creating new actor: {} {}", actor.getFirstName(), actor.getLastName());
        Actor savedActor = actorJpaRepository.save(actor);
        log.debug("Created actor with id: {}", savedActor.getId());
        return savedActor;
    }

    @Transactional(readOnly = true)
    public Page<Actor> findAllActors(Pageable pageable) {
        log.debug("Fetching actors with pagination: {}", pageable);
        Page<Actor> actors = actorJpaRepository.findAll(pageable);
        log.info("Retrieved {} actors out of {} total", actors.getNumberOfElements(), actors.getTotalElements());
        return actors;
    }

    @Transactional(readOnly = true)
    public Actor findActorById(Integer id) {
        if (id == null || id <= 0) {
            throw new ActorValidationException("Actor ID must be positive, got: " + id);
        }
        log.debug("Finding actor by id: {}", id);
        return actorJpaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Actor not found with id: {}", id);
                return new ActorNotFoundException("Actor not found with id: " + id);
            });
    }

    public Actor updateActor(Integer id, Actor actor) {
        validateActorId(id);
        validateActor(actor);
        
        log.debug("Updating actor with id: {}", id);
        Actor existingActor = findActorById(id);
        
        existingActor.setFirstName(actor.getFirstName());
        existingActor.setLastName(actor.getLastName());
        
        Actor updatedActor = actorJpaRepository.save(existingActor);
        log.info("Updated actor: {} {} (id: {})", updatedActor.getFirstName(), updatedActor.getLastName(), id);
        return updatedActor;
    }

    public void deleteActor(Integer id) {
        validateActorId(id);
        
        if (!actorJpaRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent actor with id: {}", id);
            throw new ActorNotFoundException("Cannot delete: Actor not found with id: " + id);
        }
        
        log.debug("Deleting actor with id: {}", id);
        actorJpaRepository.deleteById(id);
        log.info("Deleted actor with id: {}", id);
    }

    // Search Methods

    @Transactional(readOnly = true)
    public List<Actor> findActorsByLastName(String lastName) {
        validateLastName(lastName);
        
        log.debug("Searching actors by last name: {}", lastName);
        List<Actor> actors = actorJpaRepository.findActorsByLastName(lastName.trim());
        log.info("Found {} actors with last name: {}", actors.size(), lastName);
        return actors;
    }

    @Transactional(readOnly = true)
    public List<Actor> findActorsByFirstName(String firstName) {
        validateFirstName(firstName);
        
        log.debug("Searching actors by first name: {}", firstName);
        
        // Use modern Specification API (not deprecated where() method)
        Specification<Actor> spec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("firstName")), 
                firstName.trim().toLowerCase()
            );
        
        List<Actor> actors = actorJpaRepository.findAll(spec);
        log.info("Found {} actors with first name: {}", actors.size(), firstName);
        return actors;
    }

    // Business Logic Methods

    @Transactional(readOnly = true)
    public Optional<Actor> findActorByFullName(String firstName, String lastName) {
        validateNames(firstName, lastName);
        
        log.debug("Searching actor by full name: {} {}", firstName, lastName);
        Optional<Actor> actor = actorJpaRepository.findFirstAsOptionalByFirstNameAndLastName(
            firstName.trim(), lastName.trim()
        );
        
        if (actor.isPresent()) {
            log.info("Found actor: {} {} (id: {})", firstName, lastName, actor.get().getId());
        } else {
            log.debug("No actor found with name: {} {}", firstName, lastName);
        }
        
        return actor;
    }

    @Transactional(readOnly = true)
    public List<Actor> searchActors(String searchTerm) {
        if (!StringUtils.hasText(searchTerm)) {
            log.debug("Empty search term provided, returning empty list");
            return Collections.emptyList();
        }
        
        String term = searchTerm.trim();
        log.debug("Searching actors with term: {}", term);
        
        List<Actor> actors = actorJpaRepository.findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc(
            term, term
        );
        
        log.info("Found {} actors matching search term: {}", actors.size(), term);
        return actors;
    }

    @Transactional(readOnly = true)
    public long countActorsByLastName(String lastName) {
        validateLastName(lastName);
        
        log.debug("Counting actors by last name: {}", lastName);
        
        Specification<Actor> spec = (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("lastName"), lastName.trim());
        
        long count = actorJpaRepository.count(spec);
        log.info("Found {} actors with last name: {}", count, lastName);
        return count;
    }

    @Transactional(readOnly = true)
    public List<Actor> findAllActorsByFullName(String firstName, String lastName) {
        validateNames(firstName, lastName);
        
        log.debug("Finding all actors by full name: {} {}", firstName, lastName);
        List<Actor> actors = actorJpaRepository.findAllByFirstNameAndLastName(
            firstName.trim(), lastName.trim()
        );
        
        log.info("Found {} actors with name: {} {}", actors.size(), firstName, lastName);
        return actors;
    }

    // JDBC Template Methods (for demonstration of dual repository usage)

    @Transactional(readOnly = true)
    public List<Actor> findActorsByLastNameUsingJdbc(String lastName) {
        validateLastName(lastName);
        
        log.debug("Searching actors by last name using JDBC: {}", lastName);
        List<Actor> actors = actorJdbcTemplateRepository.findAllActorsWithLastName(lastName.trim());
        log.info("Found {} actors with last name using JDBC: {}", actors.size(), lastName);
        return actors;
    }

    // Validation Methods

    private void validateActor(Actor actor) {
        if (actor == null) {
            throw new ActorValidationException("Actor cannot be null");
        }
        validateFirstName(actor.getFirstName());
        validateLastName(actor.getLastName());
    }

    private void validateActorId(Integer id) {
        if (id == null || id <= 0) {
            throw new ActorValidationException("Actor ID must be positive, got: " + id);
        }
    }

    private void validateFirstName(String firstName) {
        if (!StringUtils.hasText(firstName)) {
            throw new ActorValidationException("First name cannot be null or empty");
        }
        if (firstName.trim().length() > 45) {
            throw new ActorValidationException("First name cannot exceed 45 characters");
        }
    }

    private void validateLastName(String lastName) {
        if (!StringUtils.hasText(lastName)) {
            throw new ActorValidationException("Last name cannot be null or empty");
        }
        if (lastName.trim().length() > 45) {
            throw new ActorValidationException("Last name cannot exceed 45 characters");
        }
    }

    private void validateNames(String firstName, String lastName) {
        validateFirstName(firstName);
        validateLastName(lastName);
    }
}

package de.workshops.dvdshack.web;

import de.workshops.dvdshack.application.ActorService;
import de.workshops.dvdshack.application.exception.ActorNotFoundException;
import de.workshops.dvdshack.application.exception.ActorValidationException;
import de.workshops.dvdshack.repository.Actor;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
@Slf4j
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public ResponseEntity<Page<Actor>> getAllActors(Pageable pageable) {
        log.debug("Getting all actors with pagination: {}", pageable);
        Page<Actor> actors = actorService.findAllActors(pageable);
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActorById(@PathVariable Integer id) {
        try {
            Actor actor = actorService.findActorById(id);
            return ResponseEntity.ok(actor);
        } catch (ActorNotFoundException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage()
            );
            problemDetail.setTitle("Actor Not Found");
            problemDetail.setProperty("actorId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid Actor ID");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @PostMapping
    public ResponseEntity<?> createActor(@Valid @RequestBody Actor actor) {
        try {
            Actor savedActor = actorService.createActor(actor);
            
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedActor.getId())
                .toUri();
                
            return ResponseEntity.created(location).body(savedActor);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Actor Validation Failed");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Integer id, @Valid @RequestBody Actor actor) {
        try {
            Actor updatedActor = actorService.updateActor(id, actor);
            return ResponseEntity.ok(updatedActor);
        } catch (ActorNotFoundException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage()
            );
            problemDetail.setTitle("Actor Not Found");
            problemDetail.setProperty("actorId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Actor Validation Failed");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Integer id) {
        try {
            actorService.deleteActor(id);
            return ResponseEntity.noContent().build();
        } catch (ActorNotFoundException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage()
            );
            problemDetail.setTitle("Actor Not Found");
            problemDetail.setProperty("actorId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid Actor ID");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Actor>> searchActors(@RequestParam String term) {
        List<Actor> actors = actorService.searchActors(term);
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/by-last-name")
    public ResponseEntity<?> getActorsByLastName(@RequestParam String lastName) {
        try {
            List<Actor> actors = actorService.findActorsByLastName(lastName);
            return ResponseEntity.ok(actors);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid Last Name");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @GetMapping("/by-first-name")
    public ResponseEntity<?> getActorsByFirstName(@RequestParam String firstName) {
        try {
            List<Actor> actors = actorService.findActorsByFirstName(firstName);
            return ResponseEntity.ok(actors);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid First Name");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @GetMapping("/by-full-name")
    public ResponseEntity<?> getActorByFullName(
            @RequestParam String firstName, 
            @RequestParam String lastName) {
        try {
            Optional<Actor> actor = actorService.findActorByFullName(firstName, lastName);
            return actor.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid Name Parameters");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }

    @GetMapping("/count-by-last-name")
    public ResponseEntity<?> countActorsByLastName(@RequestParam String lastName) {
        try {
            long count = actorService.countActorsByLastName(lastName);
            return ResponseEntity.ok(count);
        } catch (ActorValidationException e) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage()
            );
            problemDetail.setTitle("Invalid Last Name");
            return ResponseEntity.badRequest().body(problemDetail);
        }
    }
}

package de.workshops.dvdshack.web;

import de.workshops.dvdshack.application.FilmService;
import de.workshops.dvdshack.application.exception.FilmNotFoundException;
import de.workshops.dvdshack.application.exception.FilmValidationException;
import de.workshops.dvdshack.repository.Film;
import jakarta.validation.Valid;
import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Page<Film>> getAllFilms(Pageable pageable) {
        log.debug("Getting all films with pagination: {}", pageable);
        Page<Film> films = filmService.findAllFilms(pageable);
        log.info("Retrieved {} films", films.getTotalElements());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable Integer id) {
        log.debug("Getting film with id: {}", id);
        
        try {
            Film film = filmService.findFilmById(id);
            log.info("Film found with id: {}", id);
            return ResponseEntity.ok(film);
        } catch (FilmNotFoundException e) {
            log.warn("Film not found with id: {}", id);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, 
                e.getMessage()
            );
            problemDetail.setTitle("Film Not Found");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@Valid @RequestBody Film film) {
        log.debug("Creating new film: {}", film.getTitle());
        
        try {
            Film savedFilm = filmService.createFilm(film);
            log.info("Film created successfully with id: {}", savedFilm.getId());
            
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedFilm.getId())
                .toUri();
                
            return ResponseEntity.created(location).body(savedFilm);
        } catch (FilmValidationException e) {
            log.error("Validation error creating film: {}", e.getMessage());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
            );
            problemDetail.setTitle("Film Validation Failed");
            return ResponseEntity.badRequest().body(problemDetail);
        } catch (Exception e) {
            log.error("Error creating film: {}", e.getMessage(), e);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create film: " + e.getMessage()
            );
            problemDetail.setTitle("Film Creation Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFilm(@PathVariable Integer id, @Valid @RequestBody Film film) {
        log.debug("Updating film with id: {}", id);
        
        try {
            Film updatedFilm = filmService.updateFilm(id, film);
            log.info("Film updated successfully with id: {}", id);
            return ResponseEntity.ok(updatedFilm);
        } catch (FilmNotFoundException e) {
            log.warn("Film not found with id: {}", id);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
            );
            problemDetail.setTitle("Film Not Found");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        } catch (FilmValidationException e) {
            log.error("Validation error updating film: {}", e.getMessage());
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
            );
            problemDetail.setTitle("Film Validation Failed");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.badRequest().body(problemDetail);
        } catch (Exception e) {
            log.error("Error updating film with id {}: {}", id, e.getMessage(), e);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to update film: " + e.getMessage()
            );
            problemDetail.setTitle("Film Update Failed");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable Integer id) {
        log.debug("Deleting film with id: {}", id);
        
        try {
            filmService.deleteFilm(id);
            log.info("Film deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (FilmNotFoundException e) {
            log.warn("Film not found with id: {}", id);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
            );
            problemDetail.setTitle("Film Not Found");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        } catch (Exception e) {
            log.error("Error deleting film with id {}: {}", id, e.getMessage(), e);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Failed to delete film: " + e.getMessage()
            );
            problemDetail.setTitle("Film Deletion Failed");
            problemDetail.setProperty("filmId", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
        }
    }
}

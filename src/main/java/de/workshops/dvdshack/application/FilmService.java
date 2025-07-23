package de.workshops.dvdshack.application;

import de.workshops.dvdshack.application.exception.FilmNotFoundException;
import de.workshops.dvdshack.application.exception.FilmValidationException;
import de.workshops.dvdshack.repository.Film;
import de.workshops.dvdshack.repository.FilmJdbcTemplateRepository;
import de.workshops.dvdshack.repository.FilmJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FilmService {

    private final FilmJpaRepository filmJpaRepository;
    private final FilmJdbcTemplateRepository filmJdbcTemplateRepository;

    // CRUD Operations

    public Film createFilm(Film film) {
        validateFilm(film);
        // Ensure ID is not set for creation
        film.setId(0);
        log.info("Creating new film: {}", film.getTitle());
        Film savedFilm = filmJpaRepository.save(film);
        log.debug("Created film with id: {}", savedFilm.getId());
        return savedFilm;
    }

    @Transactional(readOnly = true)
    public Page<Film> findAllFilms(Pageable pageable) {
        log.debug("Fetching films with pagination: {}", pageable);
        Page<Film> films = filmJpaRepository.findAll(pageable);
        log.info("Retrieved {} films out of {} total", films.getNumberOfElements(), films.getTotalElements());
        return films;
    }

    @Transactional(readOnly = true)
    public Film findFilmById(Integer id) {
        validateFilmId(id);
        log.debug("Finding film by id: {}", id);
        return filmJpaRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Film not found with id: {}", id);
                return new FilmNotFoundException("Film not found with id: " + id);
            });
    }

    public Film updateFilm(Integer id, Film film) {
        validateFilmId(id);
        validateFilm(film);
        
        log.debug("Updating film with id: {}", id);
        
        if (!filmJpaRepository.existsById(id)) {
            log.warn("Film not found with id: {}", id);
            throw new FilmNotFoundException("Film not found with id: " + id);
        }
        
        film.setId(id);
        Film updatedFilm = filmJpaRepository.save(film);
        log.info("Updated film: {} (id: {})", updatedFilm.getTitle(), id);
        return updatedFilm;
    }

    public void deleteFilm(Integer id) {
        validateFilmId(id);
        
        if (!filmJpaRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent film with id: {}", id);
            throw new FilmNotFoundException("Cannot delete: Film not found with id: " + id);
        }
        
        log.debug("Deleting film with id: {}", id);
        filmJpaRepository.deleteById(id);
        log.info("Deleted film with id: {}", id);
    }

    // Validation Methods

    private void validateFilm(Film film) {
        if (film == null) {
            throw new FilmValidationException("Film cannot be null");
        }
        validateTitle(film.getTitle());
        
        if (film.getLanguage() == null) {
            throw new FilmValidationException("Film language cannot be null");
        }
        
        if (film.getRentalDuration() <= 0) {
            throw new FilmValidationException("Rental duration must be positive");
        }
        
        if (film.getRentalRate() == null || film.getRentalRate().signum() <= 0) {
            throw new FilmValidationException("Rental rate must be positive");
        }
        
        if (film.getReplacementCost() == null || film.getReplacementCost().signum() <= 0) {
            throw new FilmValidationException("Replacement cost must be positive");
        }
    }

    private void validateFilmId(Integer id) {
        if (id == null || id <= 0) {
            throw new FilmValidationException("Film ID must be positive, got: " + id);
        }
    }

    private void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new FilmValidationException("Film title cannot be null or empty");
        }
    }
}

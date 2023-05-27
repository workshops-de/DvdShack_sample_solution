package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmJdbcTemplateRepositoryTest {
    @Autowired
    FilmJdbcTemplateRepository repository;


    @Test
    void shouldNumberOfFilmsReleaseInSameYear() {
        var count = repository.findNumberOfFilmsPublishedInYear(Year.of(2024));
        assertThat(count).isZero();

        count = repository.findNumberOfFilmsPublishedInYear(Year.of(2006));
        assertThat(count).isEqualTo(1000);
    }

    @Test
    void shouldFindAllFilmsWithSameRating() {
        final var filmsByRating = repository.findAllFilmsWithRating(Rating.PG_13);

        assertThat(filmsByRating).isNotEmpty()
                .extracting(Film::getRating)
                .containsOnly(Rating.PG_13);
    }

    @Test
    void shouldFindAllFilmsWithGivenActor() {
        final var actor = new Actor();
        actor.setLastName("KILMER");
        actor.setFirstName("SANDRA");

        final var films = repository.findAllFilmsWithActor(actor);
        assertThat(films).isNotEmpty();
    }

    @Test
    void shouldFindAllLongFilms() {
        final var films = repository.findAllFilmsWithMinimumLength(180);

        assertThat(films).isNotEmpty()
                .extracting(Film::getLength)
                .filteredOn(length -> length < 180)
                .isEmpty();
    }

    @Test
    void shouldFindFilmsWithSpecialFeature() {
        final var allFilmsBySpecialFeatures = repository.findAllFilmsHavingSpecialFeature("Deleted Scenes");
        assertThat(allFilmsBySpecialFeatures).isNotEmpty()
                .allMatch(film -> film.getSpecialFeatures().contains("Deleted Scenes"));
    }

}
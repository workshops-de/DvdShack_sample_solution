package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = Repository.class
        )
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

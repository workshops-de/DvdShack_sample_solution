package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmJpaRepositoryTest {

    @Autowired
    ActorJpaRepository actorRepository;

    @Autowired
    FilmJpaRepository repository;

    @Test
    void shouldNumberOfFilmsReleaseInSameYear() {
        var count = repository.countFilmsByReleaseYear(Year.of(2024));
        assertThat(count).isZero();

        count = repository.countFilmsByReleaseYear(Year.of(2006));
        assertThat(count).isEqualTo(1000);
    }

    @Test
    void shouldFindAllFilmsWithSameRating() {
        final var filmsByRating = repository.findAllFilmsByRating(Rating.PG_13);

        assertThat(filmsByRating).isNotEmpty()
                .extracting(Film::getRating)
                .containsOnly(Rating.PG_13);
    }

    @Test
    void shouldFindAllFilmsWithGivenActor() {
        final var actor = actorRepository.findFirstAsOptionalByFirstNameAndLastName("REESE", "KILMER");
        assertThat(actor).isNotEmpty();

        final var films = repository.findAllFilmsByActorsIs(actor.get());
        assertThat(films).isNotEmpty();
    }

    @Test
    void shouldFindAllLongFilms() {
        final var films = repository.findAllFilmsByLengthGreaterThanEqual(180);

        assertThat(films).isNotEmpty()
                .extracting(Film::getLength)
                .filteredOn(length -> length < 180)
                .isEmpty();
    }

    @Test
    void shouldFindActorsByFilmAndFirstnameLikeOrLastnameLike() {
        final var films = repository.findByTitleAndReleaseYearOrLength(
                "STRANGERS GRAFFITI",
                Year.of(2006),
                119);
        assertThat(films)
                .hasSize(6); // This is unexpected, because the title is unique and hence there should be only entry here.
    }
}

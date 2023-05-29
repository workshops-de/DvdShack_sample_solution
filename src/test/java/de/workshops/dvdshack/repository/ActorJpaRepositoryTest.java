package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ActorJpaRepositoryTest {
    @Autowired
    ActorJpaRepository repository;

    @Test
    void shouldFindAllActorsByLastName() {
        final var actors = repository.findActorsByLastName("KILMER");
        assertThat(actors)
                .hasSizeGreaterThan(1)
                .extracting("lastName")
                .containsOnly("KILMER");
    }

    @Test
    void shouldReturnOptionalOnFind() {
        var optionalActor = repository.findFirstAsOptionalByFirstNameAndLastName("WOODY", "ALLEN");
        assertThat(optionalActor).isEmpty();

        optionalActor = repository.findFirstAsOptionalByFirstNameAndLastName("CUBA", "ALLEN");
        assertThat(optionalActor).isNotEmpty();
    }

    @Test
    void shouldReturnActorOnFind() {
        var actor = repository.findFirstAsActorByFirstNameAndLastName("WOODY", "ALLEN");
        assertThat(actor).isNull();

        actor = repository.findFirstAsActorByFirstNameAndLastName("CUBA", "ALLEN");
        assertThat(actor).isNotNull();
    }
 }

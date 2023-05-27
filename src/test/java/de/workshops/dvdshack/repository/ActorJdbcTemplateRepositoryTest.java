package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ActorJdbcTemplateRepositoryTest {
    @Autowired
    ActorJdbcTemplateRepository repository;

    @Test
    void shouldFindAllActorsByLastName() {
        final var actors = repository.findAllActorsWithLastName("KILMER");
        assertThat(actors).isNotEmpty()
                .extracting("lastName")
                .containsOnly("KILMER");
    }
}
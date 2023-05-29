package de.workshops.dvdshack.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = Repository.class
        )
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

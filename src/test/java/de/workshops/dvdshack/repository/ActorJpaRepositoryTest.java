package de.workshops.dvdshack.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts={"/actors.sql"})
@DBRider
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

    @Test
    void shouldFindAdditionalActorsByLastName() {
        final var actors = repository.findAllByFirstNameAndLastName("Mike", "Cox");
        assertThat(actors)
                .hasSize(1)
                .extracting("lastName")
                .containsOnly("Cox");
    }

    @Test
    @DataSet(
            value = "datasets/actors.yml",
            strategy = SeedStrategy.INSERT
    )
    void shouldFindAdditionalActorsFromDataSetByLastName() {
        final var actors = repository.findAllByFirstNameAndLastName("Tanya", "Watson");
        assertThat(actors)
                .hasSizeGreaterThanOrEqualTo(1)
                .extracting("lastName")
                .containsOnly("Watson");
    }

    @Test
    void shouldFindActorsByLastNameAndFirstName() {
        final var actors = repository.findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc("HO", "EN");
        assertThat(actors)
                .hasSize(3);

        var firstNames = actors.stream().map(Actor::getFirstName).toList();
        assertThat(firstNames)
                .containsExactly("MENA", "KENNETH", "GENE");
    }

    @Test
    void shouldFindActorsByParameterizedQueryMethod() {
        final var actors = repository.queryActors("PENELOPE", "GUINESS");
        assertThat(actors)
                .hasSize(1);
    }
 }

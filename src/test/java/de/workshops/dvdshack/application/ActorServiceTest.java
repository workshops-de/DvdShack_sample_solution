package de.workshops.dvdshack.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.javafaker.Faker;
import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ActorServiceTest {

    @Autowired
    private ActorService actorService;

    @MockitoBean
    private ActorJpaRepository actorJpaRepository;

    @Test
    void testFindActorsByLastName() {
        // Arrange
        Faker faker = new Faker();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        List<Actor> findActorsByLastNameResult = new ArrayList<>(
                Collections.singletonList(
                        Actor.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build()
                )
        );
        Mockito.when(actorJpaRepository.findActorsByLastName(lastName)).thenReturn(findActorsByLastNameResult);

        // Act
        List<Actor> actorList = actorService.findActorsByLastName(lastName);

        // Assert
        List<Actor> expectedActorList = new ArrayList<>(
                Collections.singletonList(
                        Actor.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build()
                )
        );
        assertEquals(expectedActorList, actorList);
    }
}

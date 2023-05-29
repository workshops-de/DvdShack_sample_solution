package de.workshops.dvdshack.application;

import com.github.javafaker.Faker;
import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ActorServiceTest {

    @Autowired
    private ActorService actorService;

    @MockBean
    private ActorJpaRepository actorJpaRepository;

    @Test
    void testFindActorsByLastName() throws Exception {
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
        List<Actor> list = actorService.findActorsByLastName(lastName);

        // Assert
        List<Actor> listExpected = new ArrayList<>(
                Collections.singletonList(
                        Actor.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build()
                )
        );
        assertEquals(listExpected, list);
    }
}

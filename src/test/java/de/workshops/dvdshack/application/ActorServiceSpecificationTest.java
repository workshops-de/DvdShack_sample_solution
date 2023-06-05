package de.workshops.dvdshack.application;

import de.workshops.dvdshack.repository.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ActorServiceSpecificationTest {

    @Autowired
    private ActorService actorService;

    @Test
    void testFindActorsByFirstNameSpecification() {
        // Arrange (implicit)

        // Act
        List<Actor> actorList = actorService.findActorsByFirstNameSpecification("ADAM");

        // Assert
        assertEquals("GRANT", actorList.get(0).getLastName());
    }
}

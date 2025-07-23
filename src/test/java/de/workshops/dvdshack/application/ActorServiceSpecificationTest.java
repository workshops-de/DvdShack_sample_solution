package de.workshops.dvdshack.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.workshops.dvdshack.repository.Actor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActorServiceSpecificationTest {

    @Autowired
    private ActorService actorService;

    @Test
    void testFindActorsByFirstNameSpecification() {
        // Arrange (implicit)

        // Act
        List<Actor> actorList = actorService.findActorsByFirstName("ADAM");

        // Assert
        assertEquals("GRANT", actorList.get(0).getLastName());
    }
}

package de.workshops.dvdshack.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.workshops.dvdshack.application.exception.ActorNotFoundException;
import de.workshops.dvdshack.application.exception.ActorValidationException;
import de.workshops.dvdshack.repository.Actor;
import de.workshops.dvdshack.repository.ActorJdbcTemplateRepository;
import de.workshops.dvdshack.repository.ActorJpaRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ActorServiceEnhancedTest {

    @Mock
    private ActorJpaRepository actorJpaRepository;

    @Mock
    private ActorJdbcTemplateRepository actorJdbcTemplateRepository;

    @InjectMocks
    private ActorService actorService;

    private Actor testActor;

    @BeforeEach
    void setUp() {
        testActor = Actor.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    // CRUD Operations Tests

    @Test
    void createActor_ValidActor_ShouldReturnSavedActor() {
        // Arrange
        Actor inputActor = Actor.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();
        Actor savedActor = Actor.builder()
                .id(1)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(actorJpaRepository.save(inputActor)).thenReturn(savedActor);

        // Act
        Actor result = actorService.createActor(inputActor);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(actorJpaRepository).save(inputActor);
    }

    @Test
    void createActor_NullActor_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class, 
            () -> actorService.createActor(null));
        
        verify(actorJpaRepository, never()).save(any());
    }

    @Test
    void createActor_EmptyFirstName_ShouldThrowValidationException() {
        // Arrange
        Actor invalidActor = Actor.builder()
                .firstName("")
                .lastName("Doe")
                .build();

        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.createActor(invalidActor));
    }

    @Test
    void findAllActors_ShouldReturnPagedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Actor> actors = Arrays.asList(testActor);
        Page<Actor> page = new PageImpl<>(actors, pageable, 1);
        
        when(actorJpaRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Actor> result = actorService.findAllActors(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }

    @Test
    void findActorById_ExistingId_ShouldReturnActor() {
        // Arrange
        when(actorJpaRepository.findById(1)).thenReturn(Optional.of(testActor));

        // Act
        Actor result = actorService.findActorById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void findActorById_NonExistingId_ShouldThrowNotFoundException() {
        // Arrange
        when(actorJpaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActorNotFoundException.class,
            () -> actorService.findActorById(999));
    }

    @Test
    void findActorById_NullId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorById(null));
    }

    @Test
    void findActorById_NegativeId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorById(-1));
    }

    @Test
    void updateActor_ValidData_ShouldReturnUpdatedActor() {
        // Arrange
        Actor updateData = Actor.builder()
                .firstName("Jane")
                .lastName("Updated")
                .build();
        Actor updatedActor = Actor.builder()
                .id(1)
                .firstName("Jane")
                .lastName("Updated")
                .build();

        when(actorJpaRepository.findById(1)).thenReturn(Optional.of(testActor));
        when(actorJpaRepository.save(any(Actor.class))).thenReturn(updatedActor);

        // Act
        Actor result = actorService.updateActor(1, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Updated", result.getLastName());
        verify(actorJpaRepository).save(any(Actor.class));
    }

    @Test
    void updateActor_NonExistingId_ShouldThrowNotFoundException() {
        // Arrange
        Actor updateData = Actor.builder()
                .firstName("Jane")
                .lastName("Updated")
                .build();
        when(actorJpaRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ActorNotFoundException.class,
            () -> actorService.updateActor(999, updateData));
    }

    @Test
    void deleteActor_ExistingId_ShouldDeleteSuccessfully() {
        // Arrange
        when(actorJpaRepository.existsById(1)).thenReturn(true);

        // Act
        actorService.deleteActor(1);

        // Assert
        verify(actorJpaRepository).deleteById(1);
    }

    @Test
    void deleteActor_NonExistingId_ShouldThrowNotFoundException() {
        // Arrange
        when(actorJpaRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ActorNotFoundException.class,
            () -> actorService.deleteActor(999));
        
        verify(actorJpaRepository, never()).deleteById(anyInt());
    }

    // Search Methods Tests

    @Test
    void findActorsByLastName_ValidName_ShouldReturnActors() {
        // Arrange
        List<Actor> actors = Arrays.asList(testActor);
        when(actorJpaRepository.findActorsByLastName("Doe")).thenReturn(actors);

        // Act
        List<Actor> result = actorService.findActorsByLastName("Doe");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getLastName());
    }

    @Test
    void findActorsByLastName_EmptyName_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorsByLastName(""));
    }

    @Test
    void findActorsByLastName_NullName_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorsByLastName(null));
    }

    @Test
    void findActorsByFirstName_ValidName_ShouldReturnActors() {
        // Arrange
        List<Actor> actors = Arrays.asList(testActor);
        when(actorJpaRepository.findAll(any(Specification.class))).thenReturn(actors);

        // Act
        List<Actor> result = actorService.findActorsByFirstName("John");

        // Assert
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(actorJpaRepository).findAll(any(Specification.class));
    }

    @Test
    void findActorsByFirstName_TooLongName_ShouldThrowValidationException() {
        // Arrange
        String longName = "a".repeat(46); // Exceeds 45 character limit

        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorsByFirstName(longName));
    }

    // Business Logic Tests

    @Test
    void findActorByFullName_ExistingActor_ShouldReturnOptionalWithActor() {
        // Arrange
        when(actorJpaRepository.findFirstAsOptionalByFirstNameAndLastName("John", "Doe"))
                .thenReturn(Optional.of(testActor));

        // Act
        Optional<Actor> result = actorService.findActorByFullName("John", "Doe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
    }

    @Test
    void findActorByFullName_NonExistingActor_ShouldReturnEmptyOptional() {
        // Arrange
        when(actorJpaRepository.findFirstAsOptionalByFirstNameAndLastName("Jane", "Smith"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Actor> result = actorService.findActorByFullName("Jane", "Smith");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void searchActors_ValidTerm_ShouldReturnMatchingActors() {
        // Arrange
        List<Actor> actors = Arrays.asList(testActor);
        when(actorJpaRepository.findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc("Jo", "Jo"))
                .thenReturn(actors);

        // Act
        List<Actor> result = actorService.searchActors("Jo");

        // Assert
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void searchActors_EmptyTerm_ShouldReturnEmptyList() {
        // Act
        List<Actor> result = actorService.searchActors("");

        // Assert
        assertTrue(result.isEmpty());
        verify(actorJpaRepository, never()).findAllByLastNameStartingWithAndFirstNameContainingOrderByIdDesc(anyString(), anyString());
    }

    @Test
    void searchActors_NullTerm_ShouldReturnEmptyList() {
        // Act
        List<Actor> result = actorService.searchActors(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void countActorsByLastName_ValidName_ShouldReturnCount() {
        // Arrange
        when(actorJpaRepository.count(any(Specification.class))).thenReturn(5L);

        // Act
        long result = actorService.countActorsByLastName("Smith");

        // Assert
        assertEquals(5L, result);
        verify(actorJpaRepository).count(any(Specification.class));
    }

    @Test
    void findAllActorsByFullName_ValidNames_ShouldReturnActors() {
        // Arrange
        List<Actor> actors = Arrays.asList(testActor);
        when(actorJpaRepository.findAllByFirstNameAndLastName("John", "Doe"))
                .thenReturn(actors);

        // Act
        List<Actor> result = actorService.findAllActorsByFullName("John", "Doe");

        // Assert
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
    }

    // JDBC Repository Tests

    @Test
    void findActorsByLastNameUsingJdbc_ValidName_ShouldReturnActors() {
        // Arrange
        List<Actor> actors = Arrays.asList(testActor);
        when(actorJdbcTemplateRepository.findAllActorsWithLastName("Doe"))
                .thenReturn(actors);

        // Act
        List<Actor> result = actorService.findActorsByLastNameUsingJdbc("Doe");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getLastName());
        verify(actorJdbcTemplateRepository).findAllActorsWithLastName("Doe");
    }

    // Edge Cases and Input Validation Tests

    @Test
    void findActorsByLastName_WhitespaceOnlyName_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorsByLastName("   "));
    }

    @Test
    void findActorsByFirstName_WhitespaceOnlyName_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.findActorsByFirstName("   "));
    }

    @Test
    void createActor_FirstNameTooLong_ShouldThrowValidationException() {
        // Arrange
        String longFirstName = "a".repeat(46);
        Actor invalidActor = Actor.builder()
                .firstName(longFirstName)
                .lastName("Doe")
                .build();

        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.createActor(invalidActor));
    }

    @Test
    void createActor_LastNameTooLong_ShouldThrowValidationException() {
        // Arrange
        String longLastName = "a".repeat(46);
        Actor invalidActor = Actor.builder()
                .firstName("John")
                .lastName(longLastName)
                .build();

        // Act & Assert
        assertThrows(ActorValidationException.class,
            () -> actorService.createActor(invalidActor));
    }
}

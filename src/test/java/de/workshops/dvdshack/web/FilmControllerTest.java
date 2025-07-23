package de.workshops.dvdshack.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.workshops.dvdshack.application.FilmService;
import de.workshops.dvdshack.application.exception.FilmNotFoundException;
import de.workshops.dvdshack.repository.Film;
import de.workshops.dvdshack.repository.Language;
import de.workshops.dvdshack.repository.Rating;
import java.math.BigDecimal;
import java.time.Year;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllFilms() throws Exception {
        // Arrange
        Film film = createTestFilm();
        Page<Film> page = new PageImpl<>(Collections.singletonList(film), PageRequest.of(0, 10), 1);
        when(filmService.findAllFilms(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Film"));
    }

    @Test
    public void testGetFilmById_Found() throws Exception {
        // Arrange
        Film film = createTestFilm();
        when(filmService.findFilmById(1)).thenReturn(film);

        // Act & Assert
        mockMvc.perform(get("/api/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Film"));
    }

    @Test
    public void testGetFilmById_NotFound() throws Exception {
        // Arrange
        when(filmService.findFilmById(1)).thenThrow(new FilmNotFoundException("Film not found with id: 1"));

        // Act & Assert
        mockMvc.perform(get("/api/films/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Film Not Found"))
                .andExpect(jsonPath("$.detail").value("Film not found with id: 1"));
    }

    @Test
    public void testCreateFilm() throws Exception {
        // Arrange
        Film inputFilm = createTestFilm();
        inputFilm.setId(0); // ID should be 0 for creation
        
        Film savedFilm = createTestFilm();
        savedFilm.setId(1); // Service returns film with generated ID
        
        when(filmService.createFilm(any(Film.class))).thenReturn(savedFilm);

        // Act & Assert
        mockMvc.perform(post("/api/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputFilm)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Film"));
    }

    @Test
    public void testUpdateFilm_Success() throws Exception {
        // Arrange
        Film film = createTestFilm();
        when(filmService.updateFilm(eq(1), any(Film.class))).thenReturn(film);

        // Act & Assert
        mockMvc.perform(put("/api/films/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Film"));
    }

    @Test
    public void testUpdateFilm_NotFound() throws Exception {
        // Arrange
        Film film = createTestFilm();
        when(filmService.updateFilm(eq(1), any(Film.class))).thenThrow(new FilmNotFoundException("Film not found with id: 1"));

        // Act & Assert
        mockMvc.perform(put("/api/films/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Film Not Found"));
    }

    @Test
    public void testDeleteFilm_Success() throws Exception {
        // No specific arrangement needed - void method

        // Act & Assert
        mockMvc.perform(delete("/api/films/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteFilm_NotFound() throws Exception {
        // Arrange
        doThrow(new FilmNotFoundException("Film not found with id: 1")).when(filmService).deleteFilm(1);

        // Act & Assert
        mockMvc.perform(delete("/api/films/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Film Not Found"));
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setId(1);
        film.setTitle("Test Film");
        film.setDescription("A test film");
        film.setReleaseYear(Year.of(2023));
        film.setRentalDuration((short) 3);
        film.setRentalRate(new BigDecimal("4.99"));
        film.setLength((short) 120);
        film.setReplacementCost(new BigDecimal("19.99"));
        film.setRating(Rating.PG_13);
        
        // Create a basic Language object
        Language language = new Language();
        language.setId(1);
        language.setName("English");
        film.setLanguage(language);
        
        return film;
    }
}

package de.workshops.dvdshack.repository;

import java.sql.Array;
import java.time.Year;
import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FilmJdbcTemplateRepository {

    private final JdbcTemplate template;

    private final NamedParameterJdbcTemplate namedTemplate;

    public FilmJdbcTemplateRepository(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    public Integer findNumberOfFilmsPublishedInYear(Year year) {
        String sql = "SELECT count(film_id) FROM film WHERE release_year=?";

        return template.queryForObject(sql, Integer.class, year.getValue());
    }

    public List<Film> findAllFilmsWithRating(Rating rating) {
        final var ratingEnumValueConverter = new RatingEnumValueConverter();
        String sql = "SELECT film_id as id, title, rating FROM film WHERE rating=?";
        String ratingValue = ratingEnumValueConverter.toRelationalValue(rating);

        return template.query(sql, (rs, rowNum) -> {
            final var film = new Film();
            film.setId(rs.getInt(rs.findColumn("id")));
            film.setTitle(rs.getString(rs.findColumn("title")));
            film.setRating(ratingEnumValueConverter.toDomainValue(rs.getString(rs.findColumn("rating"))));
            return film;
        }, ratingValue);
    }

    public List<Film> findAllFilmsWithActor(Actor actor) {
        String sql = """
        SELECT f.film_id as id, f.title as title
        FROM film f
        JOIN film_actor fa ON f.film_id = fa.film_id
        JOIN actor a ON a.actor_id = fa.actor_id
        WHERE a.first_name=:firstname AND a.last_name=:lastname""";

        final var parameter = new MapSqlParameterSource();
        parameter.addValue("firstname", actor.getFirstName());
        parameter.addValue("lastname", actor.getLastName());

        return namedTemplate.query(sql, parameter, new BeanPropertyRowMapper<>(Film.class));
    }

    public List<Film> findAllFilmsWithMinimumLength(int minutes) {
        String sql = "SELECT film_id as id, title, length FROM film WHERE length >= ?";

        return template.query(sql, new BeanPropertyRowMapper<>(Film.class), minutes);
    }

    public List<Film> findAllFilmsHavingSpecialFeature(String specialFeature) {
        String sql = "SELECT film_id as id, title, special_features FROM film WHERE ? = ANY (special_features)";

        return template.query(sql, (rs, rowNum) -> {
            final var film = new Film();
            film.setId(rs.getInt("id"));
            film.setTitle(rs.getString("title"));
            Object obj = rs.getObject("special_features");
            if (obj instanceof Array array) {
                final String[] stringArray = (String[]) array.getArray();
                film.setSpecialFeatures(List.of(stringArray));
            }
            return film;
        }, specialFeature);
    }
}

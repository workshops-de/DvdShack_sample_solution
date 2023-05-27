package de.workshops.dvdshack.repository;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActorJdbcTemplateRepository {
    private final JdbcTemplate template;

    public ActorJdbcTemplateRepository(JdbcTemplate template) {
        this.template = template;
    }

    public List<Actor> findAllActorsWithLastName(String lastName) {
        String sql = "SELECT actor_id as id, first_name, last_name, last_update FROM actor WHERE last_name=?";

        return template.query(sql, new BeanPropertyRowMapper<>(Actor.class), lastName);
    }

    public void createActor(Actor actor) {
        template.update("INSERT INTO actor (first_name, last_name) VALUES (?, ?)"
                , actor.getFirstName(), actor.getLastName());
    }

    public void updateActor(Actor actor) {
        template.update("UPDATE actor SET first_name=?, last_name=? WHERE actor_id=?"
                , actor.getFirstName(), actor.getLastName(), actor.getId());
    }
    public void deleteActor(Integer id) {
        template.execute("DELETE FROM actor WHERE actor_id=%d".formatted(id));
    }
}

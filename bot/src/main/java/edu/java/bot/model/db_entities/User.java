package edu.java.bot.model.db_entities;

import edu.java.bot.model.SessionState;
import java.net.URI;
import java.util.List;

/**
 * User entity
 */
//TODO разбить сущность на отдельные таблицы - пользователи, пользовательская активность,
public class User {
    //PK entity
    private Long id;
    private List<URI> sites;

    private SessionState state;

    public User(Long id, List<URI> sites, SessionState state) {
        this.id = id;
        this.sites = sites;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public List<URI> getSites() {
        return sites;
    }

    public SessionState getState() {
        return state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSites(List<URI> sites) {
        this.sites = sites;
    }

    public void setState(SessionState state) {
        this.state = state;
    }
}

package edu.java.bot.model.db_entities;

import edu.java.bot.model.SessionState;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity
 */
//TODO разбить сущность на отдельные таблицы - пользователи, пользовательская активность
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //PK entity
    private Long id;
    private List<URI> sites;
    private SessionState state;

    public boolean isWaitingTrack() {
        return this.state.equals(SessionState.WAIT_URI_FOR_TRACKING);
    }

    public boolean isWaitingUntrack() {
        return this.state.equals(SessionState.WAIT_URI_FOR_UNTRACKING);
    }
}

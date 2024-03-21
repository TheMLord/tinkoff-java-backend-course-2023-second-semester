package edu.java.bot.domain;

import edu.java.bot.models.SessionState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TgChat entity
 */
//TODO разбить сущность на отдельные таблицы - пользователи, пользовательская активность
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TgChat {
    //PK entity
    private Long id;
    private SessionState state;

    public boolean isWaitingTrack() {
        return this.state.equals(SessionState.WAIT_URI_FOR_TRACKING);
    }

    public boolean isWaitingUntrack() {
        return this.state.equals(SessionState.WAIT_URI_FOR_UNTRACKING);
    }
}

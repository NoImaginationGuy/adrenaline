package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

/**
 * The given player received a new score amount.
 * @see UserEvent to see what this class is used for.
 */
public class PlayerScoreEvent implements UserEvent {

    private PlayerColor color;

    private int score;

    public PlayerScoreEvent(PlayerColor color, int score) {
        this.color = color;
        this.score = score;
    }

    public PlayerColor getPlayerColor() {
        return color;
    }

    public int getNewScore() {
        return score;
    }


    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}

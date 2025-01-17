package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

/**
 * The given player obtained the control of the game.
 * @deprecated This event is not used anymore.
 * @see UserEvent to see what this class is used for.
 */
public class PlayerActiveEvent implements UserEvent {

    private PlayerColor playerColor;

    public PlayerActiveEvent(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }


    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}

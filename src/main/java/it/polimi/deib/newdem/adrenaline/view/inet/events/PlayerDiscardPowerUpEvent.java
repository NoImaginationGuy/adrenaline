package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

/**
 * The given player discarded the power up with the given ID, according to the used power up deck.
 * @see UserEvent to see what this class is used for.
 */
public class PlayerDiscardPowerUpEvent implements UserEvent {

    private PlayerColor color;
    private int cardID;

    public PlayerDiscardPowerUpEvent(PlayerColor color, int cardID) {
        this.color = color;
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }

    public PlayerColor getColor() {
        return color;
    }


    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}

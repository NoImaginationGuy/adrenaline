package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * The server is requesting the user to choose a card fragment among the given ones.
 * if {@code forceChoice} is true, then a {@code null} will not be accepted from the server.
 * @see UserEvent to see what this class is used for.
 */
public class TurnFragmentRequest implements UserEvent {

    private Integer cardID;
    private ArrayList<Integer> fragments;
    private boolean forceChoice;

    public TurnFragmentRequest(Integer cardID, List<Integer> fragments, boolean forceChoice) {
        this.cardID = cardID;
        this.fragments = new ArrayList<>(fragments);
        this.forceChoice = forceChoice;
    }

    public Integer getCardID() {
        return cardID;
    }

    public List<Integer> getSelectableFragments() {
        return fragments;
    }

    public boolean isChoiceForced() {
        return forceChoice;
    }

    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}

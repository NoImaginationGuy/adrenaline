package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

/**
 * The timer in the lobby sent a tick to update the countdown to the given seconds left.
 * @see UserEvent to see what this class is used for.
 */
public class LobbyTimerUpdateEvent implements UserEvent {

    private int seconds;

    /**
     * Create a new event indicating that the timer is being updated (server bound event)
     * @param seconds The seconds left on the timer.
     */
    public LobbyTimerUpdateEvent(int seconds) {
        this.seconds = seconds;
    }

    public int getSecondsLeft() {
        return seconds;
    }


    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}

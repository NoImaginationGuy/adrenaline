package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;

public class ExitLobbyEvent implements UserEvent {

    private String username;

    /**
     * Create a new exit lobby event indicating that the user with the specified username left the lobby (server bound event)
     * @param username The name provided by the new user.
     */
    public ExitLobbyEvent(String username) {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }


    @Override
    public void publish(UserConnection connection) {
        connection.publishEvent(this);
    }

}
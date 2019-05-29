package it.polimi.deib.newdem.adrenaline.view.server;

import it.polimi.deib.newdem.adrenaline.model.mgmt.LobbyListener;
import it.polimi.deib.newdem.adrenaline.model.mgmt.Lobby;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.GameView;
import it.polimi.deib.newdem.adrenaline.view.LobbyView;
import it.polimi.deib.newdem.adrenaline.view.inet.events.*;


public class VirtualLobbyView implements LobbyView, LobbyListener {

    private Lobby lobby;

    public VirtualLobbyView(Lobby lobby) {
        this.lobby = lobby;
    }


    private void sendEvent(UserEvent event) {
        for (User user : lobby.getUsers()) {
            user.sendEvent(event);
        }
    }


    @Override
    public void addGameView(GameView gv) {
        // TODO
    }

    @Override
    public void addUser(String name) {
        sendEvent(new EnterLobbyEvent(name));
    }

    @Override
    public void removeUser(String name) {
        sendEvent(new ExitLobbyEvent(name));
    }

    @Override
    public void startTimer(int seconds) {
        sendEvent(new LobbyTimerUpdateEvent(seconds));
    }

    @Override
    public void syncTimer(int seconds) {
        sendEvent(new LobbyTimerUpdateEvent(seconds));
    }




    @Override
    public void userDidEnterLobby(User user, Lobby lobby) {
        addUser(user.getName());
    }

    @Override
    public void userDidExitLobby(User user, Lobby lobby) {
        removeUser(user.getName());
    }

    @Override
    public void lobbyDidStartTimer(int seconds) {
        startTimer(seconds);
    }

    @Override
    public void lobbyDidSyncTimer(int seconds) {
        syncTimer(seconds);
    }

}

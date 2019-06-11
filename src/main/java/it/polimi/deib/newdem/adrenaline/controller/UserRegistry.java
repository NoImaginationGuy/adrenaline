package it.polimi.deib.newdem.adrenaline.controller;

import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.model.mgmt.UserListener;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;
import it.polimi.deib.newdem.adrenaline.view.inet.events.RejectUsernameEvent;
import it.polimi.deib.newdem.adrenaline.view.inet.events.UpdateUsernameRequest;
import it.polimi.deib.newdem.adrenaline.view.inet.events.UpdateUsernameResponse;
import it.polimi.deib.newdem.adrenaline.view.inet.events.UserEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserRegistry implements UserListener {

    private List<User> unnamedUsers;

    private HashMap<String, User> userNames;

    private ServerInstance core;

    public UserRegistry(ServerInstance core){
        this.unnamedUsers = new ArrayList<>();
        this.userNames = new HashMap<>();

        this.core = core;
    }

    public void registerUser(User user) {
        core.getLogger().info(String.format("User %s joined the server.", user.hashCode()));

        unnamedUsers.add(user);
        user.addListener(this);

        user.subscribeEvent(UpdateUsernameResponse.class, this::changeName);
        user.sendEvent(new UpdateUsernameRequest());
    }

    private void changeName(UserConnection connection, UpdateUsernameResponse event) {
        setNameForUser(event.getNewUsername(), connection.getUser());
    }

    private synchronized void setNameForUser(String name, User user) {
        if (userNames.get(name) != null) {
            core.getLogger().info(String.format("User %s tried to update their name to %s, which is already taken.", user.hashCode(), name));
            user.sendEvent(new RejectUsernameEvent(name));
        } else {
            userNames.put(name, user);
            user.setName(name);
            unnamedUsers.remove(user);
            user.unsubscribeEvent(UpdateUsernameResponse.class, this::changeName);

            core.getLogger().info(String.format("User %s successfully changed their name to %s.", user.hashCode(), name));
        }
    }

    public void unregisterUser(User user) {
        core.getLogger().info(String.format("User %s left the server.", user.hashCode()));

        user.removeListener(this);
        if (user.getName() != null) {
            userNames.remove(user.getName());
        } else {
            unnamedUsers.remove(user);
        }

    }

    public User getUserByName(String name){
        return userNames.get(name);
    }

    @Override
    public void userDidChangeConnection(User user, UserConnection oldConnection, UserConnection newConnection) {
        LobbyRegistry lobbyRegistry = core.getLobbyRegistry();

        if (!user.isConnected() && !lobbyRegistry.userHasLobby(user)) {
            unregisterUser(user);
        }
    }

    @Override
    public void userDidChangeName(User user, String name) {
        // nothing to do here.
    }

}

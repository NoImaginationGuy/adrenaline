package it.polimi.deib.newdem.adrenaline.controller;

import it.polimi.deib.newdem.adrenaline.model.mgmt.*;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualLobbyView;

public class LobbyControllerImpl implements LobbyController, TimerListener, UserListener {

    private LobbyRegistry registry;

    private Config config;

    private Thread mainThread;

    private Lobby lobby;

    private LobbyState lobbyState;

    private GameController gameController;

    LobbyControllerImpl(LobbyRegistry registry, Config config) {
        this.registry = registry;

        this.config = config;

        this.lobby = new LobbyImpl();

        VirtualLobbyView view = new VirtualLobbyView(lobby);
        this.lobby.setListener(view);

        this.gameController = null;

        this.switchState(new ReadyLobbyState());
    }

    /**
     * Changes the state of the lobby to the given state.
     */
    private synchronized void switchState(LobbyState newState) {
        if (newState != this.lobbyState) {
            if (lobbyState != null) {
                lobbyState.lobbyDidExitState(this);
            }

            newState.lobbyWillEnterState(this);
        }

        this.lobbyState = newState;
    }

    @Override
    public Lobby getLobby() {
        return lobby;
    }

    @Override
    public synchronized void addUser(User user) {
        if(!this.lobby.getUsers().contains(user)) {
            user.addListener(this);
        }
        this.lobby.addUser(user);

        this.switchState(lobbyState.userDidEnterLobby(user, this));
    }

    @Override
    public synchronized void removeUser(User user) {
        this.lobby.removeUser(user);
        user.removeListener(this);

        this.switchState(lobbyState.userDidExitLobby(user, this));
    }

    @Override
    public TimerListener getTimerListener() {
        return this;
    }

    @Override
    public synchronized boolean acceptsNewUsers() {
        return lobbyState.acceptsNewUsers();
    }

    @Override
    public void startGame() {
        lobby.startGame();

        this.gameController = config.getGameControllerFactory().makeGameController(this);
        // ^ this may fail now, due to invalid json for decks.
        // still not lazy enough?
        this.gameController.setupGame(lobby.getUsers());

        this.mainThread = new Thread(gameController::runGame);
        this.mainThread.start();
    }

    @Override
    public void endGame() {
        switchState(new GameOverLobbyState());

        if (registry != null) {
            registry.freeLobbyController(this);
        }
    }

    @Override
    public void timerWillStart(int secondsLeft) {
        lobby.startTimer(secondsLeft);
    }

    @Override
    public void timerSync(int secondsLeft) {
        lobby.refreshTimer(secondsLeft);
    }

    @Override
    public void timerDidFinish() {
        if (lobby.getUsers().size() >= config.getMinPlayers()) {
            this.switchState(new InGameLobbyState());
        } else {
            this.switchState(new TimerWaitingLobbyState());
        }
    }

    @Override
    public void timerDidAbort() {
        lobby.abortTimer();
    }

    @Override
    public void userDidChangeConnection(User user, UserConnection oldConnection, UserConnection newConnection) {
        if (gameController != null) {
            if (oldConnection != null && newConnection == null) {
                // user disconnected
                gameController.userDidDisconnect(user);
            } else if (oldConnection == null && newConnection != null) {
                // user reconnected
                gameController.userDidReconnect(user);
            }
        }
    }

    @Override
    public void userDidChangeName(User user, String name) {
        // should never happen for now.
    }

    @Override
    public Config getConfig() {
        return config;
    }
}

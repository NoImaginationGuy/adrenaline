package it.polimi.deib.newdem.adrenaline.common.controller;

import it.polimi.deib.newdem.adrenaline.common.controller.effects.UserRegistry;
import it.polimi.deib.newdem.adrenaline.common.model.mgmt.User;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerInstance {

    private Logger log;

    private Config config;

    private UserGreeter greeter;

    private boolean ready;

    /**
     * Creates a new server instance.
     * @param log The Logger object that will accept incoming diagnostics from the server
     * @param config The configuration object the server will set
     */
    public ServerInstance(Logger log, Config config) {
        this.log = log;
        this.config = config;
        this.greeter = new UserGreeter();

        if (config.isSocketActive()) {
            greeter.addUserModule(new SocketUserModule(config.getSocketPort()));
        }

        if (config.isRMIActive()) {
            greeter.addUserModule(new RMIUserModule());
        }

        this.ready = false;
    }

    /**
     * Initializes the server instance.
     */
    public void init() {
        greeter.init();

        ready = true;
    }

    /**
     * Starts the server instance in the current thread.
     * @throws NotReadyException if the server instance was not initialized before starting.
     * @implNote To initialize the server instance, please call ServerInstance.init()
     */
    public void start() throws NotReadyException {
        UserRegistry userRegistry = UserRegistry.getRegistry();

        LobbyRegistry lobbyRegistry = LobbyRegistry.getRegistry();

        if (!ready || userRegistry == null || lobbyRegistry == null) {
            throw new NotReadyException("This server instance was not initialized. Please call init() before starting.");
        }

        greeter.start();

        boolean stopServer = false;
        while (!stopServer) {
            try {
                User incomingUser = greeter.accept();

                userRegistry.registerUser(incomingUser);
                log.log(Level.INFO, String.format("User %s joined the server.", incomingUser.getName()));
            } catch (Exception e) {
                log.log(Level.SEVERE, "Server encountered error: "+ e.getMessage());

                stopServer = true;
            }
        }

    }

}

package it.polimi.deib.newdem.adrenaline.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

/**
 * A configuration object mantaining all the information used by the server.
 */
public final class Config {

    private boolean useSockets = true;

    private boolean useRMI = true;

    private int socketPort = 9700;

    private int rmiPort = 9701;

    private String rmiIdentifier = "adrenaline";


    private int timerLength = 3;

    private int minPlayers = 3;

    private int maxPlayers = 5;

    private int turnTime = 1;

    private GameControllerFactory gameMaker = new AdrenalineGameControllerFactory();

    private int killTrackLength = 8;


    private boolean debugMode = false;

    private Config() {  }


    private static JsonElement getMember(JsonObject object, String id) throws InvalidConfigException {
        JsonElement elem = object.get(id);
        if (elem == null) throw new InvalidConfigException("Missing value for "+ id);
        return elem;
    }

    /**
     * Parses the given configuration file into a new configuration info object.
     * @param fileName the filename of the configuration file that has to be read.
     * @return a new {@code Config} object containing all the information retrieved by the given file.
     */
    public static Config fromFile(String fileName) throws InvalidConfigException {
        Config config = new Config();

        try (FileReader fread = new FileReader(fileName)) {
            JsonObject configJson = new JsonParser().parse(fread).getAsJsonObject();

            config.useSockets = getMember(configJson, "useSockets").getAsBoolean();
            config.useRMI = getMember(configJson, "useRMI").getAsBoolean();
            config.socketPort = getMember(configJson, "socketPort").getAsInt();
            config.rmiPort = getMember(configJson, "rmiPort").getAsInt();
            config.rmiIdentifier = getMember(configJson, "rmiIdentifier").getAsString();

            config.timerLength = getMember(configJson, "timerLength").getAsInt();
            config.minPlayers = getMember(configJson, "minPlayers").getAsInt();
            config.maxPlayers = getMember(configJson, "maxPlayers").getAsInt();
            config.turnTime = getMember(configJson, "turnTime").getAsInt();

            config.killTrackLength = getMember(configJson, "killTrackLength").getAsInt();

            if (configJson.get("debugMode") != null) {
                config.debugMode = getMember(configJson, "debugMode").getAsBoolean();
            }

            config.checkIntegrity();

        } catch (Exception x) {
            throw new InvalidConfigException(x.getMessage());
        }

        return config;
    }

    private void checkIntegrity() throws InvalidConfigException {

        if (!(0 <= socketPort && socketPort < 65536))
            throw new InvalidConfigException("Socket port must be a valid port.");

        if (!(0 <= rmiPort && rmiPort < 65536))
            throw new InvalidConfigException("RMI port must be a valid port.");

        if (timerLength <= 0)
            throw new InvalidConfigException("Timer length must be positive.");

        if (minPlayers > maxPlayers)
            throw new InvalidConfigException("Min players must be lower than max players.");

        if (minPlayers <= 0)
            throw new InvalidConfigException("Min players must be positive.");

        if (maxPlayers > 5)
            throw new InvalidConfigException("Max players must be at most 5.");

        if (turnTime <= 0)
            throw new InvalidConfigException("Turn time must be positive.");

        if (!(5 <= killTrackLength && killTrackLength <= 8))
            throw new InvalidConfigException("Kill Track Length must be between 5 and 8 (inclusive).");

    }


    /**
     * Returns the hardcoded default configuration.
     * @implNote This should only be used in absence of a configuration file.
     */
    public static Config getDefaultConfig() {
        return new Config();
    }

    /**
     * Returns the length in seconds that timers used by the controller should have when the lobby enters in the countdown state.
     */
    public int getTimerLength() {
        return timerLength;
    }

    /**
     * Returns the minimum numbers of players that lobbies in this server need to start a new game.
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Returns the maximum numbers of players that lobbies in this server need to start the game immediately.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Returns the TCP port the socket of the server should listen to new socket connections from.
     */
    public int getSocketPort() {
        return socketPort;
    }

    /**
     * Returns the TCP port the socket of the server should listen to new RMI connections from.
     */
    public int getRMIPort() { return rmiPort; }

    /**
     * Return the URI for the server object that will wait for new RMI user connections.
     */
    public String getRMIIdentifier() { return rmiIdentifier; }

    /**
     * Returns whether the socket module should be added to the modules used by the server instance.
     */
    public boolean isSocketActive(){
        return useSockets;
    }

    /**
     * Returns whether the RMI module should be added to the modules used by the server instance.
     */
    public boolean isRMIActive(){
        return useRMI;
    }

    /**
     * Returns the factory object used to create new game controllers for the .
     */
    public GameControllerFactory getGameControllerFactory() {
        return gameMaker;
    }

    /**
     * Returns the time for an ordinary turn in seconds
     */
    public int getTurnTime(){
        return turnTime;
    }

    /**
     * Returns whether the server instance that uses this configuration should run in debug mode.
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Returns the length of the kill tracks used in Adrenaline games.
     */
    public int getKillTrackLength() {
        return killTrackLength;
    }

}

package it.polimi.deib.newdem.adrenaline.model.game;

import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrack;
import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackData;
import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackListener;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.items.*;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;

import java.util.List;

/**
 * A running game of Adrenaline
 */
public interface Game {

    /**
     * Returns the Map object representing the map used in this game.
     */
    Map getMap();

    void setGameListener(GameListener listener);

    void setKillTrackListener(KillTrackListener listener);

    /**
     * Returns the player object corresponding to the given player color.
     * If no player is associated to the given color for this game, then {@code null} is returned.
     */
    Player getPlayerFromColor(PlayerColor color);

    /**
     * Returns a list of players, starting from the first player and in the order they should play.
     */
    List<Player> getPlayers();

    /**
     * Returns whether the game turned into frenzy mode.
     */
    boolean isInFrenzy();

    /**
     * Returns whether the game should go in frenzy mode.
     */
    boolean shouldGoFrenzy();

    /**
     * Returns the Turn object that should be used for the next player's turn.
     */
    Turn getNextTurn();

    /**
     * Finalizes the given Turn.
     */
    void concludeTurn(Turn turn);

    /**
     * Sets the user to be associated with the player with the given color.
     */
    void setUserForColor(User user, PlayerColor color);

    /**
     * Returns the User object associated to the given player.
     */
    User getUserByPlayer(Player player);

    /**
     * Initializes the Game object and all the objects that come with it.
     */
    void init();

    /**
     * Returns whether the game is over.
     */
    boolean isOver();

    /**
     * Declares that the game is over.
     */
    void declareOver();

    /**
     * Returns the time allowed for turns in seconds
     */
    int getTurnTime();

    /**
     * Returns the deck of power up cards used in the game.
     */
    Deck<PowerUpCard> getPowerUpDeck();

    /**
     * Returns the deck of power up cards used in the game.
     *
     * @return this game's drop deck
     */
    Deck<DropInstance> getDropDeck();

    /**
     * Generates a new {@code GameData} object for this {@code Game}  based on
     * its current state
     * @return game snapshot
     */
    GameData generateGameData();

    /**
     * Generates a new {@code KillTrackData} object for this {@code Game}'s {@code KillTrack}
     * based on its current state
     * @return Killtrack snapshot
     */
    KillTrackData generateKillTrackData();

    /**
     *
     * @return
     */
    GameResults generateResults();

    void concludeGame();

    KillTrack getKillTrack();

}

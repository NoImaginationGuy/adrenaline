package it.polimi.deib.newdem.adrenaline.model.game.player;

import it.polimi.deib.newdem.adrenaline.controller.actions.ActionFactory;
import it.polimi.deib.newdem.adrenaline.model.game.DamageBoard;
import it.polimi.deib.newdem.adrenaline.model.game.Game;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

import java.util.List;

public interface Player {

    Game getGame();

    PlayerListener getListener();

    String getName();

    Tile getTile();

    void setTile(Tile dest);

    PlayerInventory getInventory();

    PlayerColor getColor();

    List<ActionFactory> getMoves();

    int getMovesAmount();

    int getDeaths();

    int getTotalDamage();

    Player getDamager(int cell);

    int getDamageFromPlayer(Player player);

    int getMarksFromPlayer(Player player);

    boolean isDead();

    void registerDamageBoard(DamageBoard damageBoard);

    void goFrenzy(boolean precedesFirstPlayer);

    int getScoreForPlayer(Player player);

    void assignFirstPlayerCard();

    void init();

    boolean hasFirstPlayerCard();

    void addScore(int points);

    int getScore();

    void setListener(PlayerListener listener);

    boolean isConnected();

    void drawCard();

    DamageBoard getDamageBoard();

    void reportDeath(boolean isDead);

    void addSkull();

    boolean isActionBoardFrenzy();

    PlayerData generatePlayerData();
}

package it.polimi.deib.newdem.adrenaline.common.model.game.changes;

import it.polimi.deib.newdem.adrenaline.common.model.game.Game;
import it.polimi.deib.newdem.adrenaline.common.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.common.model.game.Player;
import it.polimi.deib.newdem.adrenaline.common.model.map.Tile;

public class MovementGameChange implements GameChange {

    private Player player;

    private Tile sourceTile;
    private Tile destinationTile;

    public MovementGameChange(Player player, Tile srcTile, Tile destTile){
        //TODO
    }

    @Override
    public void update(Game game) {
        //TODO
    }

    @Override
    public void revert(Game game) {
        //TODO
    }
}
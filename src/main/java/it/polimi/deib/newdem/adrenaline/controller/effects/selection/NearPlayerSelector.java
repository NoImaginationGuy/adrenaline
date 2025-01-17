package it.polimi.deib.newdem.adrenaline.controller.effects.selection;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

public class NearPlayerSelector implements PlayerSelector {

    private Tile sourceTile;
    private int minDistance;
    private int maxDistance;

    /**
     * Selects all the players in the given distance range from the given tile.
     * @see PlayerSelector for further information
     */
    public NearPlayerSelector(Tile sourceTile, int minDistance, int maxDistance){
        this.sourceTile = sourceTile;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public NearPlayerSelector(Player sourcePlayer, int minDistance, int maxDistance) {
        this(sourcePlayer.getTile(), minDistance, maxDistance);
    }

    @Override
    public boolean isSelectable(Map map, Player player) {
        if (player.getTile() == null) return false;

        int distance = sourceTile.distanceFrom(player.getTile());

        return minDistance <= distance && distance <= maxDistance;
    }
}

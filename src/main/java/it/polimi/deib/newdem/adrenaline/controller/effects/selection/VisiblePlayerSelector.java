package it.polimi.deib.newdem.adrenaline.controller.effects.selection;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.map.Room;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

import java.util.ArrayList;
import java.util.List;

public class VisiblePlayerSelector implements PlayerSelector {

    private Player sourcePlayer;

    /**
     * Selects all the players that are visible from the given player.
     * @see PlayerSelector for further information
     */
    public VisiblePlayerSelector(Player sourcePlayer) {
        this.sourcePlayer = sourcePlayer;
    }

    @Override
    public boolean isSelectable(Map map, Player player) {
        if (player.getTile() == null) {
            return false;
        }

        List<Room> visibleRooms = new ArrayList<>();
        visibleRooms.add(sourcePlayer.getTile().getRoom());

        for (Tile adjT : sourcePlayer.getTile().getAdjacentTiles()) {
            visibleRooms.add(adjT.getRoom());
        }

        return visibleRooms.contains(player.getTile().getRoom());
    }
}

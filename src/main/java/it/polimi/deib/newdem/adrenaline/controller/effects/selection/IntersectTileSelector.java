package it.polimi.deib.newdem.adrenaline.controller.effects.selection;

import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

public class IntersectTileSelector implements TileSelector {

    private TileSelector firstOp;
    private TileSelector secondOp;

    /**
     * Selects all the tiles selected by BOTH the given selectors.
     * @see TileSelector for further information
     */
    public IntersectTileSelector(TileSelector firstOp, TileSelector secondOp) {
        this.firstOp = firstOp;
        this.secondOp = secondOp;
    }

    @Override
    public boolean isSelectable(Map map, Tile tile) {
        return firstOp.isSelectable(map, tile) && secondOp.isSelectable(map, tile);
    }
}

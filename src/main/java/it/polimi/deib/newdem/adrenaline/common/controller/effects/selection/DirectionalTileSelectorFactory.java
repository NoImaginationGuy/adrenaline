package it.polimi.deib.newdem.adrenaline.common.controller.effects.selection;

import it.polimi.deib.newdem.adrenaline.common.controller.effects.EffectVisitor;

public class DirectionalTileSelectorFactory implements TileSelectorFactory {

    private int minDistance;
    private int maxDistance;
    private boolean ignoreWalls;

    public DirectionalTileSelectorFactory(int minDist, int maxDist, boolean ignoreWalls){
        //TODO
    }

    @Override
    public TileSelector makeSelector(EffectVisitor visitor) {
        //TODO
        return null;
    }
}
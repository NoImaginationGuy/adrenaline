package it.polimi.deib.newdem.adrenaline.common.controller.effects;

import it.polimi.deib.newdem.adrenaline.common.model.game.Player;

import java.util.List;

public class NearPlayerSelectorFactory implements PlayerSelectorFactory {

    private MetaPlayer sourcePlayer;
    int minDistance;
    int maxDistance;

    public NearPlayerSelectorFactory(MetaPlayer sourcePlayer, int minDistance, int maxDistance){
        //TODO
    }

    @Override
    public PlayerSelector makeSelector(EffectVisitor visitor, List<Player> excluded) {
        //TODO

        return null;
    }
}
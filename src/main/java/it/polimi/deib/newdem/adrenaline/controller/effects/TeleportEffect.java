package it.polimi.deib.newdem.adrenaline.controller.effects;

import it.polimi.deib.newdem.adrenaline.controller.effects.selection.AnyTileSelector;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

/**
 * Card effect that implements Adrenaline' Chainsaw power up card.
 * @see Effect for further information about the card effects.
 */
public class TeleportEffect implements Effect {

    @Override
    public void apply(EffectManager manager, Player actor) throws UndoException {

        Tile destinationTile = manager.bindTile(new AnyTileSelector());


        manager.movePlayer(actor, destinationTile);

    }

}

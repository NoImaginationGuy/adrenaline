package it.polimi.deib.newdem.adrenaline.model.game.changes;

import it.polimi.deib.newdem.adrenaline.model.game.Game;
import it.polimi.deib.newdem.adrenaline.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoSet;

/**
 * GameChange object that removes the given ammos from the given player.
 */
public class AmmoLossGameChange implements GameChange {

    private Player player;
    private AmmoSet ammos;

    /**
     * Builds a new AmmoLossGameChange for the given {@code Player} and {@code AmmoSet}
     * @param player to change the inventory of
     * @param ammos to remove from the {@code Player}'s inventory
     */
    public AmmoLossGameChange(Player player, AmmoSet ammos) {
        this.ammos = ammos;
        this.player = player;
    }

    @Override
    public void update(Game game) {
        player.getInventory().removeAmmoSet(ammos);
    }

    @Override
    public void revert(Game game) {
        player.getInventory().addAmmoSet(ammos);
    }
}

package it.polimi.deib.newdem.adrenaline.model.game.changes;

import it.polimi.deib.newdem.adrenaline.model.game.Game;
import it.polimi.deib.newdem.adrenaline.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.Weapon;

public class WeaponReloadGameChange implements GameChange {

    private Player player;
    private Weapon weapon;

    public WeaponReloadGameChange(Player player, Weapon weapon){
        this.player = player;
        this.weapon = weapon;
    }

    @Override
    public void update(Game game) {
        player.getInventory().addWeapon(weapon);
    }

    @Override
    public void revert(Game game) {
        //TODO
    }
}

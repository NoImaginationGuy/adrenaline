package it.polimi.deib.newdem.adrenaline.model.game.player;

import it.polimi.deib.newdem.adrenaline.model.items.AmmoSet;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpCard;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;

public final class NullPlayerListener implements PlayerListener {

    @Override
    public void playerDidInit(PlayerData data) {
        // nothing to do here.
    }

    @Override
    public void playerDidReceivePowerUpCard(Player player, PowerUpCard powerUpCard) {
        // nothing to do here.
    }

    @Override
    public void playerDidDiscardPowerUpCard(Player player, PowerUpCard powerUpCard) {
        // nothing to do here.
    }

    @Override
    public void playerDidReceiveWeaponCard(Player player, WeaponCard weaponCard) {
        // nothing to do here.
    }

    @Override
    public void playerDidDiscardWeaponCard(Player player, WeaponCard weaponCard) {
        // nothing to do here.
    }

    @Override
    public void playerDidUpdateScore(Player player, int newScore) {
        // nothing to do here.
    }

    @Override
    public void playerDidReceiveAmmos(Player player, AmmoSet ammos) {
        // nothing to do here.
    }

    @Override
    public void playerDidDiscardAmmos(Player player, AmmoSet ammos) {
        // nothing to do here.
    }

    @Override
    public void playerDidUnloadWeaponCard(Player player, WeaponCard weaponCard) {
        // nothing to do here.
    }

    @Override
    public void playerDidReloadWeaponCard(Player player, WeaponCard weaponCard) {
        // nothing to do here.
    }
}

package it.polimi.deib.newdem.adrenaline.view.server;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerData;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerListener;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoSet;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpCard;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.PlayerView;
import it.polimi.deib.newdem.adrenaline.view.inet.events.*;

/**
 * A virtual view is a view object that acts as an adapter between model/controller and views,
 * translating the model objects into plain data objects usable by views.
 * This way the view is completely separated from the model and we do not need to clone/reflect
 * model objects into the client.
 *
 * Note: the VirtualGameView is also used to give information about the in-game users to the other
 * virtual views.
 */
public class VirtualPlayerView implements PlayerView, PlayerListener {

    private VirtualGameView gameView;

    private Player player;

    public VirtualPlayerView(VirtualGameView gameView, Player player) {
        this.gameView = gameView;
        this.player = player;
    }


    @Override
    public void setPlayerData(PlayerData data) {
        gameView.sendEvent(new PlayerDataEvent(data));
    }

    @Override
    public void setName(String name) {
        gameView.sendEvent(new PlayerNameEvent(player.getColor(), name));
    }

    @Override
    public void setScore(int score) {
        gameView.sendEvent(new PlayerScoreEvent(player.getColor(), score));
    }

    @Override
    public void addPowerUpCard(int cardID) {
        for (PlayerColor color : gameView.getPlayers()) {
            User user = gameView.getUserFromColor(color);
            if (color == player.getColor()) {
                user.sendEvent(new PlayerAcquirePowerUpEvent(player.getColor(), cardID));
            } else {
                user.sendEvent(new PlayerAcquirePowerUpEvent(player.getColor(), PowerUpCard.HIDDEN));
            }
        }
    }

    @Override
    public void removePowerUpCard(int cardID) {
        for (PlayerColor color : gameView.getPlayers()) {
            User user = gameView.getUserFromColor(color);
            if (color == player.getColor()) {
                user.sendEvent(new PlayerDiscardPowerUpEvent(player.getColor(), cardID));
            } else {
                user.sendEvent(new PlayerDiscardPowerUpEvent(player.getColor(), PowerUpCard.HIDDEN));
            }
        }
    }

    @Override
    public void addWeaponCard(int cardID) {
        gameView.sendEvent(new PlayerAcquireWeaponEvent(player.getColor(), cardID));
    }

    @Override
    public void removeWeaponCard(int cardID) {
        gameView.sendEvent(new PlayerDiscardWeaponEvent(player.getColor(), cardID));
    }

    @Override
    public void addAmmoSet(int yellowAmount, int redAmount, int blueAmount) {
        gameView.sendEvent(new PlayerReceiveAmmoEvent(player.getColor(), yellowAmount, redAmount, blueAmount));
    }

    @Override
    public void removeAmmoSet(int yellowAmount, int redAmount, int blueAmount) {
        gameView.sendEvent(new PlayerDiscardAmmoEvent(player.getColor(), yellowAmount, redAmount, blueAmount));
    }

    @Override
    public void reloadWeaponCard(int cardID) {
        gameView.sendEvent(new WeaponReloadEvent(player.getColor(), cardID));
    }

    @Override
    public void unloadWeaponCard(int cardID) {
        gameView.sendEvent(new WeaponUnloadEvent(player.getColor(), cardID));
    }



    @Override
    public void playerDidInit(PlayerData data) {
        setPlayerData(data);
    }

    @Override
    public void playerDidReceivePowerUpCard(Player player, PowerUpCard powerUpCard) {
        addPowerUpCard(powerUpCard.getCardID());
    }

    @Override
    public void playerDidDiscardPowerUpCard(Player player, PowerUpCard powerUpCard) {
        removePowerUpCard(powerUpCard.getCardID());
    }

    @Override
    public void playerDidReceiveWeaponCard(Player player, WeaponCard weaponCard) {
        addWeaponCard(weaponCard.getCardID());
    }

    @Override
    public void playerDidDiscardWeaponCard(Player player, WeaponCard weaponCard) {
        removeWeaponCard(weaponCard.getCardID());
    }

    @Override
    public void playerDidUpdateScore(Player player, int newScore) {
        setScore(newScore);
    }

    @Override
    public void playerDidReceiveAmmos(Player player, AmmoSet ammos) {
        addAmmoSet(ammos.getYellowAmmos(), ammos.getRedAmmos(), ammos.getBlueAmmos());
    }

    @Override
    public void playerDidDiscardAmmos(Player player, AmmoSet ammos) {
        removeAmmoSet(ammos.getYellowAmmos(), ammos.getRedAmmos(), ammos.getBlueAmmos());
    }

    @Override
    public void playerDidUnloadWeaponCard(Player player, WeaponCard weaponCard) {
        unloadWeaponCard(weaponCard.getCardID());
    }

    @Override
    public void playerDidReloadWeaponCard(Player player, WeaponCard weaponCard) {
        reloadWeaponCard(weaponCard.getCardID());
    }
}

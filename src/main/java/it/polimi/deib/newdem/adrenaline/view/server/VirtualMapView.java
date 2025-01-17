package it.polimi.deib.newdem.adrenaline.view.server;

import it.polimi.deib.newdem.adrenaline.model.game.GameData;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoSet;
import it.polimi.deib.newdem.adrenaline.model.items.DropInstance;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;
import it.polimi.deib.newdem.adrenaline.model.map.MapData;
import it.polimi.deib.newdem.adrenaline.model.map.MapListener;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;
import it.polimi.deib.newdem.adrenaline.model.map.TilePosition;
import it.polimi.deib.newdem.adrenaline.view.MapView;
import it.polimi.deib.newdem.adrenaline.view.inet.events.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A virtual view is a view object that acts as an adapter between model/controller and views,
 * translating the model objects into plain data objects usable by views.
 * This way the view is completely separated from the model and we do not need to clone/reflect
 * model objects into the client.
 *
 * Note: the VirtualGameView is also used to give information about the in-game users to the other
 * virtual views.
 */
public class VirtualMapView implements MapView, MapListener {

    private VirtualGameView gameView;

    public VirtualMapView(VirtualGameView gameView) {
        this.gameView = gameView;
    }


    @Override
    public void updateView(MapData data) {
        gameView.sendEvent(new MapDataEvent(data));
    }

    @Override
    public void playerDidResurrect(Player player) {
        spawnPlayer(player.getColor(), player.getTile().getPosition());
    }

    @Override
    public void playerDidMove(Player player, Tile source, Tile destination) {
        movePlayer(player.getColor(), destination.getPosition());
    }

    @Override
    public void playerDidSpawn(Player player, Tile spawnPoint) {
        spawnPlayer(player.getColor(), spawnPoint.getPosition());
    }

    @Override
    public void dropDidSpawn(Tile tile, DropInstance drop) {
        List<GameData.DropType> drops = new ArrayList<>();

        AmmoSet ammoSet = drop.getAmmos();

        for (int i = 0; i < ammoSet.getBlueAmmos(); i++) {
            drops.add(GameData.DropType.BLUE_AMMO);
        }

        for (int i = 0; i < ammoSet.getRedAmmos(); i++) {
            drops.add(GameData.DropType.RED_AMMO);
        }

        for (int i = 0; i < ammoSet.getYellowAmmos(); i++) {
            drops.add(GameData.DropType.YELLOW_AMMO);
        }

        if(drop.hasPowerUp()){
            drops.add(GameData.DropType.POWER_UP);
        }


        addDrops(tile.getPosition(), drops.get(0), drops.get(1), drops.get(2));
    }

    @Override
    public void dropDidDespawn(Tile tile) {
        removeDrops(tile.getPosition());
    }

    @Override
    public void playerDidDie(Player player) {
        killPlayer(player.getColor());
    }

    @Override
    public void playerDidLeaveMap(Player player) {
        removePlayer(player.getColor());
    }

    @Override
    public void mapDidRestoreData(MapData data) {
        updateView(data);
    }

    @Override
    public void weaponDidSpawn(Tile tile, WeaponCard weapon) {
        addWeapon(tile.getPosition(), weapon.getCardID());
    }

    @Override
    public void weaponDidDespawn(Tile tile, WeaponCard weapon) {
        removeWeapon(tile.getPosition(), weapon.getCardID());
    }


    /// MapView methods



    @Override
    public void addDrops(TilePosition tile, GameData.DropType drop1, GameData.DropType drop2, GameData.DropType drop3) {
        gameView.sendEvent(new SpawnDropEvent(drop1, drop2, drop3, tile));
    }

    @Override
    public void removeDrops(TilePosition tile) {
        gameView.sendEvent(new DespawnDropEvent(tile));
    }

    @Override
    public void movePlayer(PlayerColor player, TilePosition destTile) {
        gameView.sendEvent(new MovePlayerEvent(player, destTile));
    }

    @Override
    public void spawnPlayer(PlayerColor player, TilePosition tile) {
        gameView.sendEvent(new SpawnPlayerEvent(player, tile));
    }

    @Override
    public void killPlayer(PlayerColor player) {
        gameView.sendEvent(new DeathPlayerEvent(player));
    }

    @Override
    public void removePlayer(PlayerColor player) {
        gameView.sendEvent(new LeaveMapPlayerEvent(player));
    }

    @Override
    public void addWeapon(TilePosition tilePosition, int cardId) {
        gameView.sendEvent(new SpawnWeaponEvent(tilePosition, cardId));
    }

    @Override
    public void removeWeapon(TilePosition tilePosition, int cardId) {
        gameView.sendEvent(new DespawnWeaponEvent(tilePosition, cardId));
    }

}

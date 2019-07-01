package it.polimi.deib.newdem.adrenaline.model.game.turn;

import it.polimi.deib.newdem.adrenaline.controller.actions.ActionType;
import it.polimi.deib.newdem.adrenaline.controller.effects.*;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.PlayerSelector;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.TileSelector;
import it.polimi.deib.newdem.adrenaline.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpCard;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;
import it.polimi.deib.newdem.adrenaline.model.map.OrdinaryTile;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;
import it.polimi.deib.newdem.adrenaline.model.map.TilePosition;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.ArrayList;
import java.util.List;

public class ScriptedDataSource implements TurnDataSource {

    ActionType[] arr;
    List<Tile> tiles;
    List<Integer> pups;
    int i;
    private static final int UNDO_TILE_X = 90;
    private static final int UNDO_TILE_Y = 90;


    public ScriptedDataSource(ActionType ... types) {
        arr = types;
        i = 0;
        tiles = new ArrayList<>();
        pups = new ArrayList<>();
    }


    public static Tile getUndoTile() {
        return new OrdinaryTile(new TilePosition(UNDO_TILE_X, UNDO_TILE_Y));
    }

    // Tile (90, 90) means throw undo exception
    public void pushTile(Tile tile) {
        tiles.add(tile);
    }

    public void pushPupIndex(Integer i) {
        pups.add(i);
    }


    @Override
    public ActionType requestAction(List<ActionType> actionTypeList) throws UndoException {
        ActionType out = arr[i];
        i++;
        return out;
    }

    @Override
    public void pushActor(Player actor) {

    }

    @Override
    public void popActor(Player actor) {

    }

    @Override
    public Player requestPlayer(MetaPlayer metaPlayer, PlayerSelector selector, boolean forceChoice) throws UndoException {
        return null;
    }

    @Override
    public WeaponCard chooseWeaponCard(List<WeaponCard> cards) throws UndoException {
        return null;
    }

    @Override
    public PowerUpCard choosePowerUpCard(List<PowerUpCard> cards) throws UndoException {
        PowerUpCard pup = cards.get(pups.remove(pups.size() - 1));
        if(null == pup) throw new IllegalStateException();
        return pup;
    }

    @Override
    public Tile requestTile(TileSelector selector, boolean forceChoice) throws UndoException {
        Tile tile = tiles.remove(tiles.size() - 1);
        if(null == tile) throw new IllegalStateException();
        if(tile.getPosition().equals(new TilePosition(UNDO_TILE_X,UNDO_TILE_Y))) throw new UndoException();
        return tile;
    }

    @Override
    public Integer requestFragment(int cardID, List<Integer> fragments, boolean forceChoice) throws UndoException {
        return null;
    }

    @Override
    public PaymentReceipt requestPayment(PaymentInvoice invoice, Integer choice) throws UndoException {
        return null;
    }
}

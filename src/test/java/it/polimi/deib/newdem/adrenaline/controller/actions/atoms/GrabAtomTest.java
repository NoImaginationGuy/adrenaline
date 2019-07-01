package it.polimi.deib.newdem.adrenaline.controller.actions.atoms;

import it.polimi.deib.newdem.adrenaline.TestingUtils;
import it.polimi.deib.newdem.adrenaline.controller.actions.ActionType;
import it.polimi.deib.newdem.adrenaline.model.game.Game;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.turn.ScriptedDataSource;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoColor;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoSet;
import it.polimi.deib.newdem.adrenaline.model.map.TestingMapBuilder;
import org.junit.Before;
import org.junit.Test;

public class GrabAtomTest {

    private Game game;
    private ScriptedDataSource source;

    @Before
    public void setUp() throws Exception {
        game = TestingUtils.makeTestGame(PlayerColor.MAGENTA);
        source = new ScriptedDataSource(
                new ActionType(AtomicActionType.MOVE3, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB),
                new ActionType(AtomicActionType.MOVE2, AtomicActionType.GRAB)
                );
    }

    @Test
    public void testGrab() throws Exception {
        Turn turn = game.getNextTurn();
        turn.bindDataSource(source);

        for(int i = 0; i < 10; i++) {
            source.pushWeaponCardIndex(0);
        }
        // read from bottom to top

        // just grab it
        source.pushTile(game.getMap().getTile(TestingMapBuilder.getDropTilePos())); // move to next tile, with drop
        // ^ ACTION 2
        // do not discard card
        // pay for it
        // buy random weapon from spawnpoint
        refillAmmos(turn.getActivePlayer());
        // ^ fill inventory with max ammos
        source.pushTile(game.getMap().getSpawnPointFromColor(AmmoColor.RED)); // move to red spawnpoint tile
        // ACTION 1
        source.pushPupIndex(0); // spawn // spawn at a random spawnpoint
        // end bottom to top

        turn.execute();
        game.concludeTurn(turn);
        // now player has 1 weapon
        //
        //
        //

        turn = game.getNextTurn();
        turn.bindDataSource(source);

        pickUpWeaponHere();
        refillAmmos(turn.getActivePlayer());
        moveStill(turn.getActivePlayer());
        // ^ add second weapon

        turn.execute();
        game.concludeTurn(turn);
        // now player has 2 weapons

        turn = game.getNextTurn();
        turn.bindDataSource(source);

        pickUpWeaponHere();
        refillAmmos(turn.getActivePlayer());
        moveStill(turn.getActivePlayer());
        // ^ fill inventory with third weapon

        turn.execute();
        game.concludeTurn(turn);

        turn = game.getNextTurn();
        turn.bindDataSource(source);

        // more difficult than pickUpWeaponHere();
        // or not
        // choiche is random anyway
        pickUpWeaponHere();
        refillAmmos(turn.getActivePlayer());
        moveStill(turn.getActivePlayer());

        turn.execute();
        game.concludeTurn(turn);
    }

    private void pickUpWeaponHere() {
        // do not discard card
        // pay for it
        // buy random weapon from spawnpoint
        source.pushTile(game.getMap().getSpawnPointFromColor(AmmoColor.RED)); // move to red spawnpoint tile
        source.pushPupIndex(0); // spawn
        // ACTION 1
    }

    private void moveStill(Player player) {
        source.pushTile(player.getTile());
    }

    private void refillAmmos(Player player) {
        player.getInventory().addAmmoSet(new AmmoSet(3,3,3));
    }
}
package it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions;

import it.polimi.deib.newdem.adrenaline.TestingUtils;
import it.polimi.deib.newdem.adrenaline.controller.actions.ActionType;
import it.polimi.deib.newdem.adrenaline.model.game.rigged.GameRigged;
import it.polimi.deib.newdem.adrenaline.model.game.turn.ScriptedDataSource;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoColor;
import it.polimi.deib.newdem.adrenaline.model.map.TestingMapBuilder;
import org.junit.Before;
import org.junit.Test;
import sun.security.krb5.SCDynamicStoreConfig;

import static it.polimi.deib.newdem.adrenaline.controller.actions.atoms.AtomicActionType.GRAB;
import static it.polimi.deib.newdem.adrenaline.controller.actions.atoms.AtomicActionType.MOVE2;
import static it.polimi.deib.newdem.adrenaline.controller.actions.atoms.AtomicActionType.MOVE3;
import static it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor.MAGENTA;
import static it.polimi.deib.newdem.adrenaline.model.items.AmmoColor.RED;
import static org.junit.Assert.*;

public class GrabInteractionTest {

    GameRigged game;

    @Before
    public void SetUp() throws Exception {
        game = TestingUtils.makeRiggedGame(MAGENTA);
    }

    @Test
    public void testExecute() throws Exception {
        ScriptedDataSource sds = new ScriptedDataSource(
                new ActionType(MOVE3, GRAB),
                new ActionType(MOVE3, GRAB)
        );

        sds.pushWeaponCardIndex(0);
        sds.pushTile(game.getMap().getSpawnPointFromColor(RED));
        // end action 1
        // grab drop
        sds.pushTile(game.getMap().getTile(TestingMapBuilder.getDropTilePos()));
        sds.pushPupIndex(0);

        Turn t = game.getNextTurn();
        t.setRunClosingActions(false);
        t.bindDataSource(sds);
        t.execute();
    }

    @Test
    public void testUndos() throws Exception {
        ScriptedDataSource sds = new ScriptedDataSource(
                new ActionType(MOVE2, GRAB),
                new ActionType(MOVE3)
        );

        sds.pushWeaponCardIndex(0);
        sds.pushTile(game.getMap().getSpawnPointFromColor(RED));
        // end action 1
        // pick up weapon
        sds.pushWeaponCardIndex(0);
        sds.pushTile(game.getMap().getSpawnPointFromColor(AmmoColor.BLUE));
        // undo, back to tile
        sds.pushWeaponCardIndex(ScriptedDataSource.getUndoWeaponCardIndex());
        // attempt to grab weapon
        sds.pushTile(game.getMap().getSpawnPointFromColor(RED));
        sds.pushPupIndex(0);

        Turn t = game.getNextTurn();
        t.setRunClosingActions(false);
        t.bindDataSource(sds);
        t.execute();
    }

    @Test
    public void testUndoPayment() throws Exception {
        ScriptedDataSource sds = new ScriptedDataSource(
                new ActionType(MOVE3, GRAB),
                new ActionType(MOVE3)
        );;

        sds.pushTile(game.getMap().getSpawnPointFromColor(RED));
        // grab drop
        sds.pushTile(game.getMap().getTile(TestingMapBuilder.getDropTilePos()));
        // undo grab, roll back to move
        sds.pushWeaponCardIndex(ScriptedDataSource.getUndoWeaponCardIndex());
        // fail to pay
        sds.addScheduledUndoPayment();
        sds.pushWeaponCardIndex(0);
        // attempt to grab weapon
        sds.pushTile(game.getMap().getSpawnPointFromColor(RED));
        sds.pushPupIndex(0);

        Turn t = game.getNextTurn();
        t.setRunClosingActions(false);
        t.bindDataSource(sds);
        t.execute();
    }
}
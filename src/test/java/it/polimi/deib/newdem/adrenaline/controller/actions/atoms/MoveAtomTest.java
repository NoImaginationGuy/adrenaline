package it.polimi.deib.newdem.adrenaline.controller.actions.atoms;

import it.polimi.deib.newdem.adrenaline.TestingUtils;
import it.polimi.deib.newdem.adrenaline.controller.Config;
import it.polimi.deib.newdem.adrenaline.controller.actions.ActionType;
import it.polimi.deib.newdem.adrenaline.model.game.ColorUserPair;
import it.polimi.deib.newdem.adrenaline.model.game.Game;
import it.polimi.deib.newdem.adrenaline.model.game.GameImpl;
import it.polimi.deib.newdem.adrenaline.model.game.GameParameters;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.turn.ScriptedDataSource;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.game.turn.TurnDataSource;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoColor;
import it.polimi.deib.newdem.adrenaline.model.map.OrdinaryTile;
import it.polimi.deib.newdem.adrenaline.model.map.TilePosition;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Integration tests for {@code MoveAtom}
 *
 * These tests are run as a simulation of a complete game with
 * controlled user inputs and cards drawn
 *
 * @see it.polimi.deib.newdem.adrenaline.controller.actions.atoms.MoveAtom
 */
public class MoveAtomTest {

    private Game game;
    private ScriptedDataSource source;

    /**
     * Creates a new test game
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        game = TestingUtils.makeTestGame(PlayerColor.MAGENTA);
        source = new ScriptedDataSource( game,
                new ActionType(AtomicActionType.MOVE1),
                new ActionType(AtomicActionType.MOVE4),
                new ActionType(AtomicActionType.MOVE4));
        source.pushPupIndex(0);
        source.pushPupIndex(ScriptedDataSource.getUndoPupIndex());
        source.pushTile(game.getMap().getSpawnPointFromColor(AmmoColor.RED));
        source.pushTile(ScriptedDataSource.getUndoTile());
        source.pushTile(game.getMap().getTile(new TilePosition(1,0)));
    }

    /**
     * Simulates a game with a {@code MoveAtom}
     */
    @Test
    public void testExecute() {
        Turn turn = game.getNextTurn();
        turn.setRunClosingActions(false);
        turn.bindDataSource(source);
        turn.execute();

    }
}
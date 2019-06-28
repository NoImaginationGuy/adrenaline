package it.polimi.deib.newdem.adrenaline.controller.actions.atoms;

import it.polimi.deib.newdem.adrenaline.controller.actions.Action;
import it.polimi.deib.newdem.adrenaline.controller.effects.UndoException;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.NearTileSelector;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.TileSelector;
import it.polimi.deib.newdem.adrenaline.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.model.game.changes.MovementGameChange;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

public class MoveAtom extends AtomBase {

    private int minDist;
    private int maxDist;

    private GameChange gc;

    public MoveAtom(AtomsContainer parent, int minDist, int maxDist) {
        super(parent);
        this.minDist = minDist;
        this.maxDist = maxDist;
    }

    @Override
    public void execute()  {
        TileSelector selector = new NearTileSelector(parent.getActor(), minDist, maxDist);
        Tile target = null;
        do {
            try {
                target = parent.getDataSource().actionDidRequestTile(selector);
            } catch (UndoException e) {
                // do not exit, do not propagate.
                // force user selection
            }
        }while (null == target);
        gc = new MovementGameChange(parent.getActor(), target);
        gc.update(parent.getGame());
    }
}

package it.polimi.deib.newdem.adrenaline.controller.effects;

import it.polimi.deib.newdem.adrenaline.controller.effects.selection.TileSelectorFactory;
import it.polimi.deib.newdem.adrenaline.model.game.changes.DamageGameChange;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

public class RoomDamageEffect extends ConcreteEffect {

    private int damageAmount;
    private int markAmount;

    private TileSelectorFactory selectorMaker;

    public RoomDamageEffect(int id, int dmgAmt, int mrkAmt, TileSelectorFactory selectorMaker) {
        super(id);
        this.selectorMaker = selectorMaker;

        this.damageAmount = dmgAmt;
        this.markAmount = mrkAmt;
    }

    @Override
    public void apply(EffectVisitor visitor) throws UndoException {
        Player attacker = visitor.getBoundPlayer(MetaPlayer.ATTACKER);

        Tile roomTile = visitor.getTile(selectorMaker.makeSelector(visitor));

        for (Tile tile : roomTile.getRoom().getTiles()) {
            for (Player player : tile.getPlayers()) {
                visitor.reportGameChange(new DamageGameChange(attacker, player, damageAmount, markAmount));
            }
        }

    }
}

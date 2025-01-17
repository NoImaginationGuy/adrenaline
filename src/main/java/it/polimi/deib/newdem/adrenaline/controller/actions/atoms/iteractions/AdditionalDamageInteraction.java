package it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions;

import it.polimi.deib.newdem.adrenaline.controller.effects.UndoException;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpTrigger;

import java.util.List;

/**
 * Interaction encapsulating the initial check for legal targets in/ a REVENGE atom
 */
public class AdditionalDamageInteraction extends InteractionBase {

    /**
     * Builds a new {@code AdditionalDamageInteraction } bound to the given {@code InteractionContext}
     * @param context this interaction's environment
     */
    public AdditionalDamageInteraction(InteractionContext context) {
        super(context);
    }

    @Override
    public void execute() {
        List<Player> damagedPlayers = context.getDamagedPlayers();

        if(damagedPlayers.isEmpty()) { return; }
        if(context.getActor().getInventory().getPowerUpByTrigger(PowerUpTrigger.DAMAGE_DEALT).isEmpty()) { return; }

        context.pushInteraction(new SelectAdditionalDamagePowerupInteraction(context));
    }

    @Override
    public void revert() {

    }

    @Override
    public boolean requiresInput() {
        return false;
    }
}

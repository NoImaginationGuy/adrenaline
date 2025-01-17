package it.polimi.deib.newdem.adrenaline.controller.effects;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;

/**
 * Card effect that implements Adrenaline' Targeting Scope power up card.
 * @see Effect for further information about the card effects.
 */
public class TargetingScopeEffect implements Effect {

    private static final int ADDITIONAL_DAMAGE = 1;

    private static final PaymentInvoice ADDITIONAL_DAMAGE_PAYMENT = new PaymentInvoice(0, 0, 0, 1);


    @Override
    public void apply(EffectManager manager, Player actor) throws UndoException {
        // actor is attacker, victim comes form manager.
        if (manager.pay(ADDITIONAL_DAMAGE, ADDITIONAL_DAMAGE_PAYMENT)) {
            manager.damagePlayer(actor, manager.getVictim(), 1, 0, false);
        } else {
            throw new UndoException();
        }

    }

}

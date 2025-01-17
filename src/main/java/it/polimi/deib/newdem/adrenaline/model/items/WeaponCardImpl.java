package it.polimi.deib.newdem.adrenaline.model.items;

import it.polimi.deib.newdem.adrenaline.controller.effects.Effect;
import it.polimi.deib.newdem.adrenaline.controller.effects.PaymentInvoice;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;

public class WeaponCardImpl implements WeaponCard {

    PaymentInvoice pickupPrice;
    PaymentInvoice reloadPrice;
    Effect effect;
    int id;

    public WeaponCardImpl(int id, PaymentInvoice pickupPrice, PaymentInvoice reloadPrice, Effect effect){
        this.effect = effect;
        this.pickupPrice = pickupPrice;
        this.reloadPrice = reloadPrice;
        this.id = id;
    }


    @Override
    public PaymentInvoice getPickupPrice() {
        return pickupPrice;
    }

    @Override
    public PaymentInvoice getReloadPrice() {
        return reloadPrice;
    }

    @Override
    public Effect getEffect() {
        return effect;
    }

    @Override
    public Weapon makeWeapon(Player player) {
        return new WeaponImpl(this, player);
    }

    @Override
    public int getCardID() {
        return id;
    }
}

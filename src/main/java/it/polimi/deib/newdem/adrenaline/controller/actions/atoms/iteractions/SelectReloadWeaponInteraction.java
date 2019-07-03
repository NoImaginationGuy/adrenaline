package it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions;

import it.polimi.deib.newdem.adrenaline.controller.effects.UndoException;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;

import java.util.List;

public class SelectReloadWeaponInteraction extends InteractionBase {

    public SelectReloadWeaponInteraction(InteractionContext context) {
        super(context);
    }

    @Override
    public void execute() throws UndoException {
        Player player = context.getActor();

        List<WeaponCard> selectables = player.getInventory().getUnloadedWeapons().getWeapons();

        if(player.canReload()) {
            WeaponCard selectedWeaponCard = context.getDataSource().chooseWeaponCard(selectables);

            if(null == selectedWeaponCard) {
                // due to InteractionStack's design, terminating gracefully
                // without pushing anything will cause the context's visit to terminate.
                return;
            }

            context.pushInteraction(new ReloadPaymentInteraction(context, selectedWeaponCard));
        }
    }

    @Override
    public void revert() {
        // nothing to revert
    }

    @Override
    public boolean requiresInput() {
        return true;
    }
}

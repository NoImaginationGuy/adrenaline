package it.polimi.deib.newdem.adrenaline.controller.actions.atoms;

import it.polimi.deib.newdem.adrenaline.controller.effects.EffectManager;
import it.polimi.deib.newdem.adrenaline.controller.effects.UndoException;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpCard;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpTrigger;

import java.util.List;
import java.util.stream.Collectors;

public class PowerUpAtom extends AtomContext {

    public PowerUpAtom(AtomsContainer parent) {
        super(parent);
    }

    @Override
    public void execute() throws UndoException {
        // choose pup card
        List<PowerUpCard> availablePups = getActor().getInventory().getPowerUps()
                .stream()
                .filter(card -> card.getTrigger() == PowerUpTrigger.CALL)
                .collect(Collectors.toList());

        PowerUpCard selectedPup = parent.getDataSource().choosePowerUpCard(availablePups);
        if (selectedPup != null) {
            try {
                EffectManager manager = new EffectManager(this);
                manager.execute(selectedPup.getEffect());
            } catch (UndoException x) {
                // nothing to do here
            }
        }
    }
}
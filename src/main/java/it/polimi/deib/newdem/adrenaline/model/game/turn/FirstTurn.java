package it.polimi.deib.newdem.adrenaline.model.game.turn;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;

public class FirstTurn extends TurnBaseImpl {

    /**
     * This class is currently a stub
     *
     * Logic for spawning in needs to be added
     *
     * What and how to override is not yet defined
     *
     */
    @Override
    protected void performInitialActions() {
        //TODO implement
        // requires action anc listeners
        // requires spawn in routine
    }

    @Override
    protected void endOfTurn() {
        //TODO implement
        // requires action anc listeners

    }

    public FirstTurn(Player activePlayer) {
        super(activePlayer);
        //TODO implement
        // requires action anc listeners
    }

    public void start() {
        //TODO implement
        // requires action anc listeners
    }

    public void turnWillStart() {
        //TODO implement
        // requires action anc listeners
    }


}
package it.polimi.deib.newdem.adrenaline.model.game;

import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;

public final class NullGameListener implements GameListener {
    @Override
    public void gameWillStart(Game game) {

    }

    @Override
    public void gameWillEnd(Game game) {

    }

    @Override
    public void userDidEnterGame(User user, Player player) {

    }

    @Override
    public void userDidExitGame(User user, Player player) {

    }
}
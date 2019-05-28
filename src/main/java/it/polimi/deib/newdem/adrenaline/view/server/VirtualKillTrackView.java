package it.polimi.deib.newdem.adrenaline.view.server;

import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackListener;
import it.polimi.deib.newdem.adrenaline.model.game.Player;
import it.polimi.deib.newdem.adrenaline.model.game.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.KillTrackView;
import it.polimi.deib.newdem.adrenaline.view.inet.events.KillTrackAddKillEvent;

public class VirtualKillTrackView implements KillTrackView, KillTrackListener {

    private VirtualGameView vgv;

    public VirtualKillTrackView(VirtualGameView vgv) {
        this.vgv = vgv;
    }

    @Override
    public void playerDidKill(Player player, int amount) {
        registerKill(player.getColor(), amount);
    }

    @Override
    public void registerKill(PlayerColor pColor, int amount) {
        vgv.sendEvent(new KillTrackAddKillEvent(pColor, amount));
    }

    @Override
    public void goFrenzy() {
        // TODO
        // deprecate
    }

}

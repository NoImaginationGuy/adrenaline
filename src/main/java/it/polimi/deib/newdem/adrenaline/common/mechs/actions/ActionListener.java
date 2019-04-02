package it.polimi.deib.newdem.adrenaline.common.mechs.actions;

import it.polimi.deib.newdem.adrenaline.common.mechs.game.GameChange;
import it.polimi.deib.newdem.adrenaline.common.mechs.game.Player;
import it.polimi.deib.newdem.adrenaline.common.mechs.effects.Effect;
import it.polimi.deib.newdem.adrenaline.common.mechs.effects.MetaPlayer;
import it.polimi.deib.newdem.adrenaline.common.mechs.effects.PlayerSelector;
import it.polimi.deib.newdem.adrenaline.common.mechs.effects.TileSelector;
import it.polimi.deib.newdem.adrenaline.common.mechs.map.Tile;

import java.util.List;

public interface ActionListener {

    Player actionDidRequestPlayerBinding(MetaPlayer metaPlayer, PlayerSelector selector);
    /*
    At the time of writing, the Effect package, which also contains MetaPlayer, hasn't been introduced
    not even as a stub

    TODO validate
     */

    Tile actionDidRequestTile(TileSelector selector);
    /*
    At the time of writing, Effect hasn't been introduced
    not even as a stub

    TODO validate
     */

    default int actionDidRequestChoiche(List<Effect> choices) {
        return 0;
    }
    /*
    At the time of writing, AmmoSet hasn't been introduced
    not even as a stub

    TODO validate
     */

    int actionDidRequestAdditionalDamage();

    int actionDidRequestRevengeMark(Player attackedPlayer);

    void actionDidEmitGameChange(GameChange gameChange);
}

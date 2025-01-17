package it.polimi.deib.newdem.adrenaline.view.inet.events;

import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerAcquirePowerUpEventTest {

    PlayerAcquirePowerUpEvent e;

    @Before
    public void setUp() throws Exception {
        e = new PlayerAcquirePowerUpEvent(PlayerColor.YELLOW, 7);
    }

    @Test
    public void testGetPlayerColor() throws Exception {
        assertEquals(PlayerColor.YELLOW, e.getPlayerColor());
    }

    @Test
    public void testGetPowerUpCardID() throws Exception {
        assertEquals(e.getPowerUpCardID(), 7);
    }

    @Test
    public void testNotifyEvent() throws Exception {
        e.publish(new MockConnectionSender());
    }
}
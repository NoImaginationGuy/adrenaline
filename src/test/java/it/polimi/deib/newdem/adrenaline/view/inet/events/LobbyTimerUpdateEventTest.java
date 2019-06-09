package it.polimi.deib.newdem.adrenaline.view.inet.events;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LobbyTimerUpdateEventTest {

    LobbyTimerUpdateEvent e;

    @Before
    public void setUp() throws Exception {
        e = new LobbyTimerUpdateEvent(7);
    }

    @Test
    public void testGetSecondsLeft() throws Exception {
        assertEquals(7, e.getSecondsLeft());
    }

    @Test
    public void testNotifyEvent() throws Exception {
        e.publish(new MockConnectionSender());
    }
}
package it.polimi.deib.newdem.adrenaline.model.game.changes;

import it.polimi.deib.newdem.adrenaline.model.game.*;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.map.MapListener;

import static it.polimi.deib.newdem.adrenaline.model.game.DamageBoardImpl.DEATH_SHOT_INDEX;
import static it.polimi.deib.newdem.adrenaline.model.game.DamageBoardImpl.MAX_LIFE;
import static it.polimi.deib.newdem.adrenaline.model.game.DamageBoardImpl.MAX_MARKS;
import static java.lang.Math.min;
import static java.lang.Math.multiplyExact;

/**
 * GameChanges that deals damage from a player to another player in the specified amount.
 */
public class DamageGameChange implements GameChange {

    private Player attacker;
    private Player victim;
    private int desiredDmg;
    private int desiredMrk;
    private int actualDmg;
    private int previousMrk;
    private boolean didDie;
    private boolean canRealizeMarks;

    /**
     * Builds a new {@code DamageGameChange} bound to the given parameters.
     *
     * The built {@code GameChange} will attempt to realize marks by default.
     *
     * @param attacker player dealing damage
     * @param attacked player taking damage
     * @param dmgAmt amount of damage dealt
     * @param mrkAmt amount of mark dealt
     */
    public DamageGameChange(Player attacker, Player attacked, int dmgAmt, int mrkAmt){
        this.victim = attacked;
        this.attacker = attacker;
        this.desiredDmg = dmgAmt;
        this.desiredMrk = mrkAmt;
        didDie = false;
        canRealizeMarks = true;
    }

    /**
     * Builds a new {@code DamageGameChange} bound to the given parameters.
     *
     * @param attacker player dealing damage
     * @param attacked player taking damage
     * @param dmgAmt amount of damage dealt
     * @param mrkAmt amount of mark dealt
     * @param canRealizeMarks whether the damages dealt within this {@code GameChange} should attempt to realize marks.
     */
    public DamageGameChange(Player attacker, Player attacked, int dmgAmt, int mrkAmt, boolean canRealizeMarks) {
        this(attacker, attacked, dmgAmt, mrkAmt);
        this.canRealizeMarks = canRealizeMarks;
    }

    @Override
    public void update(Game game) {
        DamageBoard victimBoard = victim.getDamageBoard();

        try {
            for (int i = desiredDmg; i > 0; i--) {
                victimBoard.appendDamage(attacker, canRealizeMarks);
                // ^ implies resolution of previous marks if applicable
                // note that for desiredDmg = 0; it's never called
                // and marks are not reset
            }
        }
        catch (DamageTrackFullException e) {
            // do nothing
        }

        victimBoard.setMarksFromPlayer(
                min(victimBoard.getTotalMarksFromPlayer(attacker) + desiredMrk, MAX_MARKS),
                attacker
        );

        if(victimBoard.getTotalDamage() > DEATH_SHOT_INDEX) {
            victim.reportDeath(true);
            didDie = true;
        }
    }

    @Override
    public void revert(Game game) {
        //TODO
        DamageBoard dmgb = victim.getDamageBoard();

        // rez
        if(didDie) {
            victim.reportDeath(false);
            MapListener mapListener = game.getMap().getListener();
            if(null != mapListener) {
                mapListener.playerDidResurrect(victim);
            }
        }

        // revert marks
        dmgb.setMarksFromPlayer(previousMrk, attacker);

        // revert damage
        int dmgCursor = actualDmg;
        try {
            while (dmgCursor > 0) {
                int dmgIndex = dmgb.getTotalDamage() - 1;

                // check for legal state gracefully
                if (dmgIndex < 0 || dmgIndex > MAX_LIFE) throw new IndexOutOfBoundsException();
                if (!attacker.equals(dmgb.getDamager(dmgIndex))) throw new IllegalStateException();

                Player removed = dmgb.popDamage();
                if(!removed.equals(attacker)) throw new IllegalStateException();
                dmgCursor--;
            }
        }
        catch (DamageTrackEmptyException e) {
            // do what now?
            // this should never happen
            throw new IllegalStateException(e);
        }
    }
}

package it.polimi.deib.newdem.adrenaline.controller.actions.atoms;

import it.polimi.deib.newdem.adrenaline.controller.*;
import it.polimi.deib.newdem.adrenaline.controller.actions.Action;
import it.polimi.deib.newdem.adrenaline.controller.actions.ActionFactory;
import it.polimi.deib.newdem.adrenaline.controller.actions.ConcreteActionFactory;
import it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions.EntryPointFactory;
import it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions.EntryPointType;
import it.polimi.deib.newdem.adrenaline.controller.actions.atoms.iteractions.InteractionStackImpl;
import it.polimi.deib.newdem.adrenaline.controller.effects.*;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.PlayerSelector;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.TileSelector;
import it.polimi.deib.newdem.adrenaline.model.game.GameChange;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * An atom which allows the {@code Interaction}s contained to use an {@code EffectContext}
 */
public abstract class AtomContext extends AtomBase implements AtomEffectContext {

    protected WeaponCard usedWeapon;
    private List<Player> damagedPlayers;
    private boolean enableDamageTriggers;

    /**
     * Creates a new {@code AtomBase} bound to the given {@code AtomsContainer} and
     * with the set {@code EntryPointFactory}
     *
     * @param parent this atom's container
     * @param entryPointType this atom's entrypoint
     */
    public AtomContext(AtomsContainer parent, EntryPointType entryPointType) {
        super(parent, new EntryPointFactory(entryPointType));
        damagedPlayers = new ArrayList<>();
        enableDamageTriggers = true;
    }

    @Override
    public void setSelectedWeaponCard(WeaponCard card) {
        usedWeapon = card;
    }

    private void registerContext(InteractionStackImpl stack, AtomEffectContext context) {
        stack.registerContext(context);
    }

    @Override
    public void executeFromStart() throws UndoException {
        registerContext(interactionsStack, this);
        super.executeFromStart();
    }

    @Override
    public void executeFromLatest() throws UndoException {
        registerContext(interactionsStack, this);
        super.executeFromLatest();
    }

    @Override
    public void applyGameChange(GameChange gameChange) {
        gameChange.update(parent.getGame());
    }

    @Override
    public void revertGameChange(GameChange gameChange) {
        gameChange.revert(parent.getGame());
    }

    @Override
    public Player getActor() {
        return parent.getActor();
    }

    @Override
    public Player getAttacker() {
        return null;
    }

    @Override
    public Player getVictim() {
        return null;
    }

    @Override
    public Player choosePlayer(MetaPlayer player, PlayerSelector selector, boolean forceChoice) throws UndoException {
        return parent.getDataSource().requestPlayer(player, selector, forceChoice);
    }

    @Override
    public Tile chooseTile(TileSelector selector, boolean forceChoice) throws UndoException {
        return parent.getDataSource().requestTile(selector, forceChoice);
    }

    @Override
    public Integer chooseFragment(List<Integer> choices) throws UndoException {
        int cardID = -1;
        if (usedWeapon != null) cardID = usedWeapon.getCardID();

        return parent.getDataSource().requestFragment(cardID, choices, false);
    }

    @Override
    public PaymentReceipt choosePayment(PaymentInvoice invoice, int choice) throws UndoException {
        return parent.getDataSource().requestPayment(invoice, choice);
    }

    @Override
    public void setEnableDamageTriggers(boolean flag) {
        enableDamageTriggers = flag;
    }

    @Override
    public void damageDealtTrigger(Player attacker, Player victim) {
        if(!enableDamageTriggers) { return; }
        damagedPlayers.add(victim);
    }

    @Override
    public List<Player> getDamagedPlayers() {
        return new ArrayList<>(damagedPlayers);
    }

    @Override
    public void damageTakenTrigger(Player attacker, Player victim) {
        if(!enableDamageTriggers) { return; }
        TimedExecutor.pauseTimer();

        ConcreteActionFactory revengeFactory = new ConcreteActionFactory(AtomicActionType.REVENGE);
        revengeFactory.setAttacker(parent.getDataSource().peekActor());
        Action revengeAction = revengeFactory.makeAction(victim, parent.getDataSource());
        parent.getDataSource().pushActor(victim);

        TimedExecutor revengeExecutor = new TimedExecutor(() -> actionUndoSuppressor(revengeAction));

        try {
            revengeExecutor.execute(15);
        } catch (TimeoutException | AbortedException e) {
            // nothing to do maybe.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptExecutionException();
        } finally {
            parent.getDataSource().popActor(victim);
            TimedExecutor.resumeTimer();
        }
    }

    @Override
    public AtomEffectContext getEffectContext() {
        return this;
    }

    private void actionUndoSuppressor(Action action) {
        try {
            action.start();
        }
        catch (UndoException e) {
            // do nothing
        }
    }

    @Override
    public void setVictim(Player victim) {

    }
}

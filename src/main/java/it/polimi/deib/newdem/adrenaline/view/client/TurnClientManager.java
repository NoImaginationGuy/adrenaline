package it.polimi.deib.newdem.adrenaline.view.client;

import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.TurnView;
import it.polimi.deib.newdem.adrenaline.view.inet.UserConnection;
import it.polimi.deib.newdem.adrenaline.view.inet.UserEventLocker;
import it.polimi.deib.newdem.adrenaline.view.inet.UserEventSubscriber;
import it.polimi.deib.newdem.adrenaline.view.inet.events.*;

/**
 * Object that links the UI view to the connection events, receiving requests from server
 * and sending responses from the UI.
 */
public class TurnClientManager {

    private UserConnection connection;

    private TurnView turnView;

    private PlayerColor actor;

    private UserEventSubscriber<RevengeStartEvent> pushSubscriber = this::pushRevenge;

    private UserEventSubscriber<RevengeEndEvent> popSubscriber = this::popRevenge;

    private UserEventSubscriber<TurnActionRequest> actionSubscriber = this::askAction;

    private UserEventSubscriber<TurnWeaponRequest> weaponSubscriber = this::askWeapon;

    private UserEventSubscriber<TurnPowerUpRequest> powerUpSubscriber = this::askPowerUp;

    private UserEventSubscriber<TurnPlayerRequest> playerSubscriber = this::askPlayer;

    private UserEventSubscriber<TurnTileRequest> tileSubscriber = this::askTile;

    private UserEventSubscriber<TurnFragmentRequest> fragmentSubscriber = this::askFragment;

    private UserEventSubscriber<TurnPaymentRequest> paymentSubscriber = this::askPayment;

    /**
     * Initializes the turn client manager with the connection, the UI turn view, and the color
     * of the player that is taking control of the game.
     */
    public TurnClientManager(UserConnection connection, TurnView turnView, PlayerColor actor) {
        this.connection = connection;
        this.turnView = turnView;
        this.actor = actor;
    }

    /**
     * Links all the events to the methods of the turn view passed to the constructor and then
     * blocks the caller thread until a {@code TurnEndEvent} is received.
     */
    public void start() {
        turnView.startTurn(actor);

        connection.subscribeEvent(RevengeStartEvent.class, pushSubscriber);
        connection.subscribeEvent(RevengeEndEvent.class, popSubscriber);
        connection.subscribeEvent(TurnActionRequest.class, actionSubscriber);
        connection.subscribeEvent(TurnWeaponRequest.class, weaponSubscriber);
        connection.subscribeEvent(TurnPowerUpRequest.class, powerUpSubscriber);
        connection.subscribeEvent(TurnPlayerRequest.class, playerSubscriber);
        connection.subscribeEvent(TurnTileRequest.class, tileSubscriber);
        connection.subscribeEvent(TurnFragmentRequest.class, fragmentSubscriber);
        connection.subscribeEvent(TurnPaymentRequest.class, paymentSubscriber);

        UserEventLocker<TurnEndEvent> turnEndLocker = new UserEventLocker<>();
        TurnEndEvent turnEndEvent;
        try {
            do {
                turnEndEvent = turnEndLocker.waitOnEvent(TurnEndEvent.class, connection);
            } while (turnEndEvent.getTurnActor() != actor);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
            throw new ClosedException("Close requested.");
        } finally {
            connection.unsubscribeEvent(RevengeStartEvent.class, pushSubscriber);
            connection.unsubscribeEvent(RevengeEndEvent.class, popSubscriber);
            connection.unsubscribeEvent(TurnActionRequest.class, actionSubscriber);
            connection.unsubscribeEvent(TurnWeaponRequest.class, weaponSubscriber);
            connection.unsubscribeEvent(TurnPowerUpRequest.class, powerUpSubscriber);
            connection.unsubscribeEvent(TurnPlayerRequest.class, playerSubscriber);
            connection.unsubscribeEvent(TurnTileRequest.class, tileSubscriber);
            connection.unsubscribeEvent(TurnFragmentRequest.class, fragmentSubscriber);
            connection.unsubscribeEvent(TurnPaymentRequest.class, paymentSubscriber);
        }

        turnView.endTurn(actor);
    }


    private void pushRevenge(UserConnection connection, RevengeStartEvent event) {
        turnView.startTurn(event.getActor());
    }

    private void popRevenge(UserConnection connection, RevengeEndEvent event) {
        turnView.endTurn(event.getActor());
    }

    private void askAction(UserConnection connection, TurnActionRequest request) {
        try {
            connection.sendEvent(new TurnActionResponse(turnView.chooseAction(request.getAvailableActions())));
        } catch (Exception x) {
            // no problem
        }
    }

    private void askWeapon(UserConnection connection, TurnWeaponRequest request) {
        try {
            connection.sendEvent(new TurnWeaponResponse(turnView.chooseWeaponCard(request.getAvailableCardIDs())));
        } catch (Exception x) {
            // no problem
        }
    }

    private void askPowerUp(UserConnection connection, TurnPowerUpRequest request) {
        try {
            connection.sendEvent(new TurnPowerUpResponse(turnView.choosePowerUpCard(request.getAvailableCards())));
        } catch (Exception x) {
            // no problem
        }
    }

    private void askPlayer(UserConnection connection, TurnPlayerRequest request) {
        try {
            connection.sendEvent(new TurnPlayerResponse(
                    turnView.choosePlayer(request.getMetaPlayer(), request.getSelectablePlayers(), request.isChoiceForced())
            ));
        } catch (Exception x) {
            // no problem
        }
    }

    private void askTile(UserConnection connection, TurnTileRequest request) {
        try {
            connection.sendEvent(
                    new TurnTileResponse(turnView.chooseTile(request.getSelectableTiles(), request.isChoiceForced()))
            );
        } catch (Exception x) {
            // no problem
        }
    }

    private void askFragment(UserConnection connection, TurnFragmentRequest request) {
        try {
            connection.sendEvent(new TurnFragmentResponse(
                    turnView.chooseCardFragment(request.getCardID(), request.getSelectableFragments(), request.isChoiceForced())
            ));
        } catch (Exception x) {
            // no problem
        }
    }

    private void askPayment(UserConnection connection, TurnPaymentRequest request) {
        try {
            connection.sendEvent(new TurnPaymentResponse(
                    turnView.choosePayment(
                            request.getInvoice(),
                            request.getPlayerAmmos(),
                            request.getAvailablePowerUps(),
                            request.getFragmentToPay()
                    )
            ));
        } catch (Exception x) {
            // no problem
        }
    }

}

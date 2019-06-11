package it.polimi.deib.newdem.adrenaline.view.inet;

import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.inet.events.UserEvent;

import java.util.List;
import java.util.Map;

public interface UserConnection extends AutoCloseable {


    /**
     * Starts a new thread that listens to new events.
     * Any new event that will arrive to the object will be notified on all the receivers within that thread.
     */
    void start();

    /**
     * Returns the user associated to the connection.
     */
    User getUser();

    /**
     * Subscribes the given subscriber object to this connection so that it will receiver events of the given class.
     */
    <T extends UserEvent> void subscribeEvent(Class<T> eventClass, UserEventSubscriber<T> subscriber);

    /**
     * Removes the subscription of the given object to this connection so that it will no longer
     * receive events of the given class from this connection.
     */
    <T extends UserEvent> void unsubscribeEvent(Class<T> eventClass, UserEventSubscriber<T> subscriber);

    /**
     * Removes all the subscribers from this connection, so that events will no longer be notified to anyone.
     */
    void clearSubscribers();

    /**
     * Copies all the subscribers of the given map to the connection subscribers.
     * @apiNote This will delete any previous subscriber added to the connection.
     */
    void copySubscribers(Map<Class<? extends UserEvent>, List<UserEventSubscriber>> subscribers);

    /**
     * Sends the given {@code UserEvent} to the other end of the connection.
     */
    void sendEvent(UserEvent event);

    /**
     * Notifies the given event to the appropriate subscribers of this connection.
     */
    <T extends UserEvent> void publishEvent(T event);

}

package it.polimi.deib.newdem.adrenaline.view.inet;

import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.inet.events.ConnectionCloseEvent;
import it.polimi.deib.newdem.adrenaline.view.inet.events.HeartbeatEvent;
import it.polimi.deib.newdem.adrenaline.view.inet.events.UserEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract user connection that implements the publisher-subscriber mechanism.
 * @see UserConnection for further information.
 */
public abstract class UserConnectionBase implements UserConnection {

    private HashMap<Class<? extends UserEvent>, List<UserEventSubscriber>> subscribers;

    private final HashMap<Class<? extends UserEvent>, List<UserEvent>> pendingEvents;

    private User user;

    private final Object subsMutex = new Object();

    private Thread heartbeatThread;
    private int heartbeatCount;


    public UserConnectionBase(User user) {
        this.subscribers = new HashMap<>();
        this.pendingEvents = new HashMap<>();

        this.user = user;
        user.bindConnection(this);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * NOTE: if you need to override this method, REMEMBER TO CALL {@code super.start()}
     */
    @Override
    public void start() {
        this.heartbeatThread = new Thread(this::doHeartbeat);
        this.heartbeatThread.start();
    }

    @Override
    public <T extends UserEvent> void subscribeEvent(Class<T> eventClass, UserEventSubscriber<T> subscriber) {
        synchronized (subsMutex) {
            subscribers.computeIfAbsent(eventClass, key -> new ArrayList<>()).add(subscriber);
        }

        synchronized (pendingEvents) {
            List<UserEvent> eventList = pendingEvents.get(eventClass);
            if (eventList != null) {
                for (UserEvent event : eventList) notifyEvent(event);
                eventList.clear();
            }
        }
    }

    @Override
    public <T extends UserEvent> void unsubscribeEvent(Class<T> eventClass, UserEventSubscriber<T> subscriber) {
        List<UserEventSubscriber> subList = subscribers.get(eventClass);

        if (subList != null) {
            subList.remove(subscriber);
        }
    }

    @Override
    public void clearSubscribers() {
        synchronized (subsMutex) {
            this.subscribers = new HashMap<>();
        }
    }

    @Override
    public void copySubscribers(Map<Class<? extends UserEvent>, List<UserEventSubscriber>> subscribers) {
        synchronized (subsMutex) {
            this.subscribers = new HashMap<>(subscribers);
        }
    }

    /**
     * NOTE: if you need to override this method, REMEMBER TO CALL {@code super.close()}
     */
    @Override
    public void close() {
        notifyEvent(new ConnectionCloseEvent());

        if (heartbeatThread != null) {
            this.heartbeatThread.interrupt();
            this.heartbeatThread = null;
        }

        // empty subscribers
        synchronized (subsMutex) {
            this.subscribers = new HashMap<>();
        }

        user.bindConnection(null);
    }

    protected void notifyEvent(UserEvent event) {
        event.publish(this);
    }

    /**
     * @implNote the unchecked cast was necessary in order to accomplish a good decoupling between events and subscribers.
     * This way, event receivers will be able to subscribe only to the events they are interested in, and
     * new UserEvent subclasses can be added without the need to modify the code of this class.
     *
     * However, the cast will always be successful since this class will make sure that, in the {@code subscribers} map,
     * subscribers of type {@code T} will always be indexed by a {@code Class<? extends T>} object.
     */
    @SuppressWarnings("unchecked")
    public <T extends UserEvent> void publishEvent(T event) {
        List<UserEventSubscriber> subscriberList = subscribers.get(event.getClass());

        if (subscriberList != null && !subscriberList.isEmpty()) {
            for (UserEventSubscriber<? extends UserEvent> subscriber : new ArrayList<>(subscriberList)) {
                try {
                    UserEventSubscriber<T> castedSubscriber = (UserEventSubscriber<T>)(subscriber);

                    new Thread(() -> castedSubscriber.receiveEvent(this, event)).start();
                } catch (ClassCastException x) {
                    // this should not happen, but it is not a problem.
                }
            }
        } else {
            synchronized (pendingEvents) {
                pendingEvents.computeIfAbsent(event.getClass(), key -> new ArrayList<>()).add(event);
            }
        }
    }

    private void doHeartbeat() {
        try {
            resetHeartbeat();

            do {
                Thread.sleep(1000);

                this.heartbeatCount -= 1;
                if (heartbeatCount <= 0) {
                    close();
                    break;
                }

                sendEvent(new HeartbeatEvent());
            } while (true);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        } catch (Exception x) {
            // no problem, kill the thread.
        }
    }

    @Override
    public void resetHeartbeat() {
        this.heartbeatCount = 5;
    }
}

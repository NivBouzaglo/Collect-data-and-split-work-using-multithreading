package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Deque;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, Deque<Message>> microservices = null;
    private ConcurrentHashMap<Class<? extends Event<?>>, Deque<MicroService>> events = null;
    private ConcurrentHashMap<Class<? extends Broadcast>, Deque<MicroService>> broadcasts = null;
    private static MessageBusImpl INSTANCE = null;
    private Object lockBroadcast;
    private Object lockEvent;

    public MessageBusImpl() {
        microservices = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        broadcasts = new ConcurrentHashMap<>();
        lockBroadcast = new Object();
        lockEvent = new Object();
    }

    public static MessageBusImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageBusImpl();
        }
        return INSTANCE;
    }

    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!events.containsKey(type))
                events.put(type, new LinkedBlockingDeque<MicroService>());
            events.get(type).addFirst(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!broadcasts.containsKey(type))
            broadcasts.put(type, new LinkedBlockingDeque<MicroService>());
        broadcasts.get(type).add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        synchronized (this) {
            e.action(result);
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (!broadcasts.containsKey(b.getClass())) {
            // throw new IllegalArgumentException("don't have microservice that subscribe this broadcast");
            System.out.println("don't have microservice that subscribe this broadcast");
        } else if (microservices != null && broadcasts != null)
            synchronized (lockBroadcast) {
                for (MicroService m : broadcasts.get(b.getClass())) {
                    if (m != null && registered(m))
                        if (b.getClass().equals(TerminateBroadcast.class))
                            microservices.get(m).addFirst(b);
                        else
                            microservices.get(m).add(b);
                    lockBroadcast.notifyAll();
                }
            }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (!events.containsKey(e.getClass()))
            return null;
        else {
            synchronized (lockEvent) {
                MicroService getTheEvent = roundRobin(events.get(e.getClass()));
                microservices.get(getTheEvent).add(e);
                Future<T> future = new Future<>();
                lockEvent.notifyAll();
                return future;
            }
        }
    }

    private MicroService roundRobin(Queue<MicroService> microServices) {
        MicroService m = microServices.poll();
        microServices.add(m);
        return m;
    }

    @Override
    public void register(MicroService m) {
        if (!microservices.containsKey(m)) {
            microservices.put(m, new LinkedBlockingDeque<>());
        }
    }

    @Override
    public void unregister(MicroService m) {
        // TODO Auto-generated method stub
        if (!registered(m)) {
            throw new IllegalArgumentException("this microservice not registered");
        } else {
                Queue<Message> remove = microservices.get(m);
                for (Message d : remove) {
                    if (d instanceof Event)
                        events.get(d.getClass()).remove(m);
                    if (d instanceof Broadcast)
                        broadcasts.get(d.getClass()).remove(m);
                }
            }
        microservices.get(m).clear();
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (m != null && microservices != null && microservices.get(m) != null) {
            System.out.println("wait for msg");
            synchronized (lockBroadcast) {
                while (microservices.get(m).isEmpty()) try {
                    lockBroadcast.wait();
                } catch (InterruptedException e) {
                }
            }
            return microservices.get(m).remove();
        }
        return null;
    }


    @Override
    public boolean BroadcastSended(Broadcast b) {
        if (broadcasts.containsKey(b.getClass())) {
            for (MicroService m : broadcasts.get(b.getClass())) {
                if (!microservices.get(m).contains(b))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean EventSended(Event b) {
        if (events.containsKey(b.getClass())) {
            for (MicroService m : events.get(b.getClass())) {
                if (!microservices.get(m).contains(b))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean registered(MicroService m) {
        return microservices.containsKey(m);
    }

    public <T> boolean updateEvent(Class<? extends Event<T>> type, MicroService m) {
        if (events.containsKey(type.getClass()))
            return events.get(type.getClass()).contains(m);
        else
            return false;
    }

    public <T> boolean updateBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (broadcasts.containsKey(type.getClass()))
            return broadcasts.get(type.getClass()).contains(m);
        else
            return false;
    }


}

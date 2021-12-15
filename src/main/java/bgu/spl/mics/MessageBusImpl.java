package bgu.spl.mics;

import java.sql.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microservices;
    private ConcurrentHashMap<Class<? extends Event<?>>, BlockingDeque<MicroService>> events;
    private ConcurrentHashMap<Class<? extends Broadcast>, BlockingDeque<MicroService>> broadcasts;
    private ConcurrentHashMap<Message, Future> eventFuture;
    private static MessageBusImpl INSTANCE = null;
    private Object mlock = new Object();

    public MessageBusImpl() {
        microservices = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        broadcasts = new ConcurrentHashMap<>();
        eventFuture = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageBusImpl();
        }
        return INSTANCE;
    }

    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!events.containsKey(type)) {
            events.put(type, (BlockingDeque<MicroService>) new LinkedBlockingQueue<MicroService>());
        } else
            events.get(type).addFirst(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!broadcasts.containsKey(type)) {
            broadcasts.put(type, (BlockingDeque<MicroService>) new LinkedBlockingQueue<MicroService>());
        } else
            broadcasts.get(type).add(m);

    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        eventFuture.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (!broadcasts.containsKey(b.getClass()))
           // throw new IllegalArgumentException("don't have microservice that subscribe this broadcast");
            System.out.println("don't have microservice that subscribe this broadcast");
        else
            synchronized (mlock) {
                for (MicroService m : broadcasts.get(b.getClass())) {
                    if (!registered(m)) {
                        throw new IllegalArgumentException("didn't register yet");
                    } else
                        microservices.get(m).add(b);
                }
                mlock.notifyAll();
            }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (!events.containsKey(e.getClass()) || events.get(e.getClass()).isEmpty())
            return null;
        else {
            synchronized (mlock) {
                MicroService getTheEvent = roundRobin(events.get(e.getClass()));
                if (getTheEvent != null) {
                    microservices.get(getTheEvent).add(e);
                    Future<T> future = new Future<>();
                    eventFuture.put(e, future);
                    mlock.notifyAll();
                    return future;
                }
            }
        }
        return null;
    }

    private MicroService roundRobin(BlockingDeque<MicroService> microServices) {
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
            synchronized (mlock) {
                BlockingQueue<Message> remove = microservices.remove(m);
                for (Message d : remove) {
                    if (d instanceof Event)
                        events.get(d.getClass()).remove(m);
                    if (d instanceof Broadcast)
                        broadcasts.get(d.getClass()).remove(m);
                }
                mlock.notifyAll();
                m.terminate();
            }
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!registered(m))
            throw new InterruptedException("not registered");
        else {
            while (microservices.get(m).isEmpty()) {
                wait();
            }
            Message message = microservices.get(m).poll();
            return message;
        }
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

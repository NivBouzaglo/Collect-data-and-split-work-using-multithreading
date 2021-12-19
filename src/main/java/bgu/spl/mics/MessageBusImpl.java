package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, Deque<Message>> microservices = null;
    private ConcurrentHashMap<Class<? extends Event<?>>, List<MicroService>> events = null;
    private ConcurrentHashMap<Class<? extends Broadcast>, Deque<MicroService>> broadcasts = null;
    private static MessageBusImpl INSTANCE = null;
    private Object lockBroadcast;
    private Object lockEvent;
    private ConcurrentHashMap<MicroService, Boolean> busyServices = null;

    public MessageBusImpl() {
        microservices = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        broadcasts = new ConcurrentHashMap<>();
        lockBroadcast = new Object();
        lockEvent = new Object();
        busyServices = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageBusImpl();
        }
        return INSTANCE;
    }

    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!events.containsKey(type))
            events.put(type, new ArrayList<>());
        events.get(type).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!broadcasts.containsKey(type))
            broadcasts.put(type, new LinkedBlockingDeque<MicroService>());
        broadcasts.get(type).add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        e.action(result);
        busyServices.remove(e.getService());
        busyServices.put(e.getService(), false);

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (!broadcasts.containsKey(b.getClass())) {
            // throw new IllegalArgumentException("don't have microservice that subscribe this broadcast");
            System.out.println("don't have microservice that subscribe this broadcast");
        } else if (microservices != null && broadcasts != null)
            for (MicroService m : broadcasts.get(b.getClass())) {
                synchronized (m) {
                    if (m != null && registered(m))
                        if (b.getClass().equals(TickBroadcast.class) || b.getClass().equals(TerminateBroadcast.class) && !(m instanceof StudentService)) {
                            microservices.get(m).addFirst(b);
                        } else
                            microservices.get(m).add(b);
                    m.notifyAll();
                }
            }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (!events.containsKey(e.getClass()))
            return null;
        else {
            MicroService getTheEvent = null;
            if (e instanceof TestModelEvent) {
                for (int i = 0; i < events.get(TestModelEvent.class).size(); i++) {
                    if (events.get(TestModelEvent.class).get(i).equals(((TestModelEvent) e).getModel()))
                        getTheEvent = events.get(TestModelEvent.class).get(i);
                }
            } else
                getTheEvent = roundRobin(events.get(e.getClass()));
            if (getTheEvent != null) {
                synchronized (getTheEvent) {
                    microservices.get(getTheEvent).add(e);
                    Future<T> future = new Future<>();
                    getTheEvent.notifyAll();
                    return future;
                }
            }
        }
        return null;

    }

    private MicroService roundRobin(List<MicroService> microServices) {
        if (!microServices.isEmpty()) {
            int count = 0;
            MicroService m = microServices.remove(count);
            if (busyServices.get(m) != null) {
                while (busyServices.get(m) && count < microServices.size()) {
                    microServices.add(m);
                    count++;
                    microServices.get(count);
                }
            } else
                microServices.add(m);
            return m;
        }
        return null;
    }

    @Override
    public void register(MicroService m) {
        if (!microservices.containsKey(m)) {
            microservices.put(m, new LinkedBlockingDeque<>());
            busyServices.put(m, false);
        }
    }

    public void unregister(MicroService m) {
        if (!registered(m)) {
            throw new IllegalArgumentException("this microservice not registered");
        } else {
            synchronized (m) {
                microservices.remove(m);
                for (Class d : events.keySet())
                    if (checkEvent(d, m))
                        events.get(d).remove(m);
                for (Class d : broadcasts.keySet())
                    if (checkBroadcast(d, m))
                        broadcasts.get(d).remove(m);

            }
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (m != null && microservices != null && microservices.get(m) != null) {
            synchronized (m) {
                while (microservices.get(m).isEmpty()) {
                    m.wait();
                }
            }
        }
        synchronized (this) {
            notifyAll();
        }
        Message e = microservices.get(m).peek();
        if (m instanceof GPUService && e.getClass().equals(TrainModelEvent.class)) {
            busyServices.remove(m);
            busyServices.put(m, true);
            return microservices.get(m).remove();
        } else
            return microservices.get(m).remove();
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

    public <T> boolean checkEvent(Class c, MicroService m) {
        if (events.containsKey(c))
            return events.get(c).contains(m);
        else
            return false;
    }

    public <T> boolean updateBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (broadcasts.containsKey(type.getClass()))
            return broadcasts.get(type.getClass()).contains(m);
        else
            return false;
    }

    public <T> boolean checkBroadcast(Class c, MicroService m) {
        if (broadcasts.containsKey(c))
            return broadcasts.get(c).contains(m);
        else
            return false;
    }

}
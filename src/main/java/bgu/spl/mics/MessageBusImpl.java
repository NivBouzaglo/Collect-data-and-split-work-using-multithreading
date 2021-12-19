package bgu.spl.mics;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, Deque<Event>> microservicesEvent;
    private ConcurrentHashMap<MicroService, Deque<Broadcast>> microservicesBroadcast;
    private ConcurrentHashMap<Class<? extends Event<?>>, List<MicroService>> events = null;
    private ConcurrentHashMap<Class<? extends Broadcast>, Deque<MicroService>> broadcasts = null;
    private static MessageBusImpl INSTANCE = null;
    int count = 0;


    public MessageBusImpl() {
        microservicesEvent = new ConcurrentHashMap<>();
        microservicesBroadcast = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        broadcasts = new ConcurrentHashMap<>();
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
        e.getFuture().resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (!broadcasts.containsKey(b.getClass())) {
            // throw new IllegalArgumentException("don't have microservice that subscribe this broadcast");
            System.out.println("don't have microservice that subscribe this broadcast");
        } else if (microservicesBroadcast != null && broadcasts != null)
            for (MicroService m : broadcasts.get(b.getClass())) {
                synchronized (m) {
                    if (m != null && registered(m))
                        if (PublishConferenceBroadcast.class.equals(b.getClass()) || b.getClass().equals(TerminateBroadcast.class) && !(m instanceof StudentService)) {
                            microservicesBroadcast.get(m).addFirst(b);
                        } else
                            microservicesBroadcast.get(m).add(b);
                //   System.out.println("Notify "+ m.getName());
                    m.notifyAll();
                }
            }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        if (!events.containsKey(e.getClass()))
            return null;
        MicroService getTheEvent = roundRobin(events.get(e.getClass()));
        while (getTheEvent==null){

        }
        if (getTheEvent != null) {
            synchronized (getTheEvent) {
                microservicesEvent.get(getTheEvent).addFirst(e);
                Future<T> future = new Future<>();
                getTheEvent.notifyAll();
                //  System.out.println("Notify "+ getTheEvent.getName());
                return future;
            }
        }
        return null;
    }



    private MicroService roundRobin(List<MicroService> microServices) {
            if (!microServices.isEmpty()) {
                MicroService m = microServices.get(count);
                count++;
                if (count == microServices.size())
                    count = 0;
                synchronized (microServices) {
                    microServices.notifyAll();
                }
                return m;
            }
        return null;
    }

    @Override
    public void register(MicroService m) {
        if (!microservicesBroadcast.containsKey(m)) {
            microservicesBroadcast.put(m, new LinkedBlockingDeque<>());
        }
        if (!microservicesEvent.containsKey(m))
            microservicesEvent.put(m, new LinkedBlockingDeque<>());
    }

    public void unregister(MicroService m) {
        if (!registered(m)) {
            throw new IllegalArgumentException("this microservice not registered");
        } else {
            microservicesBroadcast.remove(m);
            microservicesEvent.remove(m);
            for (Class d : events.keySet())
                if (checkEvent(d, m))
                    events.get(d).remove(m);
            for (Class d : broadcasts.keySet())
                if (checkBroadcast(d, m))
                    broadcasts.get(d).remove(m);
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (m == null) {
            throw new IllegalArgumentException("M IS NULL");
        }
        if (m instanceof GPUService) {
            while (true) {
                if (((GPUService) m).getGpu().getModel() == null && !microservicesEvent.get(m).isEmpty()) {
                    return microservicesEvent.get(m).poll();
                } else if (!microservicesBroadcast.get(m).isEmpty()){
                    return microservicesBroadcast.get(m).poll();}
                else
                    synchronized (m) {
                    System.out.println("Wait "+ m.getName());
                        m.wait();
                    }
            }
        }
        else{
            while (true){
                if (!microservicesEvent.get(m).isEmpty()){
                    return microservicesEvent.get(m).poll();
                }
                else if (!microservicesBroadcast.get(m).isEmpty()){
                    return microservicesBroadcast.get(m).poll();
                }else synchronized (m){
                    m.wait();
                }

            }
        }
    }


    @Override
    public boolean BroadcastSended(Broadcast b) {
        if (broadcasts.containsKey(b.getClass())) {
            for (MicroService m : broadcasts.get(b.getClass())) {
                if (!microservicesBroadcast.get(m).contains(b))
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
                if (!microservicesEvent.get(m).contains(b))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean registered(MicroService m) {
        return microservicesBroadcast.containsKey(m) || microservicesEvent.containsKey(m);
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
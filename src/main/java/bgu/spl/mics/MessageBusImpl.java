package bgu.spl.mics;

import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.finishBroadcast;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<MicroService, LinkedBlockingDeque<Event>> microservicesEvent;
    private ConcurrentHashMap<MicroService, LinkedBlockingDeque<Broadcast>> microservicesBroadcast;
    private ConcurrentHashMap<Class<? extends Event<?>>, LinkedBlockingDeque<MicroService>> events;
    private ConcurrentHashMap<Class<? extends Broadcast>, LinkedBlockingDeque<MicroService>> broadcasts;
    private ConcurrentHashMap<Event,Future> futureMap;
    private static class SingletonHolder{
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public MessageBusImpl() {
        futureMap = new ConcurrentHashMap<>();
        microservicesEvent = new ConcurrentHashMap<>();
        microservicesBroadcast = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
        broadcasts = new ConcurrentHashMap<>();
    }

   public static MessageBusImpl getInstance(){return SingletonHolder.instance;}

    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        events.putIfAbsent(type,new LinkedBlockingDeque<>());
        events.get(type).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        broadcasts.putIfAbsent(type,new LinkedBlockingDeque<>());
        broadcasts.get(type).add(m);
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        futureMap.get(e).resolve(result);
        MessageBusImpl.getInstance().sendBroadcast(new finishBroadcast());
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (!broadcasts.containsKey(b.getClass())) {
            System.out.println("don't have microservice that subscribe this broadcast");
        } else if (microservicesBroadcast != null && broadcasts != null)
            for (MicroService m : broadcasts.get(b.getClass())) {
                synchronized (m) {
                    if (m != null && registered(m))
                        if (PublishConferenceBroadcast.class.equals(b.getClass()) || b.getClass().equals(TerminateBroadcast.class)) {
                            microservicesBroadcast.get(m).addFirst(b);
                        } else
                            microservicesBroadcast.get(m).add(b);
                    m.notifyAll();
                }
            }
    }
    public <T> Future<T> sendEvent(Event<T> e) {
        while (events.get(e.getClass())==null){
            // waiting for someone to subscribe, don't want to lose event.
        }
        roundRobin(events.get(e.getClass()),e);
        Future future = new Future();
        futureMap.put(e,future);
        return future;
    }



    private void roundRobin(LinkedBlockingDeque<MicroService> microServices,Event event) {
        synchronized (event.getClass()) {
            if (!microServices.isEmpty()) {
                MicroService micro = microServices.poll();
                microServices.add(micro);
                MessageBusImpl.getInstance().microservicesEvent.get(micro).add(event);
            }
        }
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
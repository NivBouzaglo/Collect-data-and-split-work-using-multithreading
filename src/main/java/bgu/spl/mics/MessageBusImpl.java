package bgu.spl.mics;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private HashMap<MicroService, Queue<Message>> microservices;
	private HashMap<Class<? extends Event<?>>, BlockingDeque<MicroService>> events;
	private HashMap<Class<? extends Broadcast>, BlockingDeque<MicroService>> broadcasts;
	private static MessageBusImpl INSTANCE = null;

	public MessageBusImpl(){
		microservices = new HashMap<>();
		events = new HashMap<>();
		broadcasts=new HashMap<>();
	}

	public static MessageBusImpl getInstance(){
		if(INSTANCE == null) {
			INSTANCE = new MessageBusImpl();
		}
		return INSTANCE;
	}
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!events.containsKey(type)){
			events.put(type, (BlockingDeque<MicroService>) new LinkedBlockingQueue<MicroService>());
		}
		else
			events.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcasts.containsKey(type)){
			broadcasts.put(type, (BlockingDeque<MicroService>) new LinkedBlockingQueue<MicroService>());
		}
		else
			broadcasts.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	synchronized public void sendBroadcast(Broadcast b) {
		if (broadcasts.containsKey(b.getClass()))
			for(MicroService m : broadcasts.get(b.getClass())) {
				microservices.get(m).add(b);
			}

		notifyAll();
	}

	@Override
	synchronized public <T> Future<T> sendEvent(Event<T> e) {
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!microservices.containsKey(m)){
			microservices.put(m  , new LinkedBlockingDeque<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!registered(m))
			throw new InterruptedException("not registered");
		else
			while (microservices.get(m).isEmpty()){
				wait();
			}
			Message message =microservices.get(m).poll();
			return message;
	}

	@Override
	public boolean BroadcastSended(Broadcast b) {
		if (broadcasts.containsKey(b.getClass())){
			for (MicroService m : broadcasts.get(b.getClass())){
				if (!microservices.get(m).contains(b))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean EventSended(Event b) {
		if (events.containsKey(b.getClass())){
			for (MicroService m : events.get(b.getClass())){
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

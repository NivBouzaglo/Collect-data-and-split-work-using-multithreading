package bgu.spl.mics;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return null;
	}

	@Override
	public <T> boolean IsSubscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		return false;
	}

	@Override
	public boolean IsSubscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return false;
	}

	@Override
	public boolean BroadcastSended(Broadcast b) {
		return false;
	}

	@Override
	public boolean EventSended(Event b) {
		return false;
	}

	@Override
	public boolean registered(MicroService m) {
		return false;
	}

	public <T> boolean updateEvent(Class<? extends Event<T>> type, MicroService m) {
		return false;
	}

	public <T> boolean updateBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return false;
	}


}

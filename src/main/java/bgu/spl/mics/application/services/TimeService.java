package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	//added by bar
	private int speed;
	private int duration;
	private Timer timer;
	private TimerTask task;
	private int currentTime;
	private TickBroadcast tick;
	private LinkedList<Thread> threads;

	public TimeService() {
		super("TIMER SERVICE");
		// TODO Implement this
		this.duration=0;
		this.tick= new TickBroadcast();
		this.speed=0;
		this.timer=new Timer();
		this.currentTime=0;
		this.task= new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(tick);
				currentTime++;
				System.out.println(currentTime);
				if(currentTime>=duration){
					task.cancel();
					end();
				}
			}};
	}
	public void end() {
		timer.cancel();
		sendBroadcast(new TerminateBroadcast());
		timer.cancel();
		for (Thread thread : threads){
			thread.interrupt();
	}
		System.out.println("Finish "+threads.size());

	}

	public void setThreads(LinkedList<Thread> services){
		threads=services;
	}
	//added
	public TimeService(int s, int d){
		super("timer");
		this.duration=d;
		this.speed=s;
		this.timer=new Timer();
		this.currentTime=0;
		this.task= new TimerTask() {
			@Override
			public void run() {
				currentTime++;
				System.out.println(currentTime+"its me");
				sendBroadcast(tick);
				if(currentTime>=duration){
					task.cancel();
					timer.cancel();
					sendBroadcast(new TerminateBroadcast());
					System.out.println("Time was terminated");
					terminate();

					}
			}};
	}
	public void set(int tick , int duration) {
		speed=tick;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		subscribeBroadcast(TerminateBroadcast.class, m->{terminate();});
		timer.scheduleAtFixedRate(task,0,speed);
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public int getDuration() {
		return duration;
	}
}

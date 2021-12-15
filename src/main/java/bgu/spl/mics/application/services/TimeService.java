package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

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

	public TimeService() {
		super("TIMER SERVICE");
		// TODO Implement this
		this.duration=0;
		this.tick= new TickBroadcast();
		this.speed=0;
		this.timer=new Timer();
		this.currentTime=1;
		this.task= new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(tick);
				System.out.println(currentTime);
			}};
	}
	//added
	public TimeService(int s, int d){
		super("");
		this.duration=d;
		this.speed=s;
		this.timer=new Timer();
		this.currentTime=1;
		this.task= new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(tick);
				System.out.println(currentTime++);
			}};
	}
	public void set(int tick , int duration) {
		speed=tick;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		subscribeBroadcast(TickBroadcast.class, t->{
			System.out.println("Got my broadcast");
			currentTime++;
			if(currentTime>=duration){
				task.cancel();
				timer.cancel();
				subscribeBroadcast(TerminateBroadcast.class, m->{terminate();});
				System.out.println("Time was terminated");
				terminate();

		}
		});
		timer.scheduleAtFixedRate(task,speed,duration);
	}


}

package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
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
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    //added by bar
    private int speed;
    private int duration;
    private Timer timer;
    private TimerTask task;
    private int currentTime;
    private TickBroadcast tick;
    private LinkedList<Thread> threads;
    private boolean end;
    private Thread object;
    private MessageBusImpl inc = MessageBusImpl.getInstance();

    public TimeService() {
        super("TIMER SERVICE");
        // TODO Implement this
        this.end = false;
        this.duration = 0;
        this.tick = new TickBroadcast();
        this.speed = 0;
        this.timer = new Timer();
        this.currentTime = 0;
        this.task = new TimerTask() {
            @Override
            public void run() {
                sendBroadcast(tick);
                System.out.println(currentTime);
                currentTime++;
                if (currentTime >= duration) {
                    task.cancel();
                    end();
                }
            }
        };
    }


    public void end() {
        System.out.println("time is over ");
        timer.cancel();
        sendBroadcast(new TerminateBroadcast());
        /*for (Thread t : threads)
            t.interrupt();*/
        end = true;
    }

    public void setThreads(LinkedList<Thread> services) {
        threads = services;
    }

    public void set(int tick, int duration) {
        speed = tick;
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        timer.scheduleAtFixedRate(task, 0, speed);
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
        });
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isEnded() {
        return end;
    }
}

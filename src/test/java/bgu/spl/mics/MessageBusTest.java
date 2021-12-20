package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static org.junit.Assert.*;

public class MessageBusTest {
    MessageBusImpl b;
    Student s;
    CPU c;

    @Before
    public void setUp() {
        b = b.getInstance();
        s = new Student("niv", "computer Science", "PhD");
        c = new CPU(32);
    }

    @Test
    public void subscribeEventTest() {
        MicroService ms = new StudentService(s);
        b.subscribeEvent(ExampleEvent.class, ms);
        assertTrue(b.updateEvent(ExampleEvent.class, ms));
    }

    @Test
    public void subscribeBroadcastTest() {
        MicroService ms = new StudentService(s);
        b.subscribeBroadcast(ExampleBroadcast.class, ms);
        assertTrue(b.updateBroadcast(ExampleBroadcast.class, ms));
        //
    }

    @Test
    public void completeTest() {
        Event<String> event = new ExampleEvent(s.getName());
        Future<String> future = b.sendEvent(event);
        b.complete(event, "good");
        assertEquals("good", future.get());
    }

    @Test
    public void sendBroadcastTest() {
        MicroService m = new StudentService(s);
        b.register(m);
        Broadcast b1 = new ExampleBroadcast("notSubscribe");
        b.sendBroadcast(b1);
        assertFalse(b.BroadcastSended(b1));//test: false --because no microservice that subscribe ExampleBroadcast
        b.subscribeBroadcast(ExampleBroadcast.class, m);
        Broadcast b2 = new ExampleBroadcast("subscribe");
        b.sendBroadcast(b2);
        assertTrue(b.BroadcastSended(b2));//test3: check if it send to m
    }

    @Test
    public void sendEventTest() {
        MicroService m = new StudentService(s);
        b.register(m);
        Event<String> b1 = new ExampleEvent("notSubscribe");
        Future<String> future = b.sendEvent(b1);
        assertEquals(null, future);//test: false --because m don't subscribe ExampleBroadcast
        b.subscribeEvent(ExampleEvent.class, m);
        Event b2 = new ExampleEvent("subscribe");
        Future<String> future1 = b.sendEvent(b2);
        assertTrue(b.EventSended(b2));//test3: check if it send to m
    }

    @Test
    public void registerTest() {
        MicroService m = new StudentService(s);
        assertFalse(b.registered(m));//test 1
        b.register(m);
        assertTrue(b.registered(m));//test 2
    }

    @Test
    public void unRegisterTest() {
        MicroService m = new StudentService(s);
        b.register(m);
        b.subscribeEvent(ExampleEvent.class, m);
        Event b2 = new ExampleEvent("subscribe");
        Future<String> future1 = b.sendEvent(b2);
        b.unregister(m);
        assertFalse(b.registered(m));
    }

    @Test
    public void awaitMessageTest() {
        //test 1:
        MicroService m = new CPUService("cpu 1", c);
        b.register(m);
        b.subscribeEvent(ExampleEvent.class, m);
        b.sendBroadcast(new TickBroadcast());
        try {
            Message p = b.awaitMessage(m);
            assertEquals( p.getClass(), TickBroadcast.class );
        } catch (InterruptedException e) {}
    }

}
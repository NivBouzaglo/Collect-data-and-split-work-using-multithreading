package bgu.spl.mics;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class FutureTest<T> {
    private Future test;

    @Before
    public void setUp() {
        test = new Future();
    }

    @Test
    public void testGet1() {
        test.resolve("Good");
        assertEquals("Good", test.get());
    }

    @Test
    public void testResolve() {
        test.resolve("Good");
        assertTrue(test.isDone());
        assertEquals("good", test.get());
    }

    @Test
    public void testGet2() {
        long timeout = 10;
        assertEquals(null ,test.get(timeout, TimeUnit.SECONDS));
        Thread t1 = new Thread(() -> assertEquals("good", test.get(timeout, TimeUnit.SECONDS)));
        Thread t2 = new Thread(() -> test.resolve("good"));
        t1.start();
        try {
            wait(timeout);
        } catch (Exception ex){}
        t2.start();
        try {
            t2.join();
            t1.join();
        }catch (InterruptedException e){}
        assertEquals("good" ,test.get(timeout, TimeUnit.SECONDS));
    }

}
package bgu.spl.mics;

import static org.junit.Assert.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.platform.engine.TestDescriptor;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class GPUTest {
    private GPU test;
    private Model m;
    private Event e;
    private Cluster c;

    @Before
    public void setUp() {
        e = new ExampleEvent("Test");
        m = new Model(new Student("niv","computer science","PhD"), new Data("image", 2000),"YOLO10");
        c = new Cluster();
        test = new GPU("RTX080", m, e);
    }

    @Test
    public void testGetType() {
        assertEquals("RTX2080" ,test.getType());
    }
    @Test
    public void testGetModel() {
        assertEquals(m, test.getModel());
    }
    @Test
    public void testGetEvent() {
        assertEquals(e, test.getEvent());
    }
    @Test
    public void testGetCluster() {
        assertEquals(c, test.getCluster());
    }
    @Test
    public void testGetBatches() {
        Queue<DataBatch> copy = new LinkedList<DataBatch>();
        assertEquals(copy, test.getDataBatchList());
    }
    @Test
    public void testSendToCluster(){
        DataBatch d = new DataBatch(m.getData(),0);
        int before= test.getDataBatchList().size();
        test.sendToCluster();
        testSendToCluster();
        assertEquals(before-1,test.getDataBatchList().size() );
    }
    @Test
    public void testDivide(){
        test.divide();
        assertEquals(m.getData().getSize(),test.getDataBatchList().size());
    }
    @Test
    public void testTrain(){
        GPU t3090 = new GPU("RTX2080", m, e);
        long  before = test.getTicks();
        t3090.train();
        long after = test.getTicks();
        assertEquals(1,after-before);
        GPU t2080 = new GPU("RTX2080", m,  e);
        before = test.getTicks();
        t2080.train();
        after = test.getTicks();
        assertEquals(2,after-before);
        GPU t1080= new GPU("RTX2080", m, e);
        before = test.getTicks();
        t1080.train();
        after = test.getTicks();
        assertEquals(4,after-before);

    }
    @Test
    public void testReceiveFromCluster(){
        DataBatch unit=new DataBatch(new Data("Text",2000),0);
        test.receiveFromCluster(unit);
        assertEquals(unit,test.getDataBatchList().peek());
    }
    @Test
    public void testSetType(){
        test.setType("RTX3090");
        assertEquals("RTX3090",test.getType());
    }
    @Test
    public void testSetTicks(){
        //max num of ticks we need right now
        int max = 4;
        long before = test.getTicks();
        for (int i=0;i<max;i++){
            assertEquals(before+1,test.getTicks());
            before =test.getTicks();
        }
    }

}
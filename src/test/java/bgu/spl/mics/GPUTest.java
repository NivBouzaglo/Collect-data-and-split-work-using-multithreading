package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class GPUTest {
    private GPU test;
    private Model m;
    private Event e;
    private Cluster c;

    @Before
    public void setUp() {
        e = new ExampleEvent("Test");
        m = new Model(new Student("niv","computer science","PhD"), new Data("Text", 2000),"YOLO10");
        c = Cluster.getInstance();
        test = new GPU("RTX080");
    }

    @Test
    public void testGetType() {
        assertEquals("RTX2080" ,test.getType());
    }
    @Test
    public void testGetModel() {
        test.setModel(m);
        assertEquals(m, test.getModel());
    }
    @Test
    public void testGetCluster() {
        assertEquals(c, test.getCluster());
    }
    @Test
    public void testGetBatches() {
        Queue<DataBatch> copy = test.getBatches();
        assertEquals(copy, test.getDataBatchList());
    }
    @Test
    public void testAddType(){
        assertEquals("RTX080",test.getType());
    }

    @Test
    public void testSendToCluster(){
        DataBatch d = new DataBatch(m.getData(),0);
        test.getBatches().add(d);
        int before= test.getDataBatchList().size();
        test.sendToCluster();
        assertEquals(before-1,test.getDataBatchList().size() );
    }
    @Test
    public void testDivide(){
        test.divide();
        assertEquals(m.getData().getSize()/1000,test.getDataBatchList().size());
    }
    @Test
    public void testSubTrain(){
        test.getBatches().add(new DataBatch(test.getModel().getData(),0));
        test.subTrain();
        assertEquals(0,test.getBatches().size());
        test.subTrain();
        assertEquals(Model.status.Trained,test.getModel().getStatus());
    }
    @Test
    public void testAddTime(){
        test.getProcessed().add(new DataBatch(test.getModel().getData(),0) );
        test.addTime();
        assertEquals(test.getModel().getStatus(), Model.status.Training);
        test.addTime();
        assertEquals(test.getModel().getStatus(), Model.status.Tested);
    }
    @Test
    public void testTest(){
        test.testGPU(test.getModel());
        assertNotSame(Model.result.None,test.getModel().getResult());
    }

}
package bgu.spl.mics;

import static org.junit.Assert.*;
import java.time.Instant;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

public class CPUTest {
    private static CPU test;
    private static Cluster c;
    private static DataBatch unit;

    @Before
    public void setUp() throws Exception {
        test = new CPU(2);
        c = new Cluster();
        unit = new DataBatch(new Data("Tabular",10),1);
    }
    @Test
    public void testGetcores(){
        assertEquals(2,test.getCores());
    }
    @Test
    public void testGetCluster(){
        assertEquals(c, test.getCluster());
    }
    @Test
    public void testGetData(){
        Collection<DataBatch> col=new LinkedList<DataBatch>();
        assertEquals(col,test.getData());
        test.receiveData(unit);
        col.add(unit);
        assertEquals(col,test.getData());
    }
    @Test
    public void testreciveData(){
        Collection<DataBatch> col=new LinkedList<DataBatch>();
        test.receiveData(unit);
        col.add(unit);
        assertEquals(col,test.getData());
        //?
        CPU cpu2= new CPU(0);
        test.receiveData(unit);


    }
    @Test
    public void testSendData(){
        Collection<DataBatch> col=new LinkedList<DataBatch>();
        test.receiveData(unit);
        test.sendData(unit);
        assertEquals(0,test.getData().size());
    }
    @Test
    public void testProcess(){
        DataBatch tabular = new DataBatch(new Data("Tabular",10),1);
        DataBatch text = new DataBatch(new Data("Text",10),2);
        DataBatch images= new DataBatch(new Data("Images",10),3);
        test.receiveData(tabular);
        long before = test.getTicks();
        test.process(unit);
        long after = test.getTicks();
        assertEquals(32/test.getCores(),after-before);

        test.receiveData(text);
        before = test.getTicks();
        test.process(unit);
        after = test.getTicks();
        assertEquals((32/test.getCores())*2,after - before);

        test.receiveData(images);
        before = test.getTicks();
        test.process(unit);
        after = test.getTicks();
        assertEquals((32/test.getCores())*4,after - before);
    }

    @Test
    public void testSetTicks(){
        //max num of ticks we need right now
        int max = 32*4;
        long before = test.getTicks();
        for (int i=0;i<max;i++){
            assertEquals(before+1,test.getTicks());
            before =test.getTicks();
        }
    }
}
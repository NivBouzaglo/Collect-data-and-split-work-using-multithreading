package bgu.spl.mics;

import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class CPUTest {
    private static CPU test;
    private static Cluster c;
    private static DataBatch unit;

    @Before
    public void setUp() throws Exception {
        test = new CPU(32);
        c = new Cluster();
        unit = new DataBatch(new Data("Tabular",10),1);
    }
    @Test
    public void testGetCores(){
        assertEquals(16,test.getCores());
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
        long after = test.getTicks();
        assertEquals(32/test.getCores(),after-before);

        test.receiveData(text);
        before = test.getTicks();
        after = test.getTicks();
        assertEquals((32/test.getCores())*2,after - before);

        test.receiveData(images);
        before = test.getTicks();
        after = test.getTicks();
        assertEquals((32/test.getCores())*4,after - before);
    }
    @Test
    public void testAddTime(){
        test.receiveData(unit);
        test.addTime();
        assertEquals(0,test.getData().size());

    }

}
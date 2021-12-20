package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.services.CPUService;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class CPUTest {
    private static CPU test;
    private static Cluster c;
    private static DataBatch unit;
    MessageBusImpl m = MessageBusImpl.getInstance();

    @Before
    public void setUp() throws Exception {
        test = new CPU(32);
        c = Cluster.getInstance();
        unit = new DataBatch(new Data("Tabular", 10), 1);
    }

    @Test
    public void testGetCores() {
        assertEquals(32, test.getCores());
    }

    @Test
    public void testGetCluster() {
        assertEquals(c, test.getCluster());
    }

    @Test
    public void testGetData() {
        LinkedBlockingDeque<DataBatch> col = (LinkedBlockingDeque<DataBatch>) test.getData();
        test.receiveData(unit);
        col.add(unit);
        assertEquals(col, test.getData());
    }

    @Test
    public void testreciveData() {
        Collection<DataBatch> col = new LinkedList<DataBatch>();
        test.receiveData(unit);
        col.add(unit);
        assertEquals(col, test.getData());

    }

    @Test
    public void testSendData() {
        Collection<DataBatch> col = new LinkedList<DataBatch>();
        test.receiveData(unit);
        test.sendData(unit);
        assertEquals(0, test.getData().size());
    }

    @Test
    public void testAddTime() {
        test.receiveData(unit);
        int before = test.getTicks();
        test.addTime();
        int after = test.getTicks();
        assertEquals(1, after - before);
    }

}
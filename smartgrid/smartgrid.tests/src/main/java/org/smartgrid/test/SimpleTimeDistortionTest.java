package org.smartgrid.test;


import org.evaluation.SmartGrid;
import org.evaluation.SmartMeter;
import org.evaluation.impl.DefaultEvaluationFactory;
import org.kevoree.modeling.api.persistence.Batch;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimeAwareKMFContainer;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 14/11/2013
 * Time: 20:31
 */
public class SimpleTimeDistortionTest {

    public static final int NODES_PER_GRID = 100;
    private static DataStore datastore = new LevelDbDataStore("/Users/duke/Documents/dev/kevoreeTeam/kmf-samples/smartgrid/smartgrid.tests/target/tempTimeDistorted");

    private static void populate(DefaultEvaluationFactory factory) {
        SmartGrid smartgrid = factory.createSmartGrid();
        for (int i = 0; i < NODES_PER_GRID; i++) {
            SmartMeter node = factory.createSmartMeter();
            node.setName("meter_" + i);
            smartgrid.addSmartmeters(node);
            node.setElectricLoad(100000l);
            if (i >= 2) {
                node.addNeighbors(smartgrid.getSmartmeters().get(i - 2));
                node.addNeighbors(smartgrid.getSmartmeters().get(i - 1));
            }
        }
        System.out.println("Persist everything...");
        Batch b = factory.createBatch().addElementAndReachable(smartgrid);
        System.out.println("batchcreated");
        factory.persistBatch(b);
        System.out.println("persited");
        factory.commit();
        System.out.println("done");
    }

    public static void main(String[] args) {


        DefaultEvaluationFactory factory = new DefaultEvaluationFactory();
        factory.setDatastore(datastore);
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        factory.setRelativeTime(TimePoint.object$.create("0"));

        long startPersist = System.currentTimeMillis();
        populate(factory);
        factory.clearCache();

        SmartGrid grid = (SmartGrid) factory.lookup("/");

        for (int i = 1; i < 10000; i++) {
            for (SmartMeter meter : grid.getSmartmeters()) {
                SmartMeter meter2 = (SmartMeter) meter.shift(TimePoint.object$.create(i + ""));
                meter2.setElectricLoad(200000l);
                factory.persist(meter2);
            }
            if (i % 100 == 0) {
                System.out.println(i);
            }

            factory.clearCache();
        }

        factory.commit();
        long endPersist = System.currentTimeMillis();
        System.out.println("Persisted in " + (endPersist - startPersist) + " ms");


        //datastore.dump();
        long before = System.currentTimeMillis();
        computeLast(factory, 90, 100);
        computeLast(factory, 20, 30);
        System.out.println("lookup two elements in " + (System.currentTimeMillis() - before) + "ms");

    }


    public static void computeLast(DefaultEvaluationFactory factory, int begin, int end) {
        factory.clearCache();
        SmartMeter meter5 = (SmartMeter) factory.lookup("smartmeters[meter_5]");
        //System.out.println(meter5.getNeighbors().size());
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        for (int i = end; i > begin; i--) {
            factory.setRelativeTime(new TimePoint(i, 0));
            //System.out.println(factory.getRelativeTime());
            //SmartGrid grid = (SmartGrid) factory.lookup("/");
            //System.out.println(((TimeAwareKMFContainer) grid.getSmartmeters().get(0)).getNow());
            SmartMeter meter = (SmartMeter) factory.lookup("smartmeters[meter_5]");
            //System.out.println(((TimeAwareKMFContainer) meter).getNow());
            //System.out.println(meter.getNeighbors().size());
        }
    }


}

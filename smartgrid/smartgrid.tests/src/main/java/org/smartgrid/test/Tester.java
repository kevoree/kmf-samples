package org.smartgrid.test;


import org.evaluation.EvaluationFactory;
import org.evaluation.SmartGrid;
import org.evaluation.SmartMeter;
import org.evaluation.impl.DefaultEvaluationFactory;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.time.TimeView;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 06/11/2013
 * Time: 10:39
 */
public class Tester {

    public static void print(Object c) {
        System.out.println(c);
    }

    public static void main(String[] args) {

        DefaultEvaluationFactory factory = new DefaultEvaluationFactory();
        factory.setDatastore(new MemoryDataStore());

        TimeView<EvaluationFactory> tp0 = factory.time("0");
        SmartGrid grid = tp0.factory().createSmartGrid().withDescription("Hello");
        SmartMeter meter0 = tp0.factory().createSmartMeter().withName("m0");
        SmartMeter meter1 = tp0.factory().createSmartMeter().withName("m1");
        grid.addSmartmeters(meter0);
        grid.addSmartmeters(meter1);
        meter0.addNeighbors(meter1);
        tp0.commit();

        System.out.println("============= 0 ===============");


        TimeView tp1 = tp0.time("1");
        SmartGrid grid_t1 = (SmartGrid) tp1.lookup("/");
        System.out.println("GridSize=" + grid_t1.getSmartmeters().size());

        // grid_t1.setDescription("LastDescription");
        grid_t1.findSmartmetersByID("m1").delete();

        tp1.commit();


        TimeView tp2 = tp0.time("2");
        SmartGrid grid_t2 = (SmartGrid) tp2.lookup("/");
        System.out.println("GridSize=" + grid_t2.getSmartmeters().size());
        grid_t2.findSmartmetersByID("m0").setElectricLoad(3l);

        tp2.commit();

        TimeView tp3 = tp0.time("3");
        SmartGrid grid_t3 = (SmartGrid) tp3.lookup("/");
        System.out.println("GridSize=" + grid_t3.getSmartmeters().size());
        grid_t3.findSmartmetersByID("m0").delete();

        tp3.commit();

        TimeView tp4 = tp0.time("4");
        SmartGrid grid_t4 = (SmartGrid) tp4.lookup("/");
        System.out.println("GridSize=" + grid_t4.getSmartmeters().size());

        for (int i = 0; i <= 5; i++) {
            TimeView tvi = factory.time(i + "");
            Set<String> elements = tvi.modified();
            for (String elem : elements) {
                System.out.println(i + "->[" + elem + "]---" + tvi.lookup(elem));
            }
        }

        //TODO test the compare



        tp0.time("3").delete().commit();

        for (int i = 0; i <= 5; i++) {
            TimeView tvi = factory.time(i + "");
            Set<String> elements = tvi.modified();
            for (String elem : elements) {
                System.out.println(i + "->[" + elem + "]---" + tvi.lookup(elem));
            }
        }


        System.out.println("GridSize=" + ((SmartGrid)tp0.time("3").lookup("/")).getSmartmeters().size());


        /*
        TimeView tp2 = tp1.time("2");
        SmartGrid grid_t2 = (SmartGrid) tp2.lookup("/");
        print(grid_t2.getNow() + "->" + grid_t2.getDescription());

        print("latest->" + ((SmartGrid) tp2.lookup("/")).lastest());

        print("meterSize=" + grid_t2.getSmartmeters().size());

        grid_t2.delete();
        tp2.commit();

        SmartGrid grid_t2_bis = (SmartGrid) tp2.lookup("/");
        print(tp2.now() + "->" + grid_t2_bis);

        SmartGrid grid_t1_pas = (SmartGrid) tp0.time("1").lookup("/");
        print(grid_t1_pas.getNow() + "->" + grid_t1_pas.getDescription() + ",previous=" + grid_t1_pas.previous() + ",next=" + grid_t1_pas.next());

        SmartGrid grid_t0_pas = (SmartGrid) tp0.time("0").lookup("/");
        print(grid_t0_pas.getNow() + "->" + grid_t0_pas.getDescription() + ",previous=" + grid_t0_pas.previous() + ",next=" + grid_t0_pas.next());


        print("latest->" + ((SmartGrid) tp0.time("1").lookup("/")).lastest());
        print("globalLatest->" + tp0.globalLatest());
        */









        /*
        SmartGrid grid_t1 = (SmartGrid) grid_t0.shift(grid_t0.getNow().shift(1l));
        System.out.println(grid_t1.getNow() + "->" + grid_t1.getDescription());


        DefaultEvaluationFactory factory2 = new DefaultEvaluationFactory();
        factory2.setDatastore(factory.getDatastore());
        SmartGrid grid_latest = (SmartGrid) factory2.lookup("/");
        System.out.println(grid_latest.getNow() + "->" + grid_latest.getDescription());

        SmartGrid grid_latest_p1 = (SmartGrid) grid_latest.shift(grid_latest.getNow().shift(1l));
        grid_latest_p1.setDescription("LastDescription");

        factory2.commit();
        SmartGrid grid_latest_2 = (SmartGrid) factory2.lookup("/");
        System.out.println(grid_latest_2.getNow() + "->" + grid_latest_2.getDescription());
        */
    }


    /*
    private static void populate(DefaultCloudFactory factory) {
        Cloud cloud = factory.createCloud();
        Node node0 = factory.createNode();
        node0.setId("node0");
        cloud.addNodes(node0);
        Software soft0 = factory.createSoftware();
        soft0.setName("soft0");
        node0.addSoftwares(soft0);
        System.out.println("Persist " + cloud + "/" + node0 + soft0);
        factory.persistBatch(factory.createBatch().addElementAndReachable(cloud));
    }

    public static void main(String[] args) {

        DefaultCloudFactory factory = new DefaultCloudFactory();
        factory.setDatastore(new MemoryDataStore());

        System.out.println("Populate");
        populate(factory);
        factory.clearCache();
        System.out.println("DataStore export");

        MemoryDataStore datastore = (MemoryDataStore) factory.getDatastore();
        System.out.println("Dump Traces");
        for (String key : datastore.getMaps().get("trace").keySet()) {
            System.out.println(key + "->" + datastore.getMaps().get("trace").get(key));
        }
        System.out.println("Dump Types");
        for (String key : datastore.getMaps().get("type").keySet()) {
            System.out.println(key + "->" + datastore.getMaps().get("type").get(key));
        }

        System.out.println("Lookup from DataStore");

        Cloud cloudLazy = (Cloud) factory.lookup("/");
        //System.out.println(cloudLazy);
        System.out.println(cloudLazy.findNodesByID("node0"));
        System.out.println(cloudLazy.getNodes().get(0));


        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]"));
        System.out.println(factory.lookup("nodes[node0]/softwares[soft0]"));

        KMFContainerProxy lazyNode = (KMFContainerProxy) cloudLazy.getNodes().get(0);
        System.out.println(lazyNode.getIsResolved());

        System.out.println(cloudLazy.getNodes().get(0).getSoftwares().get(0));

        System.out.println(lazyNode.getIsResolved());


        KMFContainerProxy cloudLazyProxy = (KMFContainerProxy) cloudLazy;
        System.out.println(cloudLazyProxy.getIsResolved());

        System.out.println(cloudLazy.findByPath("nodes[node0]"));
        System.out.println(cloudLazy.findByPath("nodes[node0]").eContainer());
        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]").eContainer());
        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]").eContainer().path());

        cloudLazy.visit(new ModelVisitor() {
            @Override
            public void visit(KMFContainer kmfContainer, String s, KMFContainer kmfContainer2) {
                System.out.println("visit=" + kmfContainer);
                KMFContainerProxy proxy = (KMFContainerProxy) kmfContainer;
                System.out.println(proxy.getIsResolved());
            }
        }, true, true, true);

    }
    */


}

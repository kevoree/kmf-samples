package smartgrid.incremental;

import org.apache.commons.io.FileUtils;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;
import org.smartgrid.*;
import org.smartgrid.impl.DefaultSmartGridFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by duke on 3/19/14.
 */
public class CreationLazyTest {

    private static final int NODES_PER_HUBS = 1000;
    private static Random random = new Random();
    private static float mb = 1024 * 1024;

    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("manipulationLazySamplingResult.csv");
        writer.append("nbElements,LUTime,LUMemory,MUTime,MUMemory\n");
        writer.flush();

        File dbdir = new File("tempdb");
        dbdir.mkdirs();
        DataStore datastore = new LevelDbDataStore(dbdir.getPath());

        SmartGridFactory factory = new DefaultSmartGridFactory();
        factory.setDatastore(datastore);
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        factory.setRelativeTime(TimePoint.object$.create("0"));

        for(int i=0;i<1;i++){
            SmartGridModel model = factory.createSmartGridModel();
            factory.persist(model);
            factory.commit();
        }

        int nbelement = 0;
        Runtime runtime = Runtime.getRuntime();

        for (int loop = 1; loop < 100; loop = loop + 2) {
            factory.setRelativeTime(TimePoint.object$.create(loop + ""));
            long before = System.currentTimeMillis();
            nbelement = nbelement + performModification(factory);
            factory.commit();
            long after = System.currentTimeMillis();
            try {
                System.gc();
                Thread.sleep(500);//the GC try to collect a bit memory
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            float memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / mb;
            long time = after - before;

            writer.append(nbelement + "," + time + "," + memoryUsed);
            writer.flush();

            //cleanup
            try {
                factory.commit();
                factory.clearCache();
                datastore.sync();
                System.gc();
                Thread.sleep(1000);//the GC try to collect a bit memory
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //minor update
            before = System.currentTimeMillis();
            performMinorModification(factory);
            factory.commit();
            after = System.currentTimeMillis();
            try {
                System.gc();
                Thread.sleep(500);//the GC try to collect a bit memory
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / mb;
            time = after - before;
            writer.append("," + time + "," + memoryUsed + "\n");

            //cleanup
            factory.commit();
            factory.clearCache();
            try {
                factory.commit();
                factory.clearCache();
                datastore.sync();
                System.gc();
                Thread.sleep(500);//the GC try to collect a bit memory
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(loop + "->" + nbelement);

        }

        writer.flush();
        writer.close();

        datastore.sync();
        FileUtils.deleteDirectory(dbdir);
    }

    private static int nbHub = 0;

    private static int performModification(SmartGridFactory factory) {
        //attach a concentrator and meter
        Concentrator concentrator = factory.createConcentrator();
        concentrator.setSerialNumber("hub_" + nbHub);
        nbHub++;
        SmartGridModel model = (SmartGridModel) factory.lookupFromTime("/", TimePoint.object$.create("0"));
        model.addEntities(concentrator);
        factory.persist(concentrator);

        concentrator.setConsumption(factory.createMeasuredData());
        concentrator.getConsumption().setCurrent(random.nextLong());
        concentrator.getConsumption().setMeasuringTime(random.nextLong());
        concentrator.getConsumption().setProduction(random.nextLong());
        concentrator.getConsumption().setVoltage(random.nextLong());

        for (int meteri = 0; meteri < NODES_PER_HUBS; meteri++) {
            Meter meter = factory.createSmartMeter();
            meter.setSerialNumber("meter_" + meteri);
            concentrator.addChildren(meter);
            meter.setConsumption(factory.createMeasuredData());
            meter.getConsumption().setCurrent(random.nextLong());
            meter.getConsumption().setMeasuringTime(random.nextLong());
            meter.getConsumption().setProduction(random.nextLong());
            meter.getConsumption().setVoltage(random.nextLong());
            factory.persist(meter);
            factory.persist(meter.getConsumption());
        }
        return 2 + (NODES_PER_HUBS * 2);
    }

    private static void performMinorModification(SmartGridFactory factory) {
        //attach a concentrator and meter
        SmartMeter meter = (SmartMeter) factory.lookup("entities[hub_" + (nbHub - 1) + "]/children[meter_20]");
        meter.setMaxAllowedPower(random.nextLong());
        meter.shiftOffset(1);
        factory.persist(meter);
    }


}

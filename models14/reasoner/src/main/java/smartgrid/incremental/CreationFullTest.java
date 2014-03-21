package smartgrid.incremental;

import org.apache.commons.io.FileUtils;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;
import org.smartgrid.*;
import org.smartgrid.impl.DefaultSmartGridFactory;
import org.smartgrid.loader.JSONModelLoader;
import org.smartgrid.serializer.JSONModelSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by duke on 3/19/14.
 */
public class CreationFullTest {

    private static final int NODES_PER_HUBS = 1000;
    private static Random random = new Random();
    private static float mb = 1024 * 1024;
    private static int nbHub = 0;

    private static JSONModelSerializer saver = new JSONModelSerializer();
    private static JSONModelLoader loader = new JSONModelLoader();

    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("manipulationFullSamplingResult.csv");
        writer.append("nbElements,LUTime,LUMemory,MUTime,MUMemory\n");
        writer.flush();

        File dbdir = new File("tempdb");
        dbdir.mkdirs();
        DataStore datastore = new LevelDbDataStore(dbdir.getPath());
        SmartGridFactory factory = new DefaultSmartGridFactory();
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        factory.setRelativeTime(TimePoint.object$.create("0"));
        SmartGridModel model = factory.createSmartGridModel();
        datastore.put("raw", "0", saver.serialize(model));
        model = null;//clear the 0 version to free memory
        int nbelement = 0;

        Runtime runtime = Runtime.getRuntime();


        for (int loop = 1; loop < 100; loop = loop + 2) {

            //major update
            long before = System.currentTimeMillis();
            //load previous model
            String content = datastore.get("raw", (loop - 1) + "");
            model = (SmartGridModel) loader.loadModelFromString(content).get(0);
            datastore.sync();
            nbelement = nbelement + performLargeModification(factory, model);
            datastore.put("raw", loop + "", saver.serialize(model));
            datastore.sync();
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
                model.delete();
                model = null;
                content = null;
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
            //load previous model

            String previous = datastore.get("raw", loop + "");
            model = (SmartGridModel) loader.loadModelFromString(previous).get(0);
            datastore.sync();
            performMinorModification(factory, model);
            datastore.put("raw", (loop + 1) + "", saver.serialize(model));
            datastore.sync();
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
                model.delete();
                model = null;
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

    private static int performLargeModification(SmartGridFactory factory, SmartGridModel model) {
        //attach a concentrator and meter

        Concentrator concentrator = factory.createConcentrator();
        concentrator.setSerialNumber("hub_" + nbHub);
        concentrator.setConsumption(factory.createMeasuredData());
        concentrator.getConsumption().setCurrent(random.nextLong());
        concentrator.getConsumption().setMeasuringTime(random.nextLong());
        concentrator.getConsumption().setProduction(random.nextLong());
        concentrator.getConsumption().setVoltage(random.nextLong());

        nbHub++;
        model.addEntities(concentrator);
        for (int meteri = 0; meteri < NODES_PER_HUBS; meteri++) {
            Meter meter = factory.createSmartMeter();
            meter.setSerialNumber("meter_" + meteri);
            concentrator.addChildren(meter);
            meter.setConsumption(factory.createMeasuredData());
            meter.getConsumption().setCurrent(random.nextLong());
            meter.getConsumption().setMeasuringTime(random.nextLong());
            meter.getConsumption().setProduction(random.nextLong());
            meter.getConsumption().setVoltage(random.nextLong());

        }
        return 2 + (NODES_PER_HUBS * 2);
    }

    private static void performMinorModification(SmartGridFactory factory, SmartGridModel model) {
        //attach a concentrator and meter
        SmartMeter meter = (SmartMeter) model.findByPath("entities[hub_" + (nbHub - 1) + "]/children[meter_20]");
        meter.setMaxAllowedPower(random.nextLong());
    }

}

package smartgrid.storage;

import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.time.TimePoint;
import org.smartgrid.SmartGridFactory;
import org.smartgrid.SmartGridModel;
import org.smartgrid.SmartMeter;
import org.smartgrid.impl.DefaultSmartGridFactory;
import org.smartgrid.serializer.JSONModelSerializer;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


/**
 * Created by duke on 3/17/14.
 */
public class StorageTest {

    private static JSONModelSerializer saver = new JSONModelSerializer();

    public static int computeJsonStaticVersioning(int percent) {
        SmartGridFactory factory = new DefaultSmartGridFactory();
        int fullSize = 0;
        SmartGridModel model = createModel(factory);
        ByteArrayOutputStream fip = new ByteArrayOutputStream();
        modifyAttributes(percent, model, null);
        saver.serializeToStream(model, fip);
        fullSize += fip.toByteArray().length;

        return fullSize;
    }

    public static void main(String[] args) throws IOException {

        StringBuilder buffer = new StringBuilder();
        buffer.append("percent,fullVersioning,elementVersioning\n");
        SmartGridFactory factoryP = new DefaultSmartGridFactory();
        final int[] counter = {0};
        factoryP.setDatastore(new MemoryDataStore() {
            @Override
            public void put(String segment, String key, String value) {
                String previous = get(segment, key);
                //don't write if exists (for storage accounting only)
                if (previous == null || !previous.equals(value)) {
                    super.put(segment, key, value);
                    counter[0] = counter[0] + (key + value).length();
                }
            }
        });
        SmartGridModel persistentModel = createModel(factoryP);

        factoryP.persistBatch(factoryP.createBatch().addElementAndReachable(persistentModel));

        for (int percent = 1; percent <= 100; percent++) {
            buffer.append(percent);
            buffer.append(",");
            buffer.append(computeJsonStaticVersioning(percent));
            long before = counter[0];
            modifyAttributes(percent, persistentModel, factoryP);
            factoryP.commit();
            long after = counter[0];
            buffer.append(",");
            buffer.append(after - before + "");
            buffer.append("\n");
        }

        FileWriter writer = new FileWriter("storageResult.csv");
        writer.append(buffer.toString());
        writer.close();

    }

    public static SmartGridModel createModel(SmartGridFactory factory) {
        SmartGridModel model = factory.createSmartGridModel();
        for (int i = 0; i < 100; i++) {
            SmartMeter meter = factory.createSmartMeter();
            meter.setSerialNumber("meter_" + i);
            meter.setMaxAllowedPower(random.nextLong());
            model.addEntities(meter);
        }
        return model;
    }

    private static Random random = new Random();

    public static void modifyAttributes(int nbmodify, SmartGridModel model, SmartGridFactory factory) {
        for (int i = 0; i < nbmodify; i++) {
            SmartMeter meter = (SmartMeter) model.findEntitiesByID("meter_" + i);
            meter.setMaxAllowedPower(random.nextLong());
            if (factory != null) {
                meter.setNow(TimePoint.object$.create("" + nbmodify));
                factory.persist(meter);
            }
        }
    }

}

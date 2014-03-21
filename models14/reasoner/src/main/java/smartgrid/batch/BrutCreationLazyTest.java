package smartgrid.batch;

import org.apache.commons.io.FileUtils;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;
import org.smartgrid.*;
import org.smartgrid.impl.DefaultSmartGridFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by duke on 3/19/14.
 */
public class BrutCreationLazyTest {

    private static final int nbHubs = 30;
    private static final int nbMeters = 1000;
    private static final int samples = 2600; //on month
    private static Random random = new Random();

    public static void main(String[] args) throws IOException {
        File dbdir = new File("tempdb");
        dbdir.mkdirs();
        DataStore datastore = new LevelDbDataStore(dbdir.getPath());
        SmartGridFactory factory = new DefaultSmartGridFactory();
        factory.setDatastore(datastore);
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        factory.setRelativeTime(TimePoint.object$.create("0"));

        long startPersist = System.currentTimeMillis();
        SmartGridModel grid = populate(factory);

        System.out.println("Populated");

        factory.commit();
        for (int i = 1; i < samples; i++) {
            for (Entity concentrator : grid.getEntities()) {
                for (Entity meter : concentrator.getChildren()) {
                    SmartMeter meter2 = (SmartMeter) meter.shift(TimePoint.object$.create(i + ""));
                    meter2.setMaxAllowedPower(200000l);
                    factory.persist(meter2);
                }
            }
            //if (i % 100 == 0) {
                System.out.println(i);
            //}
            factory.clearCache();
        }
        factory.commit();
        long endPersist = System.currentTimeMillis();
        System.out.println("Persisted in " + (endPersist - startPersist) + " ms");

        datastore.sync();
        FileUtils.deleteDirectory(dbdir);
    }

    private static SmartGridModel populate(SmartGridFactory factory) {
        SmartGridModel grid = factory.createSmartGridModel();
        factory.persist(grid);
        for (int i = 0; i < nbHubs; i++) {
            Concentrator concentrator = factory.createConcentrator();
            concentrator.setSerialNumber("hub_" + i);
            grid.addEntities(concentrator);
            factory.persist(concentrator);
            for (int meteri = 0; meteri < nbMeters; meteri++) {
                Meter meter = factory.createSmartMeter();
                meter.setSerialNumber("meter_" + meteri);
                concentrator.addChildren(meter);
                factory.persist(meter);
            }
        }
        return grid;
    }


}

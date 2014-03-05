package org.smartgrid.test.reason;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.evaluation.SmartMeter;
import org.evaluation.impl.DefaultEvaluationFactory;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

/**
 * Created by duke on 3/5/14.
 */
public class SimpleDistortedReason {

    private static DataStore datastore = new LevelDbDataStore("/Users/duke/Documents/dev/kevoreeTeam/kmf-samples/smartgrid/smartgrid.tests/target/tempTimeDistorted");

    public static void main(String[] args) {

        DefaultEvaluationFactory factory = new DefaultEvaluationFactory();
        factory.setDatastore(datastore);

        //warmup round
        for (int i = 0; i < 100; i++) {
            predict(factory, 50, 100, 150);
            factory.clearCache();
        }
        factory.clearCache();

        //datastore.dump();
        long before = System.currentTimeMillis();
        System.out.println(predict(factory, 90, 100, 120));
        System.out.println(predict(factory, 20, 30, 50));
        System.out.println("lookup two elements in " + (System.currentTimeMillis() - before) + "ms");
    }

    public static double predict(DefaultEvaluationFactory factory, int begin, int end, int futur) {
        SimpleRegression regression = new SimpleRegression();
        factory.setRelativityStrategy(RelativeTimeStrategy.ABSOLUTE);
        for (int i = end; i > begin; i--) {
            factory.setRelativeTime(new TimePoint(i, 0));
            SmartMeter meter = (SmartMeter) factory.lookup("smartmeters[meter_5]");
            int nbElem = 0;
            int sum = 0;
            for (SmartMeter sm : meter.getNeighbors()) {
                Long conso = sm.getElectricLoad();
                if (conso != null) {
                    sum += conso;
                    nbElem++;
                }
            }
            Long conso = meter.getElectricLoad();
            if (conso != null) {
                sum += conso;
                nbElem++;
            }
            regression.addData(i, sum / nbElem);
        }
        return regression.predict(futur);
    }

}

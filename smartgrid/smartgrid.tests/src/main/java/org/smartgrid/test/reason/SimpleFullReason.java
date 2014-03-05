package org.smartgrid.test.reason;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.evaluation.SmartGrid;
import org.evaluation.SmartMeter;
import org.evaluation.impl.DefaultEvaluationFactory;
import org.evaluation.loader.JSONModelLoader;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.time.RelativeTimeStrategy;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

/**
 * Created by duke on 3/5/14.
 */
public class SimpleFullReason {

    private static DataStore datastore = new LevelDbDataStore("/Users/duke/Documents/dev/kevoreeTeam/kmf-samples/smartgrid/smartgrid.tests/target/tempFullSampling");
    private static String SEGMENT = "default_segment";

    public static void main(String[] args) {

        DefaultEvaluationFactory factory = new DefaultEvaluationFactory();
        factory.setDatastore(datastore);

        //warmup round
        for (int i = 0; i < 100; i++) {
            predict(factory, 10, 20, 150);
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
        for (int i = end; i > begin; i--) {

            factory.clearCache();

            JSONModelLoader loader = new JSONModelLoader();
            long before = System.currentTimeMillis();
            String sample = datastore.get(SEGMENT, new TimePoint(Long.valueOf(i), 0).toString());
            SmartGrid model = (SmartGrid) loader.loadModelFromString(sample).get(0);
            System.out.println(System.currentTimeMillis() - before);

            SmartMeter meter = model.findSmartmetersByID("meter_5");
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

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

import java.util.ArrayList;
import java.util.List;

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

        factory.clearCache();
        int nbTry = 1;

        System.out.println("FullSampling benchmark =========== ");

        long before, after;
        before = System.nanoTime();
        for (int i = 0; i < nbTry; i++) {
            factory.clearCache();
            predict(factory, 60, 90, 120);
        }
        after = System.nanoTime();
        System.out.println("FullSampling Short Deep Prediction (SDP)=" + computTimeMs(before, after, nbTry) + "ms");

        before = System.nanoTime();
        for (int i = 0; i < nbTry; i++) {
            factory.clearCache();
            predict(factory, 200, 5000, 6000);
        }
        after = System.nanoTime();
        System.out.println("FullSampling Long Deep Prediction (LDP)=" + computTimeMs(before, after, nbTry) + "ms");

        before = System.nanoTime();
        for (int i = 0; i < nbTry; i++) {
            factory.clearCache();
            predictWidth(factory, 60, 90, 120);
        }
        after = System.nanoTime();
        System.out.println("FullSampling Short Wide Prediction (SWP)=" + computTimeMs(before, after, nbTry) + "ms");

        before = System.nanoTime();
        for (int i = 0; i < nbTry; i++) {
            factory.clearCache();
            predictWidth(factory, 200, 5000, 6000); //long prediction
        }
        after = System.nanoTime();
        System.out.println("FullSampling Long Wide Prediction (LWP)=" + computTimeMs(before, after, nbTry) + "ms");

    }

    public static double computTimeMs(Long before, Long after, int nbTry) {
        return (((after - before) / nbTry) / 1000000d);
    }


    public static double predict(DefaultEvaluationFactory factory, int begin, int end, int futur) {
        SimpleRegression regression = new SimpleRegression();
        for (int i = end; i > begin; i--) {
            factory.clearCache();
            datastore.sync();
            JSONModelLoader loader = new JSONModelLoader();
            String sample = datastore.get(SEGMENT, new TimePoint(Long.valueOf(i), 0).toString());
            SmartGrid model = (SmartGrid) loader.loadModelFromString(sample).get(0);
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

    public static double predictWidth(DefaultEvaluationFactory factory, int begin, int end, int futur) {
        SimpleRegression regression = new SimpleRegression();
        for (int i = end; i > begin; i--) {
            factory.clearCache();
            datastore.sync();
            JSONModelLoader loader = new JSONModelLoader();
            String sample = datastore.get(SEGMENT, new TimePoint(Long.valueOf(i), 0).toString());
            SmartGrid model = (SmartGrid) loader.loadModelFromString(sample).get(0);
            SmartMeter meter = model.findSmartmetersByID("meter_5");
            List<Long> wideCollection = new ArrayList<Long>();
            deepWidthCollectConso(wideCollection, 20, meter);
            int nb = 0;
            Long total = 0l;
            for (Long wc : wideCollection) {
                total = total + wc;
                nb = nb + 1;
                //perhaps do a mean here
            }
            regression.addData(i, total / nb);
        }
        return regression.predict(futur);
    }

    public static void deepWidthCollectConso(List<Long> result, int range, SmartMeter origin) {
        result.add(origin.getElectricLoad());
        if (range > 0) {
            int newrange = range - 1;
            for (SmartMeter meter : origin.getNeighbors()) {
                deepWidthCollectConso(result, newrange, meter);
            }
        }
    }

}

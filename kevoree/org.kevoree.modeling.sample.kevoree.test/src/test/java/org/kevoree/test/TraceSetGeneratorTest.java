package org.kevoree.test;

import org.junit.Test;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.impl.DeployUnitImpl;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.modeling.api.trace.ModelTrace;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by duke on 26/07/13.
 */
public class TraceSetGeneratorTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void settest() {

        DeployUnitImpl du0 = (DeployUnitImpl) factory.createDeployUnit();
        du0.setName("model");
        du0.setGroupName("modelGroup");
        du0.setVersion("v1");

        DeployUnitImpl du1 = (DeployUnitImpl) factory.createDeployUnit();
        du1.setName("model");
        du1.setGroupName("modelGroup");
        du1.setVersion("v2");

        List<ModelTrace> traces = du0.createTraces(null,false, false,false,true);
        assertTrue(traces.toString(), lookupForTrace(traces, "name"));
        assert(lookupForTrace(traces,"groupName"));
        assert(lookupForTrace(traces,"version"));

        List<ModelTrace> traces2 = du0.createTraces(du1, false,false,false,true);
        assert(lookupForTrace(traces2,"version"));
        assert(!lookupForTrace(traces2,"name"));
        assert(!lookupForTrace(traces2,"groupName"));

        List<ModelTrace> traces3 = du0.createTraces(du1, true,false,false,true);
        assert(!lookupForTrace(traces3,"version"));
        assert(lookupForTrace(traces3,"name"));
        assert(lookupForTrace(traces3,"groupName"));


    }

    private boolean lookupForTrace(List<ModelTrace> traces, String attName) {
        for (ModelTrace trace : traces) {
              if(trace.toString().contains("\"r\":\""+attName+"\"")){
                  return true;
              } /*else {
                  System.out.println("not/"+trace.toString());
              }*/
        }
        return false;
    }


}

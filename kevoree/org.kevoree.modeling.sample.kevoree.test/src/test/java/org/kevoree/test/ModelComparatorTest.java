package org.kevoree.test;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ComponentType;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.modeling.api.trace.ModelTrace;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by duke on 29/07/13.
 */
public class ModelComparatorTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare compare = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void testCompareDiff() {

        ContainerRoot modelM0 = factory.createContainerRoot();

        ComponentType typeDef = factory.createComponentType();
        typeDef.setName("TD1");
        modelM0.addTypeDefinitions(typeDef);

        ContainerNode newNode = factory.createContainerNode();
        newNode.setName("testNode");
        modelM0.addNodes(newNode);

        ContainerRoot modelM1 = cloner.clone(modelM0);

        List<ModelTrace> traces = compare.diff(modelM0, modelM1).getTraces();
        printTraces(traces);
        assert (traces.size() == 0);

        ContainerNode newNode2 = factory.createContainerNode();
        newNode2.setName("testNode2");
        newNode2.setHost(modelM1.findNodesByID("testNode"));
        modelM1.addNodes(newNode2);


        traces = compare.diff(modelM0, modelM1).getTraces();
        printTraces(traces);



    }

    @Test
    public void testCompareInter() {

        ContainerRoot modelM0 = factory.createContainerRoot();
        ComponentType typeDef = factory.createComponentType();
        typeDef.setName("TD1");
        modelM0.addTypeDefinitions(typeDef);

        ContainerNode newNode = factory.createContainerNode();
        newNode.setName("testNode");
        modelM0.addNodes(newNode);

        ContainerRoot modelM1 = cloner.clone(modelM0);

        ContainerNode newNode2 = factory.createContainerNode();
        newNode2.setName("testNode2");
        newNode2.setHost(modelM1.findNodesByID("testNode"));
        modelM1.addNodes(newNode2);

        List<ModelTrace> traces = compare.inter(modelM0, modelM1).getTraces();
        for(ModelTrace trace : traces){
           if(trace.toString().equals(newNode2.getName())){
               fail("testNode2 must not be present");
           }
        }

        printTraces(traces);
    }

    private boolean lookupForTrace(List<ModelTrace> traces, String attName, String optionalContent) {
        for (ModelTrace trace : traces) {
            if(trace.toString().contains("refname : \""+attName+"\"")){
                if(optionalContent != null){
                    if(trace.toString().contains("content : \""+optionalContent+"\"") || trace.toString().contains("objPath : \""+optionalContent+"\"")){
                        return true;
                    }
                } else {
                    return true;
                }
            }

        }
        return false;
    }

    private void printTraces(List<ModelTrace> traces){
       for(ModelTrace trace : traces){
           System.out.println(trace);
       }
    }



}

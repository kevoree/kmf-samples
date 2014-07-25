package org.kevoree.test;


import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.modeling.api.trace.TraceSequence;

/**
 * Created by duke on 3/6/14.
 */
public class TraceSpecialCharTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void main() {

        ContainerRoot current = (ContainerRoot) loader.loadModelFromStream(TraceSpecialCharTest.class.getClassLoader().getResourceAsStream("Current.json")).get(0);
        DefaultKevoreeFactory factory = new DefaultKevoreeFactory();
        ContainerNode node = factory.createContainerNode();
        node.setName("n0 \n \r n0");
        current.addNodes(node);

        saver.serializeToStream(current,System.out);

        String model = saver.serialize(current);

        ContainerRoot modelL = (ContainerRoot) loader.loadModelFromString(model).get(0);
        System.out.println(modelL.getNodes().get(0).getName());


            /*
        ModelCompare compare = new DefaultModelCompare();
        TraceSequence seq = compare.merge(current, proposed);

        TraceSequence newtraceSeq = new DefaultTraceSequence();
        newtraceSeq.populateFromString(seq.exportToString());
         */


        TraceSequence seq = comparator.merge(current, modelL);

        TraceSequence newtraceSeq = new TraceSequence(factory);
        newtraceSeq.populateFromString(seq.exportToString());

    }

}

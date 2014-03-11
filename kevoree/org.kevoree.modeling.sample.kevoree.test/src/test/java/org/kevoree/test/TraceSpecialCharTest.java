package org.kevoree.test;


import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.compare.DefaultModelCompare;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.JSONModelLoader;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.serializer.JSONModelSerializer;
import org.kevoree.trace.DefaultTraceSequence;

/**
 * Created by duke on 3/6/14.
 */
public class TraceSpecialCharTest {

    @Test
    public void main() {

        ContainerRoot current = (ContainerRoot) new JSONModelLoader().loadModelFromStream(TraceSpecialCharTest.class.getClassLoader().getResourceAsStream("Current.json")).get(0);
        DefaultKevoreeFactory factory = new DefaultKevoreeFactory();
        ContainerNode node = factory.createContainerNode();
        node.setName("n0 \n \r n0");
        current.addNodes(node);

        JSONModelSerializer saver = new JSONModelSerializer();
        saver.serializeToStream(current,System.out);

        String model = saver.serialize(current);

        JSONModelLoader loader = new JSONModelLoader();
        ContainerRoot modelL = (ContainerRoot) loader.loadModelFromString(model).get(0);
        System.out.println(modelL.getNodes().get(0).getName());


            /*
        ModelCompare compare = new DefaultModelCompare();
        TraceSequence seq = compare.merge(current, proposed);

        TraceSequence newtraceSeq = new DefaultTraceSequence();
        newtraceSeq.populateFromString(seq.exportToString());
         */


        ModelCompare compare = new DefaultModelCompare();
        TraceSequence seq = compare.merge(current, modelL);

        TraceSequence newtraceSeq = new DefaultTraceSequence();
        newtraceSeq.populateFromString(seq.exportToString());

    }

}

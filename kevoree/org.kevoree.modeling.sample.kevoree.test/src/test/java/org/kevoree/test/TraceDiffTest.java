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
import org.kevoree.modeling.api.trace.ModelTrace;

import java.util.List;

/**
 * Created by duke on 26/07/13.
 */
public class TraceDiffTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void difftest() {

        KevoreeFactory factory = new DefaultKevoreeFactory();

        ContainerRoot model = factory.createContainerRoot();

        ContainerNode node1 = factory.createContainerNode();
        node1.setName("node1");
        model.addNodes(node1);

        ContainerNode node2 = factory.createContainerNode();
        node2.setName("node2");
        model.addNodes(node2);

        ContainerRoot model2 = cloner.clone(model);

        List<ModelTrace> traces = comparator.diff(model, model2).getTraces();
        assert(traces.size() == 0);


        ContainerNode node3 = factory.createContainerNode();
        node3.setName("node3");
        model.addNodes(node3);
        traces = comparator.diff(model, model2).getTraces();
        System.out.println(traces);

    }

}

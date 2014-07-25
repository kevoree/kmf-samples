package org.kevoree.test;

import jet.runtime.typeinfo.JetValueParameter;
import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;

import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.events.ModelElementListener;
import org.kevoree.modeling.api.events.ModelEvent;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.modeling.api.trace.Event2Trace;
import org.kevoree.modeling.api.trace.ModelTrace;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.modeling.api.util.ModelTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duke on 26/07/13.
 */
public class TraceRollbackTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void difftest() {

        KevoreeFactory factory = new DefaultKevoreeFactory();
        final Event2Trace converter = new Event2Trace(comparator);

        ContainerRoot model = factory.createContainerRoot();

        ContainerNode node1 = factory.createContainerNode();
        node1.setName("node1");
        model.addNodes(node1);

        ContainerRoot backup = cloner.clone(model);


        ModelTracker tracker = new ModelTracker(comparator);
        tracker.track(model);

        ContainerNode node2 = factory.createContainerNode();
        node2.setName("node2");
        model.addNodes(node2);

        tracker.undo();

        assert (comparator.diff(model, backup).getTraces().size() == 0);

    }

}

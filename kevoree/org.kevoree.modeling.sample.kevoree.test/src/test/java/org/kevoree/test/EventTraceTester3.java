package org.kevoree.test;

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
import org.kevoree.modeling.api.trace.TraceSequence;


public class EventTraceTester3 {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare compare = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void testEventTrace() {
        KevoreeFactory factory = new DefaultKevoreeFactory();
        ContainerRoot model = factory.createContainerRoot();

        ContainerNode node0 = factory.createContainerNode();
        node0.setName("node0");
        model.addNodes(node0);

        ContainerNode web = factory.createContainerNode();
        web.setName("web");
        model.addNodes(web);
        node0.addHosts(web);

        try {

            ContainerRoot clonedModel = (ContainerRoot) cloner.clone(model);

            EventListenerImpl listener = new EventListenerImpl();
            model.addModelTreeListener(listener);

            ContainerNode newChildNode = factory.createContainerNode();
            newChildNode.setName("toto");
            model.addNodes(newChildNode);
            node0.addHosts(newChildNode);

            listener.traceSequence.applyOn(clonedModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class EventListenerImpl implements ModelElementListener {

        TraceSequence traceSequence = new TraceSequence(factory);

        @Override
        public void elementChanged(ModelEvent modelEvent) {
            traceSequence.append(new Event2Trace(compare).convert(modelEvent));
        }
    }


}

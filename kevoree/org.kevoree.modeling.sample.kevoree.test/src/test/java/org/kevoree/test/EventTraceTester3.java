package org.kevoree.test;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.KevoreeFactory;
import org.kevoree.cloner.DefaultModelCloner;
import org.kevoree.compare.DefaultModelCompare;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.events.ModelElementListener;
import org.kevoree.modeling.api.events.ModelEvent;
import org.kevoree.modeling.api.trace.Event2Trace;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.trace.DefaultTraceSequence;


public class EventTraceTester3 {

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

            ContainerRoot clonedModel = (ContainerRoot) new DefaultModelCloner().clone(model);

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

        TraceSequence traceSequence = new DefaultTraceSequence();
        ModelCompare compare = new DefaultModelCompare();

        @Override
        public void elementChanged(ModelEvent modelEvent) {
            traceSequence.append(new Event2Trace(compare).convert(modelEvent));
        }
    }


}

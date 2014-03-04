package org.kevoree.test;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.KevoreeFactory;
import org.kevoree.cloner.DefaultModelCloner;
import org.kevoree.compare.DefaultModelCompare;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.modeling.api.trace.TraceSequence;


public class EventTraceTester2 {

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

            ContainerNode newChildNode = factory.createContainerNode();
            newChildNode.setName("toto");
            model.addNodes(newChildNode);
            node0.addHosts(newChildNode);

            ContainerRoot clonedModel = (ContainerRoot) new DefaultModelCloner().clone(model);

            node0.removeHosts(newChildNode);
            model.removeNodes(newChildNode);

            TraceSequence tseq = new DefaultModelCompare().diff(clonedModel, model) ;

            System.out.println(tseq);

            tseq.applyOn(clonedModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

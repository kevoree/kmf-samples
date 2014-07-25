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


public class EventTraceTester2 {

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

            ContainerNode newChildNode = factory.createContainerNode();
            newChildNode.setName("toto");
            model.addNodes(newChildNode);
            node0.addHosts(newChildNode);

            ContainerRoot clonedModel = (ContainerRoot) cloner.clone(model);

            node0.removeHosts(newChildNode);
            model.removeNodes(newChildNode);

            TraceSequence tseq = compare.diff(clonedModel, model) ;

            System.out.println(tseq);

            tseq.applyOn(clonedModel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

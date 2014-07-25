package org.kevoree.test;

import org.junit.Test;
import org.kevoree.*;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.ModelSerializer;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;

/**
 * Created by duke on 3/13/14.
 */
public class DeleteRecusiveTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare compare = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void test() {
        ContainerRoot model = factory.createContainerRoot();
        ContainerNode node = factory.createContainerNode();
        node.setName("node0");
        model.addNodes(node);
        Group grp = factory.createGroup();
        grp.setName("grp");
        model.addGroups(grp);
        grp.addSubNodes(node);

        ComponentInstance comp = factory.createComponentInstance();
        comp.setName("comp");
        node.addComponents(comp);

        Channel chan = factory.createChannel();
        chan.setName("chan");
        model.addHubs(chan);

        Port port = factory.createPort();
        port.setName("output");
        comp.addRequired(port);

        MBinding binding = factory.createMBinding();
        binding.setHub(chan);
        binding.setPort(port);
        model.addMBindings(binding);

        node.delete();

        ModelSerializer save = new JSONModelSerializer();
        String modelStr = save.serialize(model);

        System.out.println(modelStr);

        assert(modelStr.contains("\"port\":[],\"hub\":[\"hubs[chan]\"]}"));

    }

}

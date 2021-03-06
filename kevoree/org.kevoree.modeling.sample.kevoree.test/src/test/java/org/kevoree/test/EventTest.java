package org.kevoree.test;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ComponentType;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.test.modelsync.ModelSync;

/**
 * Created by duke on 24/07/13.
 */
public class EventTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare compare = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    @Test
    public void testEvent1() {

        ContainerRoot modelM0 = factory.createContainerRoot();
        ContainerRoot modelM1 = factory.createContainerRoot();
        ModelSync sync = new ModelSync(modelM0, modelM1);

        //add node
        ComponentType typeDef = factory.createComponentType();
        typeDef.setName("TD1");
        modelM0.addTypeDefinitions(typeDef);

        assert (modelM1.getTypeDefinitions().size() == 1);
        assert (modelM0.getTypeDefinitions().size() == 1);
        assert (!modelM0.findTypeDefinitionsByID("TD1").equals(modelM1.findTypeDefinitionsByID("TD1")));

        ContainerNode newNode = factory.createContainerNode();
        newNode.setName("testNode");
        modelM0.addNodes(newNode);

        assert (modelM1.getNodes().size() == 1);
        assert (modelM0.getNodes().size() == 1);
        assert (!modelM0.findNodesByID("testNode").equals(modelM1.findNodesByID("testNode")));


        assert (modelM0.getTypeDefinitions().contains(typeDef));
        assert (!modelM1.findTypeDefinitionsByID("TD1").equals(modelM0.findTypeDefinitionsByID("TD1")));
        newNode.setTypeDefinition(typeDef);


        ContainerNode mirorNode = modelM1.findNodesByID("testNode");
        TypeDefinition M1mirrorTD = mirorNode.getTypeDefinition();

        assert (!M1mirrorTD.equals(typeDef));
        assert (M1mirrorTD.eContainer().equals(modelM1));

        //newNode.setName("newName");
        assert (mirorNode.getName().equals(newNode.getName()));

        newNode.setTypeDefinition(null);
        //modelM0.removeNodes(newNode);

    }

    @Test
    public void testEvent2() {

        ContainerRoot modelM0 = factory.createContainerRoot();

        ContainerNode newNode = factory.createContainerNode();
        newNode.setName("testNode");
        modelM0.addNodes(newNode);

        assert (modelM0.findNodesByID("testNode") != null);
        newNode.setName("newName");
        assert (modelM0.findNodesByID("newName") != null);
        assert (modelM0.findNodesByID("testNode") == null);

    }


}

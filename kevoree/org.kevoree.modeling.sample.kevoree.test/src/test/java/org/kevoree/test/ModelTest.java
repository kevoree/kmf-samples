package org.kevoree.test;

import jet.runtime.typeinfo.JetValueParameter;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.kevoree.*;
import org.kevoree.container.KMFContainerImpl;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.events.ModelElementListener;
import org.kevoree.modeling.api.events.ModelEvent;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

/**
 * Created by gregory.nain on 21/02/2014.
 */
public class ModelTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    private Semaphore sema = new Semaphore(0);


    @Test
    public void renameTest() {

        try {
            final ContainerRoot root = factory.createContainerRoot();
            Group gp = factory.createGroup();
            gp.setName("group");
            final ContainerNode node = factory.createContainerNode();
            node.setName("node0");

            root.addGroups(gp);
            root.addNodes(node);

            gp.addSubNodes(node);

            assertNotNull("Could not find node0 in first stage", gp.findSubNodesByID("node0"));

            root.addModelTreeListener(new ModelElementListener() {
                @NotNull
                @Override
                public void elementChanged(@JetValueParameter(name = "evt") @NotNull ModelEvent modelEvent) {
                if(root.findByPath(modelEvent.getPreviousPath()) == node && modelEvent.getElementAttributeName().equals("name")) {
                    sema.release();
                }
                }
            });
            node.setName("node1");
            sema.acquire();

            assertNotNull("Could not find renamed node0.", gp.findSubNodesByID("node1"));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void deleteOppositeTest() {

        final ContainerRoot root = factory.createContainerRoot();


        final Group gp = factory.createGroup();
        gp.setName("group");
        final ContainerNode node = factory.createContainerNode();
        node.setName("node0");

        root.addGroups(gp);
        root.addNodes(node);

        gp.addSubNodes(node);

        assertNotNull("Could not find node0 in first stage", gp.findSubNodesByID("node0"));

        node.delete();

        assertNull("Node0 is still referenced. ", gp.findSubNodesByID("node0"));

    }


    @Test
    public void deleteSingleRefTest() {

        final ContainerRoot root = factory.createContainerRoot();

        final ContainerNode node = factory.createContainerNode();
        node.setName("node0");
        root.addNodes(node);

        final ComponentType cType = factory.createComponentType();
        cType.setName("testCType");
        root.addTypeDefinitions(cType);

        final PortTypeRef ptr = factory.createPortTypeRef();
        ptr.setName("p0");
        cType.addProvided(ptr);

        final ComponentInstance inst = factory.createComponentInstance();
        inst.setName("cpInst");
        node.addComponents(inst);

        final Port port = factory.createPort();
        port.setName(ptr.getName());
        inst.addProvided(port);
        port.setPortTypeRef(ptr);


        assertNotNull("PortTypeRef not found", port.getPortTypeRef() == ptr);
        ptr.delete();
        assertNull("PortTypeRef still referenced in port. ", port.getPortTypeRef());
        assertNull("PortTypeRef still referenced in ComponentType. ", cType.findProvidedByID("p0"));

    }


    @Test
    public void avoidMemoryLeaksTest() {

        final ContainerRoot root = factory.createContainerRoot();

        final ContainerNode node = factory.createContainerNode();
        node.setName("node0");
        root.addNodes(node);

        final ComponentType cType = factory.createComponentType();
        cType.setName("testCType");
        root.addTypeDefinitions(cType);

        final PortTypeRef ptr = factory.createPortTypeRef();
        ptr.setName("p0");
        cType.addProvided(ptr);

        final ComponentInstance inst = factory.createComponentInstance();
        inst.setName("cpInst");
        node.addComponents(inst);

        final Port port = factory.createPort();
        port.setName(ptr.getName());
        inst.addProvided(port);
        port.setPortTypeRef(ptr);

        assertTrue("PortTypeRef does not contain an inbound ref for Port", ((KMFContainerImpl)ptr).getInternal_inboundReferences().containsKey(port));
        port.delete();
        assertFalse("PortTypeRef still contains an inbound ref for Port", ((KMFContainerImpl) ptr).getInternal_inboundReferences().containsKey(port));
    } 

}

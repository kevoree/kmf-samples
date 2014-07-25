package org.kevoree.test;

import junit.framework.Assert;
import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.ComponentType;
import org.kevoree.TypeDefinition;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.events.*;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.modeling.api.trace.Event2Trace;
import org.kevoree.modeling.api.trace.ModelTraceApplicator;
import org.kevoree.modeling.api.trace.TraceSequence;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 30/07/13
 * Time: 13:26
 */
public class TraceSyncTest {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare comparator = factory.createModelCompare();


    @Test
    public void testSyncViaTrace() {


        ContainerRoot modelM0 = factory.createContainerRoot();
        ComponentType typeDef = factory.createComponentType();
        typeDef.setName("TD1");
        modelM0.addTypeDefinitions(typeDef);

        ContainerNode newNode = factory.createContainerNode();
        newNode.setName("testNode");
        modelM0.addNodes(newNode);

        final ContainerRoot modelM1 = cloner.clone(modelM0);


        modelM0.addModelTreeListener(new ModelElementListener() {

            Event2Trace converter = new Event2Trace(comparator);
            ModelTraceApplicator applicator = new ModelTraceApplicator(modelM1, factory);

            @Override
            public void elementChanged(ModelEvent modelEvent) {
                TraceSequence traceSeq = converter.convert(modelEvent);

                TraceSequence traceSeqClone = new TraceSequence(factory);          /* Simulate network payload  */
                traceSeqClone.populateFromString(traceSeq.exportToString());
                Assert.assertEquals(traceSeq.exportToString(),traceSeqClone.exportToString());

                System.out.println(traceSeq.exportToString());

                applicator.applyTraceOnModel(traceSeqClone);
            }
        });


        ContainerNode newNode2 = factory.createContainerNode();
        newNode2.setName("newNode2");

        /* This test the creation of a new element */
        modelM0.addNodes(newNode2);

        assert (modelM1.findNodesByID("newNode2") != null);
        assert (modelM1.findNodesByID("newNode2") != newNode2);
        TypeDefinition remoteTD1M1 = modelM1.findTypeDefinitionsByID("TD1");

        newNode2.setTypeDefinition(typeDef); /* set un existing reference */
        assert (modelM1.findNodesByID("newNode2") != null);

        assert (modelM1.findTypeDefinitionsByID("TD1") != null);
        assert (modelM1.findTypeDefinitionsByID("TD1") == remoteTD1M1);    //check TD is not overriden
        assert (modelM1.findTypeDefinitionsByID("TD1") == modelM1.findNodesByID("newNode2").getTypeDefinition());

    }

}

package org.kevoree.test.modelsync;

import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelCloner;
import org.kevoree.modeling.api.compare.ModelCompare;
import org.kevoree.modeling.api.events.*;
import org.kevoree.modeling.api.json.JSONModelLoader;
import org.kevoree.modeling.api.json.JSONModelSerializer;
import org.kevoree.modeling.api.util.ElementAttributeType;

/**
 * Created by duke on 24/07/13.
 */
public class ModelSyncListener implements ModelElementListener {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private ModelCloner cloner = factory.createModelCloner();
    private ModelCompare compare = factory.createModelCompare();
    private JSONModelSerializer saver = factory.createJSONSerializer();
    private JSONModelLoader loader = factory.createJSONLoader();


    private KMFContainer currentModel = null;

    public ModelSyncListener(KMFContainer m0) {
        currentModel = m0;
    }

    @Override
    public void elementChanged(ModelEvent modelEvent) {

        System.out.println(modelEvent);

        try {
            //   saver.serialize(modelEvent.getValue(), System.out);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KMFContainer target = null;
        if (modelEvent.getPreviousPath().equals("")) {
            target = currentModel;
        } else {
            //DO find by path
            target = (KMFContainer) currentModel.findByPath(modelEvent.getPreviousPath());
        }
        //reflexive apply

        if (modelEvent.getElementAttributeType() == ElementAttributeType.CONTAINMENT) {
            target.reflexiveMutator(modelEvent.getEtype(), modelEvent.getElementAttributeName(), cloner.clone((KMFContainer)modelEvent.getValue()), false, true);
        } else {
            if (modelEvent.getValue() instanceof KMFContainer) {
                KMFContainer eventObj = (KMFContainer) modelEvent.getValue();
                String originPath = eventObj.path();
                target.reflexiveMutator(modelEvent.getEtype(), modelEvent.getElementAttributeName(), currentModel.findByPath(originPath), false, true);
            } else {
                target.reflexiveMutator(modelEvent.getEtype(), modelEvent.getElementAttributeName(), modelEvent.getValue(), false, true);
            }
        }

    }
}

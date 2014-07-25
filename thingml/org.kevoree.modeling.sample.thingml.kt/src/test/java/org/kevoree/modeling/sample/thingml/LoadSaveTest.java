package org.kevoree.modeling.sample.thingml;/*
* Author : Gregory Nain (developer.name@uni.lu)
* Date : 02/09/13
*/

import thingml.factory.DefaultThingmlFactory;
import thingml.factory.ThingmlFactory;
import org.junit.Test;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.ModelLoader;
import org.kevoree.modeling.api.ModelSerializer;
import org.kevoree.modeling.api.xmi.XMIModelSerializer;

import java.io.*;
import java.net.URISyntaxException;

public class LoadSaveTest {

    @Test
    public void loadSaveTest() {
        try {

            ThingmlFactory factory = new DefaultThingmlFactory();
            ModelLoader loader = factory.createXMILoader();
            ModelSerializer serializer = new XMIModelSerializer();

            KMFContainer container = loader.loadModelFromStream(getClass().getClassLoader().getResourceAsStream("D-CRM.xmi")).get(0);
            assert(container != null);

            System.out.println("Load done");

            File tempFile = File.createTempFile("kmfTest_" + System.currentTimeMillis(), "kev");
            System.out.println(tempFile.getAbsolutePath());
            FileOutputStream pr = new FileOutputStream(tempFile);

            serializer.serializeToStream(container,pr);
            pr.close();
            System.out.println("Model Saved to: " + tempFile.getAbsolutePath());


            KMFContainer container2 = loader.loadModelFromStream(new FileInputStream(tempFile)).get(0);
            assert(container2 != null);

            //tempFile.deleteOnExit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}

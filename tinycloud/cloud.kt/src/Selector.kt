/**
 * Created by duke on 7/21/14.
 */



import java.util.HashMap
import org.cloud.impl.DefaultCloudFactory
import org.cloud.serializer.JSONModelSerializer
import org.kevoree.modeling.api.KMFContainer
import java.util.ArrayList
import org.kevoree.modeling.api.util.ModelVisitor
import org.kevoree.modeling.api.util.ModelAttributeVisitor

fun main(args: Array<String>) {

    var factory = DefaultCloudFactory()
    var model = factory.createCloud();
    factory.root(model)
    var saver = factory.createJSONSerializer()
    model.addNodes(factory.createNode().withId("node0").withVersion("1.0"))
    model.addNodes(factory.createNode().withId("node1").withVersion("1.0"))

    model.nodes.get(0).addSoftwares(factory.createSoftware().withName("soft0"))
    model.nodes.get(1).addSoftwares(factory.createSoftware().withName("soft1"))

    println(model.select("nodes[id=*]"))
    println(model.select("/nodes[id=*]"))



    /*
    var factory = DefaultCloudFactory()
    var model = factory.createCloud();
    var saver = factory.createJSONSerializer()
    model.addNodes(factory.createNode().withId("node0").withVersion("1.0"))
    model.addNodes(factory.createNode().withId("node1").withVersion("1.0"))

    model.nodes.get(0).addSoftwares(factory.createSoftware().withName("soft0"))
    model.nodes.get(1).addSoftwares(factory.createSoftware().withName("soft1"))



    println(saver.serialize(model))

    // println(extractFirstQuery("no\\/des[node0,p=HelloJeSuis\\/Con\\,tent]/softwares[titi]"))



    println(model.select("nodes[id=*]"))
    println(model.select("nodes[]"))
    println(model.select("nodes[]"))

    println(model.select("nodes[]/softwares[]"))


    println(model.select("*[id=*]/softwares[]"))
    println(model.select("no*es[id=*]/softwares[]"))
    println(model.select("node*[id=*]/softwares[name=soft1]").last().internalGetKey())
    println(model.select("node*[id=*]/softwares[name=*1]").last().internalGetKey())
    println(model.select("node*[id=*]/softwares[name!=soft1]").last().internalGetKey())
    println(model.select("node*[id=*]/softwares[name!=*1]").last().internalGetKey())
    println(model.select("node*[id=*]/softwares[name!=soft*]"))
    println(model.select("node*[id=*]/softwares[name=soft*]"))

    println(model.nodes.get(1).path())
      */

    /*
    var factory = DefaultCloudFactory();
    var time = factory.time("0")
    var model = time.factory().createCloud();
    time.root(model);
    time.commit();

    println(time.lookup("/"))
      */
    /*
    model.addNodes(factory.createNode().withId("node0").withVersion("1.0"))
    model.addNodes(factory.createNode().withId("node1").withVersion("1.0"))
    model.nodes.get(0).addSoftwares(factory.createSoftware().withName("soft0"))
    model.nodes.get(1).addSoftwares(factory.createSoftware().withName("soft1"))
    */




}




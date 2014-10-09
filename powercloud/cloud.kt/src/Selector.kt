/**
 * Created by duke on 7/21/14.
 */



import org.kevoree.modeling.api.KMFContainer
import org.kevoree.modeling.api.util.ModelVisitor
import cloud.factory.CloudTransactionManager
import org.kevoree.modeling.api.persistence.MemoryDataStore
import cloud.Cloud

fun main(args: Array<String>) {

    var manager = CloudTransactionManager(MemoryDataStore())
    var transaction = manager.createTransaction()
    var model: Cloud = transaction.time(0).createCloud();
    transaction.time(0).root(model)

    model.addNodes(transaction.time(0).createNode().withId("node0").withVersion("1.0"))
    model.addNodes(transaction.time(0).createNode().withId("node1").withVersion("1.0"))

    model.nodes.get(0).addHosted(transaction.time(0).createNode().withId("child0").withVersion("1.0"))
    model.nodes.get(1).addHosted(transaction.time(0).createNode().withId("child1").withVersion("1.0"))


    model.nodes.get(0).addSoftwares(transaction.time(0).createSoftware().withName("soft0"))
    model.nodes.get(1).addSoftwares(transaction.time(0).createSoftware().withName("soft1"))

    println(model.select("nodes[id=*]"))
    println(model.select("/nodes[id=*]"))

    object visitor : ModelVisitor() {
        override fun visit(elem: KMFContainer, refNameInParent: String, parent: KMFContainer) {
            println(elem.path())
        }
    }



    println("NonDeep")
    model.visitContained(visitor)
    println("End NonDeep")


    println("NonContained")
    model.deepVisitContained(visitor)
    println("End NonContained")


    println("Full")
    model.deepVisitReferences(visitor)
    println("End Full")


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




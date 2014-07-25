
import org.junit.Test
import org.kevoree.modeling.api.persistence.MemoryDataStore
import kotlin.test.assertNotNull
import kmf.test.B
import kmf.test.A
import kmf.test.C
import kmf.test.Container
import kotlin.test.assertNull
import kmf.factory.DefaultKmfFactory

class ChangeContainerTest {

    val factory = DefaultKmfFactory()

    Test fun changeContainerTest() {

        factory.datastore = MemoryDataStore()
        var container = factory.createContainer()
        assert(container.path().equals(""))
        factory.root(container)
        assert(container.path().equals("/"))
        var b = factory.createB()
        container.addBees(b)
        var c = factory.createC()
        container.addCees(c)
        var a = factory.createA()
        b.addPlusList(a)

        var containerPath = container.path()!!
        var aPath = a.path()!!
        var bPath = b.path()!!
        val cPath = c.path()!!


        //INITIAL CHECK
        assert(b.eContainer() == container, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

        //PERSIST
        factory.commit();

        //RETRIEVE
        assertNotNull(factory.lookup(containerPath), "Container is null " + containerPath)
        container = factory.lookup(containerPath) as Container
        assertNotNull(factory.lookup(bPath), "B is null:" + bPath)
        b = factory.lookup(bPath)!! as B
        assertNotNull(factory.lookup(cPath), "C is null:" + cPath)
        c = factory.lookup(cPath)!! as C
        assertNotNull(factory.lookup(aPath), "A is null:" + aPath)
        a = factory.lookup(aPath)!! as A

        //RE-CHECK
        assert(b.eContainer() == container, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())


        //MOVE
        c.addBees(b)
        println("BPath:"+b.path())
        assert(b.eContainer() == c, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

        factory.commit();

        assertNull(factory.lookup(bPath), "B Path still exist after move")
        assertNull(factory.lookup(aPath), "A Path still exist after move")

        aPath = a.path()!!
        bPath = b.path()!!

        //PERSIST
        factory.commit();

        a = factory.lookup(aPath)!! as A
        assertNotNull(a, "A is null")
        b = factory.lookup(bPath)!! as B
        assertNotNull(b, "B is null")
        c = factory.lookup(cPath)!! as C
        assertNotNull(c, "C is null")

        assert(b.eContainer() == c, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

    }

}
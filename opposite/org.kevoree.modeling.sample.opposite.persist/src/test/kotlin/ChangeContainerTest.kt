
import org.junit.Test
import org.kevoree.modeling.api.persistence.MemoryDataStore
import kotlin.test.assertNotNull
import kmf.test.B
import kmf.test.A
import kmf.test.C
import kmf.test.Container
import kotlin.test.assertNull
import kmf.factory.DefaultKmfFactory
import kmf.factory.KmfTransactionManager

class ChangeContainerTest {

    val transactionManager = KmfTransactionManager(MemoryDataStore())

    Test fun changeContainerTest() {

        val transaction = transactionManager.createTransaction()

        var container = transaction.createContainer()
        assert(container.path().equals(""))
        transaction.root(container)
        assert(container.path().equals("/"))
        var b = transaction.createB()
        container.addBees(b)
        var c = transaction.createC()
        container.addCees(c)
        var a = transaction.createA()
        b.addPlusList(a)

        var containerPath = container.path()
        var aPath = a.path()
        var bPath = b.path()
        val cPath = c.path()


        //INITIAL CHECK
        assert(b.eContainer() == container, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

        //PERSIST
        transaction.commit();

        //RETRIEVE
        assertNotNull(transaction.lookup(containerPath), "Container is null " + containerPath)
        container = transaction.lookup(containerPath) as Container
        assertNotNull(transaction.lookup(bPath), "B is null:" + bPath)
        b = transaction.lookup(bPath)!! as B
        assertNotNull(transaction.lookup(cPath), "C is null:" + cPath)
        c = transaction.lookup(cPath)!! as C
        assertNotNull(transaction.lookup(aPath), "A is null:" + aPath)
        a = transaction.lookup(aPath)!! as A

        //RE-CHECK
        assert(b.eContainer() == container, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())


        //MOVE
        c.addBees(b)
        println("BPath:"+b.path())
        assert(b.eContainer() == c, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

        transaction.commit();

        assertNull(transaction.lookup(bPath), "B Path still exist after move")
        assertNull(transaction.lookup(aPath), "A Path still exist after move")

        aPath = a.path()
        bPath = b.path()

        //PERSIST
        transaction.commit();

        a = transaction.lookup(aPath)!! as A
        assertNotNull(a, "A is null")
        b = transaction.lookup(bPath)!! as B
        assertNotNull(b, "B is null")
        c = transaction.lookup(cPath)!! as C
        assertNotNull(c, "C is null")

        assert(b.eContainer() == c, "B container is: " + b.eContainer())
        assert(a.eContainer() == b, "A container is: " + a.eContainer())

    }

}
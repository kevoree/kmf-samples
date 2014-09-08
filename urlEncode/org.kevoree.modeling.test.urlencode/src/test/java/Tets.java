import kmf.Concept;
import kmf.RootConcept;
import metam.factory.MetaMTimeView;
import metam.factory.MetaMTransaction;
import metam.factory.MetaMTransactionManager;
import org.kevoree.modeling.api.persistence.MemoryDataStore;

/**
 * Created by gregory.nain on 08/09/2014.
 */
public class Tets {

    public static void main(String[] args) {
        MetaMTransactionManager tm = new MetaMTransactionManager(new MemoryDataStore());
        MetaMTransaction transaction = tm.createTransaction();
        MetaMTimeView time = transaction.time(0L);
        RootConcept rc = time.createRootConcept();
        time.root(rc);
        Concept c = time.createConcept();
        c.setMacle("http://www.kevoree.org:6464/mon/chemin");
        rc.addInnerConcepts(c);
        System.out.println("C Path: " + c.path());
        transaction.commit();
        transaction.close();

        MetaMTransaction transaction2 = tm.createTransaction();
        MetaMTimeView time2 = transaction2.time(0L);
        RootConcept c2 = (RootConcept)time2.lookup("/");
        System.out.println("AfterLookup: " + c2.getInnerConcepts().get(0).getMacle());



    }

}

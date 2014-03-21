package smartgrid.batch;

import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

import java.io.File;
import java.io.IOException;

/**
 * Created by duke on 3/19/14.
 */
public class BrutCreationFullTest {

    public static void main(String[] args) throws IOException {

        File dbdir = new File("tempdb");
        dbdir.mkdirs();
        DataStore datastore = new LevelDbDataStore(dbdir.getPath());




    }

}

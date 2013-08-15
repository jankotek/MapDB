package org.mapdb;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class Issue157Test {

    @Test
    public void concurrent_BTreeMap() throws InterruptedException {
        DBMaker dbMaker = DBMaker.newMemoryDB().cacheDisable().asyncWriteDisable();
        DB db = dbMaker.make();
        final BTreeMap<Integer, String> map = db.getTreeMap("COL_2");
        map.clear();

        Thread t1 = new Thread() {
            public void run() {
                for(int i=0; i<=10000; i++) {
                    map.put(i, "foo");
                }
            }
        };

        Thread t2 = new Thread() {
            public void run() {
                for(int i=10000; i>=0; i--) {
                    map.put(i, "bar");
                }
            }
        };

        t1.start();
        t2.start();

        t1.join();
        t2.join();

//        map.printTreeStructure();

        for(Map.Entry entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " => " + entry.getValue());
            assertTrue(""+entry.getKey(),"bar".equals(entry.getValue())||"foo".equals(entry.getValue()));
        }



    }
}

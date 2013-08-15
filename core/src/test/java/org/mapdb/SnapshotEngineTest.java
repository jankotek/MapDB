package org.mapdb;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class SnapshotEngineTest{

    SnapshotEngine e = new SnapshotEngine(new StoreWAL(Volume.memoryFactory(false, 0L)));

    @Test public void update(){
        long recid = e.put(111, Serializer.INTEGER);
        Engine snapshot = e.snapshot();
        e.update(recid, 222, Serializer.INTEGER);
        assertEquals(Integer.valueOf(111), snapshot.get(recid, Serializer.INTEGER));
    }

    @Test public void compareAndSwap(){
        long recid = e.put(111, Serializer.INTEGER);
        Engine snapshot = e.snapshot();
        e.compareAndSwap(recid, 111, 222, Serializer.INTEGER);
        assertEquals(Integer.valueOf(111), snapshot.get(recid, Serializer.INTEGER));
    }

    @Test public void delete(){
        long recid = e.put(111, Serializer.INTEGER);
        Engine snapshot = e.snapshot();
        e.delete(recid,Serializer.INTEGER);
        assertEquals(Integer.valueOf(111), snapshot.get(recid, Serializer.INTEGER));
    }

    @Test public void notExist(){
        Engine snapshot = e.snapshot();
        long recid = e.put(111, Serializer.INTEGER);
        assertNull(snapshot.get(recid, Serializer.INTEGER));
    }


    @Test public void create_snapshot(){
        Engine e = DBMaker.newMemoryDB().makeEngine();
        Engine snapshot = SnapshotEngine.createSnapshotFor(e);
        assertNotNull(snapshot);
    }

    @Test public void DB_snapshot(){
        DB db = DBMaker.newMemoryDB().asyncFlushDelay(100).transactionDisable().make();
        long recid = db.getEngine().put("aa",Serializer.STRING_NOSIZE);
        DB db2 = db.snapshot();
        assertEquals("aa", db2.getEngine().get(recid,Serializer.STRING_NOSIZE));
        db.getEngine().update(recid, "bb",Serializer.STRING_NOSIZE);
        assertEquals("aa", db2.getEngine().get(recid,Serializer.STRING_NOSIZE));
    }

    @Test public void DB_snapshot2(){
        DB db = DBMaker.newMemoryDB().transactionDisable().make();
        long recid = db.getEngine().put("aa",Serializer.STRING_NOSIZE);
        DB db2 = db.snapshot();
        assertEquals("aa", db2.getEngine().get(recid,Serializer.STRING_NOSIZE));
        db.getEngine().update(recid, "bb",Serializer.STRING_NOSIZE);
        assertEquals("aa", db2.getEngine().get(recid,Serializer.STRING_NOSIZE));
    }


    @Test public void BTreeMap_snapshot(){
        BTreeMap map =
                DBMaker.newMemoryDB().transactionDisable()
                .make().getTreeMap("aaa");
        map.put("aa","aa");
        Map map2 = map.snapshot();
        map.put("aa","bb");
        assertEquals("aa",map2.get("aa"));
    }

    @Test public void HTreeMap_snapshot(){
        HTreeMap map =
                DBMaker.newMemoryDB().transactionDisable()
                .make().getHashMap("aaa");
        map.put("aa","aa");
        Map map2 = map.snapshot();
        map.put("aa","bb");
        assertEquals("aa",map2.get("aa"));
    }

//    @Test public void test_stress(){
//        ExecutorService ex = Executors.newCachedThreadPool();
//
//        TxMaker tx = DBMaker.newMemoryDB().transactionDisable().makeTxMaker();
//
//        DB db = tx.makeTx();
//        final long recid =
//
//        final int threadNum = 32;
//        for(int i=0;i<threadNum;i++){
//            ex.execute(new Runnable() { @Override public void run() {
//
//            }});
//        }
//    }

}

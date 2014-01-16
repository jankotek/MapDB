package org.mapdb;


import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Issue247Test {

        private Map getMap(DB db){
                return db.createTreeMap("test")
                             .counterEnable()
                             .valuesOutsideNodesEnable()
                             .makeOrGet();
            }


    @Test
    public void test(){
        File f = UtilsTest.tempDbFile();
        DB db = DBMaker.newFileDB(f)
                .transactionDisable()
                .make();

        getMap(db);
        //db.commit();

        db.close();

        db = DBMaker.newFileDB(f)
                .readOnly()
                .make();
        getMap(db).size();
    }
}

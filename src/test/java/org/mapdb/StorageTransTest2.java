package org.mapdb;


public class StorageTransTest2 extends StorageDirectTest {
    


    @Override
	protected StorageTrans openEngine() {
        return new StorageTrans(fac);
    }



}

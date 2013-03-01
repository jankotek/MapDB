package org.mapdb;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * https://github.com/jankotek/MapDB/issues/69
 *
 * @author Konstantin Zadorozhny
 *
 */
public class Issue69Test {

	private DB db;

	@Before
	public void setUp() {
		db = DBMaker.newTempFileDB()
				.journalDisable()
				.checksumEnable()
				.deleteFilesAfterClose()
				.make();
	}

	@After
	public void tearDown() throws InterruptedException {
		db.close();
	}

	@Test
	public void testStackOverflowError() throws Exception {

		Map<String, String> map = db.getHashMap("test");

		StringBuilder buff = new StringBuilder();

		long maxIterations = 1000000;
		int valueLength = 1024;
		long maxKeys = 1000;
		long i = 1;
		while (i < maxIterations) {

			if (i % 10000 == 0) {
				valueLength ++;
				System.out.println("Iteration: " + i + "; Value length: " + valueLength);
			}

			String key = "key" + (int)(Math.random() * maxKeys);
			buff.setLength(valueLength);
			map.put(key, buff.toString());

			i++;

		}

	}


}

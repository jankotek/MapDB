package org.mapdb;

import org.junit.Test;

import java.io.IOException;
import java.util.*;
import static org.junit.Assert.*;

public class SerializerTest {

    @Test public void UUID2(){
        UUID u = UUID.randomUUID();
        assertEquals(u, SerializerBaseTest.clone2(u,Serializer.UUID));
    }

    @Test public void string_ascii(){
        String s = "adas9 asd9009asd";
        assertEquals(s,SerializerBaseTest.clone2(s,Serializer.STRING_ASCII));
        s = "";
        assertEquals(s, SerializerBaseTest.clone2(s,Serializer.STRING_ASCII));
        s = "    ";
        assertEquals(s, SerializerBaseTest.clone2(s,Serializer.STRING_ASCII));
    }

    @Test public void compression_wrapper() throws IOException {
        byte[] b = new byte[100];
        new Random().nextBytes(b);
        Serializer<byte[]> ser = new Serializer.CompressionWrapper(Serializer.BYTE_ARRAY);
        assertArrayEquals(b, SerializerBaseTest.clone2(b,ser));

        b = Arrays.copyOf(b, 10000);
        assertArrayEquals(b, SerializerBaseTest.clone2(b,ser));

        DataOutput2 out = new DataOutput2();
        ser.serialize(out,b);
        assertTrue(out.pos<1000);
    }
}

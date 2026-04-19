package it.leo.main.protocol.utils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SerializerUtilTest {

    @Test
    public void serializingDeserializingWorks() throws ClassNotFoundException, IOException {
        String test1 = "abc123";
        byte[] bytes1 = SerializerUtil.convertObjectToBytes(test1);
        String deserializedString1 = (String) SerializerUtil.convertBytesToObject(bytes1);
        assertEquals(test1, deserializedString1);

        Integer test2 = 1234;
        byte[] bytes2 = SerializerUtil.convertObjectToBytes(test2);
        Integer deserializedInteger2 = (Integer) SerializerUtil.convertBytesToObject(bytes2);
        assertEquals(test2, deserializedInteger2);
    }
}

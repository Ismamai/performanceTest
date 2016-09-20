package com.test.main;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by iblesa on 08/09/16.
 */
public class AddressesLoaderTest {
    @Test
    public void splitIntoBatches() throws Exception {
        List<String> strings = Arrays.asList("Address1", "Address_2", "Address_3"
                , "Address_4", "Address_5", "Address_6"
                , "Address_7", "Address_8", "Address_9",
                "Address_10");
        List<List<String>> lists = AddressesLoader.splitIntoBatches(3, strings);
        List<String> expectedFirstBatch = Arrays.asList("Address1", "Address_2", "Address_3");
        List<String> expectedLastBatch = Arrays.asList("Address_10");

        assertEquals("First batch is complete", expectedFirstBatch, lists.get(0));
        assertEquals("There are 4 batches", 4, lists.size());
        assertEquals("Last batch has only one element", 1, lists.get(3).size());
        assertEquals("Last batch has only one element", expectedLastBatch, lists.get(3));
    }

}
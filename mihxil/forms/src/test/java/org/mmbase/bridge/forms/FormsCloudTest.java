/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.forms;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.datatypes.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormsCloudTest.java 42130 2010-05-10 14:51:21Z michiel $
 */
public class FormsCloudTest  {

    private static int aaNode;
    @BeforeClass()
    public static void setUp() throws Exception {
        MockCloudContext cloudContext = MockCloudContext.getInstance();
        cloudContext.clear();
        cloudContext.addCore();
        Map<String, DataType> map = new HashMap<String, DataType>();
        map.put("number", Constants.DATATYPE_INTEGER);
        map.put("title", Constants.DATATYPE_STRING);
        cloudContext.addNodeManager("aa", map);
        Cloud cloud = cloudContext.getCloud("mmbase");
        Node aa = cloud.getNodeManager("aa").createNode();
        aa.commit();
        aaNode = aa.getNumber();
    }



    @Test
    public void wrappedForm() {
        assertTrue(aaNode > 0);
        CloudContext cc = ContextProvider.getCloudContext("forms:mock:local");
        Cloud cloud = cc.getCloud("test");
        assertEquals(2, cloud.getNodeManagers().size()); // every cloud has 'typedef'

        Node n = cloud.getNode(aaNode);
        assertNotNull(n);
        assertTrue(cloud.hasNode(aaNode));
        assertTrue(AAForm.AANode.class == n.getClass());
        assertEquals("myform", n.getNodeManager().getName());


    }

    @Test
    public void wrappedNode() {
        assertTrue(aaNode > 0);
        CloudContext cc = ContextProvider.getCloudContext("forms:mock:local");
        Cloud cloud = cc.getCloud("test2");
        assertEquals(3, cloud.getNodeManagers().size());


        Node n = cloud.getNode(aaNode);
        assertNotNull(n);
        assertTrue(cloud.hasNode(aaNode));
        assertTrue("" + n.getClass(), AANode.class == n.getClass());
        assertEquals("myform", n.getNodeManager().getName());
    }

    @Test
    public void transaction() {
        CloudContext cc = ContextProvider.getCloudContext("forms:mock:local");
        Cloud cloud = cc.getCloud("test2");
        Transaction trans = cloud.getTransaction("test123");

    }

}


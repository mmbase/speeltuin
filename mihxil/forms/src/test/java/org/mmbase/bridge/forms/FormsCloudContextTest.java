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
 * @version $Id: FormsCloudContextTest.java 39129 2009-10-13 15:33:06Z michiel $
 */
public class FormsCloudContextTest  {


    @Test
    public void cloudContext() {
        CloudContext cc = ContextProvider.getCloudContext("forms:mock://localhost");
        assertNotNull(cc);
        assertEquals(2, cc.getCloudNames().size());
        assertTrue(cc.getCloudNames().contains("test"));

        try {
            cc.getCloud("mmbase");
            fail("Should have thrown exception, because we configured only 'test' and 'test2'");
        } catch (NotFoundException nfe) {
            // ok
        }

        Cloud cloud = cc.getCloud("test");
        assertNotNull(cloud);
        assertTrue("" + cloud.getNodeManagers(), cloud.hasNodeManager("myform"));
    }

}


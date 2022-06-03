/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.portal;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * Clones an object, which all it's relations, and also adapts online and offline.
 * @author Michiel Meeuwissen
 * @version $Id: PageCloner.java 41541 2010-03-19 14:49:13Z michiel $
 */

public class PageCloner extends NodeFunction {

    public PageCloner() {
        super("clone", new Parameter[] {}, ReturnType.NODE);
    }

    protected Node  getFunctionValue(Node node, Parameters parameters) {
        Node newNode = CloneUtil.cloneNodeWithRelations(node);
        long duration = node.getDateValue("offline").getTime() - node.getDateValue("online").getTime();
        newNode.setDateValue("online", node.getDateValue("offline"));
        newNode.setDateValue("offline", new Date(node.getDateValue("offline").getTime() + duration));
        newNode.commit();
        return newNode;
    }
}


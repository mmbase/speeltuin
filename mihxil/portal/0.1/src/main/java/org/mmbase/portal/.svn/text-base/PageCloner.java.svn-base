package org.mmbase.portal;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * @author Michiel Meeuwissen
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


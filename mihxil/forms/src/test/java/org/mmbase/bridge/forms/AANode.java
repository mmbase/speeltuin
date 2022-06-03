/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.forms;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import java.util.*;

/**
 * A test/example form that deals with nodes of the type 'aa'.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AANode.java 42130 2010-05-10 14:51:21Z michiel $
 */
public class AANode extends FormNode {
    public AANode(Node n, Form f) throws NotWrapped {
        super(n, f);
        if (! n.getNodeManager().getName().equals("aa")) {
            throw new NotWrapped("Node " + n + " is not an aa but " + n.getNodeManager().getName());
        }
    }
    @Override
    public void commit() {
        // do something here
        super.commit();

    }
}


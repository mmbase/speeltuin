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
 * @version $Id: AAForm.java 42130 2010-05-10 14:51:21Z michiel $
 */
public class AAForm extends Form  {


    public AAForm(FormsCloud cloud, NodeManagerDescription desc) {
        super(cloud, desc);
    }

    @Override
    public Node getNode(int number) {
        if (vcloud.wrapped.hasNode(number)) {
            Node n = vcloud.wrapped.getNode(number);
            if (n.getNodeManager().getName().equals("aa")) {
                return new AANode(n);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public class AANode extends NodeWrapper {
        public AANode(Node n) {
            super(n);
        }
        @Override
        public NodeManager getNodeManager() {
            return AAForm.this;
        }
        @Override
        public void commit() {
            super.commit();
        }
    }

}


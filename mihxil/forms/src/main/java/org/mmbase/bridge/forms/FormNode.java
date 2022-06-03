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
 * @version $Id: FormNode.java 42130 2010-05-10 14:51:21Z michiel $
 */
public class FormNode extends NodeWrapper {

    private final Form form;
    public FormNode(Node n, Form form) throws NotWrapped {
        super(n);
        this.form = form;
    }

    @Override
    public Form getNodeManager() {
        return form;
    }

    @Override
    public void commit() {
        // do something here
        super.commit();
    }
}


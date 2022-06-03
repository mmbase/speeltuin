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
 * @version $Id: PageCloner.java 41536 2010-03-19 12:02:50Z michiel $
 */

public class Blocks extends NodeFunction {

    public static class Block {
        public int x;
        public int y;
        public int height;
        public int width;
        public Integer node;
    }

    public Blocks() {
        super("blocks", new Parameter[] {}, ReturnType.LIST);
    }

    protected List getFunctionValue(Node node, Parameters parameters) {
        NodeList blocks = node.getRelatedNodes("componentblocks");
        //TODO
        return blocks;
    }
}


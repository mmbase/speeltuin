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
 * Exceptions of this type can be thrown by the constructor of {@link FormNode} extension to
 * indicate to {@link NodeWrapperForm} that we don't want to wrap a given Node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NotWrapped.java 42130 2010-05-10 14:51:21Z michiel $
 */
public class NotWrapped extends Exception {


    public NotWrapped() {
    }

    public NotWrapped(String mes) {
        super(mes);
    }

}


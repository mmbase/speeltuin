/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.forms;

import static org.mmbase.datatypes.Constants.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.mock.MockField;
import org.mmbase.util.xml.*;
import java.util.*;
import org.mmbase.bridge.*;

/**
 * This NodeManager represents a generic form. See also {@link NodeWrapperForm}.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: Form.java 42130 2010-05-10 14:51:21Z michiel $
 */

public class Form extends AbstractNodeManager  {


    protected final NodeManagerDescription desc;
    protected final FormsCloud vcloud;

    public Form(FormsCloud cloud, NodeManagerDescription desc) {
        super(cloud);
        vcloud = cloud;
        this.desc = desc;
    }


    /**
     * {@link FormsCloud#getNode} will call this method for every of its Forms.
     * The first one not returning <code>null</code> will be used.
     *
     * This default implementation returns <code>null</code>, you may want to override this.

     */
    public Node getNode(int number) {
        return null;
    }


    @Override
    public String getName() {
        return desc.name;
    }

    @Override
    public Node createNode() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Map<String, String> getProperties() {
        return desc.properties;
    }

    @Override
    protected Map<String, Field> getFieldTypes() {
        return Collections.unmodifiableMap(desc.fields);
    }



    @Override
    public NodeManager getParent() throws NotFoundException {
        String parent = desc.reader != null ? desc.reader.getExtends() : null;
        if (parent != null && parent.length() == 0) parent = null;

        if (parent == null) {
            throw new NotFoundException();
        } else {
            return getCloud().getNodeManager(parent);
        }
    }

    @Override
    public boolean mayCreateNode() {
        return true;
    }


}

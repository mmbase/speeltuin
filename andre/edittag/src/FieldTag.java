/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The FieldTag can be used as a child of a 'NodeProvider' tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FieldTag.java 35493 2009-05-28 22:44:29Z michiel $
 */
public class FieldTag extends FieldReferrerTag implements FieldProvider, Writer {

    private static final Logger log = Logging.getLoggerInstance(FieldTag.class);

    protected Node   node;
    protected NodeProvider nodeProvider;
    protected Field  field;
    protected String fieldName;
    protected Attribute name = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    // NodeProvider Implementation
    /**
     * A fieldprovider also provides a node.
     */

    public Node getNodeVar() throws JspTagException {
        if (node == null) {
            nodeProvider = findNodeProvider();
            node = nodeProvider.getNodeVar();
        }
        if (node == null) {
            throw new JspTagException ("Did not find node in the parent node provider");
        }
        return node;
    }
    public void setModified() {
        nodeProvider.setModified();
    }



    public Field getFieldVar() {
        return field;
    }

    protected void setFieldVar(String n) throws JspTagException {
        if (n != null) {
            try {
                field = getNodeVar().getNodeManager().getField(n);
            } catch (NotFoundException e) {
                field = null;
            }
            fieldName = n;
            if (getReferid() != null) {
                throw new JspTagException ("Could not indicate both  'referid' and 'name' attribute");
            }
        } else {
            if (getReferid() == null) {
                field = getField(); // get from parent.
                getNodeVar();       // be sure to set the nodevar too, though.
                fieldName = field.getName();
            }
        }
    }
    protected void setFieldVar() throws JspTagException {
        setFieldVar((String) name.getValue(this));
    }

    /**
     * Does something with the generated output. This default
     * implementation does nothing, but extending classes could
     * override this function.
     *
     **/
    protected String convert (String s) throws JspTagException { // virtual
        return s;
    }


    /**
	 * Method to handle the EditTag if it is present around fields and their nodes.
	 * <br /><br />
	 * If the FieldTag is inside an EditTag then it wil register itself with the EditTag.
	 * The EditTag can provide access to an editor. Not only the field and its nodes will 
	 * be registered but also the query it originated from.
	 * 
     * @since MMBase-1.8
	 * @todo  EXPERIMENTAL
     */
     protected void handleEditTag() {
        // Andre is busy with this...
		
		Tag t = findAncestorWithClass(this, EditTag.class);
		if (t == null) {
	        if (log.isDebugEnabled()) log.debug("No EditTag as parent. We don't want to edit, i presume.");
		} else {
			EditTag et = (EditTag)t;
			Query query = null;
			try {
				query = nodeProvider.getGeneratingQuery();
			} catch (JspTagException jte) {
				log.error("JspTagException, no GeneratingQuery found : " + jte);
			}
			
			int nodenr = node.getIntValue("number");		// nodenr of this field to pass to EditTag
			if (nodenr < 0) {
				java.util.List steps = query.getSteps();
        		Step nodeStep = null;
        		if (query instanceof NodeQuery) {
            		nodeStep = ((NodeQuery) query).getNodeStep();
            	}
				for (int j = 0; j < steps.size(); j++) {
					Step step = (Step)steps.get(j);
					if (step.equals(nodeStep)) {
						nodenr = node.getIntValue("number");
					} else {
						String pref = step.getAlias();
						if (pref == null) pref = step.getTableName();
						nodenr = node.getIntValue(pref + ".number");                 
					}
				}
        	}
			
			if (fieldName == null) {
				log.debug("fieldName still null. Image tag? URL tag? Attachment?");
				if (this instanceof ImageTag) {
					log.debug("Image! fieldName = handle");
					fieldName = "handle";
				}
			}
			if (fieldName.indexOf(".") < 0) {	// No nodemanager
				fieldName = node.getNodeManager().getName() + "." + fieldName;
			}

			// Register field and its node with EditTag
			log.debug("EditTag register fieldName : " + fieldName + " with nodenr : " + nodenr );
			et.registerField(query, nodenr, fieldName);
		}
    }

    public int doStartTag() throws JspTagException {
        node = null;
        fieldName = (String) name.getValue(this);
        if ("number".equals(fieldName)) {
            if (nodeProvider instanceof org.mmbase.bridge.jsp.taglib.edit.CreateNodeTag) {
                // WHY can't it simply return the number it _will_ get?
                throw new JspTagException("It does not make sense to ask 'number' field on uncommited node");
            }
        }
        setFieldVar(fieldName); // set field and node
        if (log.isDebugEnabled()) {
            log.debug("Field.doStartTag(); '"  + fieldName + "'");
        }

        // found the node now. Now we can decide what must be shown:
        Object value;
        // now also 'node' is availabe;
        if (field == null) { // some function, or 'referid' was used.
            if (getReferid() != null) { // referid
                value = getObject(getReferid());
            } else {         // function
                value = getNodeVar().getValue(fieldName);
            }
        } else {        // a field was found!
            // if direct parent is a Formatter Tag, then communicate
            FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
            if (f != null && f.wantXML()) {
                if (log.isDebugEnabled()) log.debug("field " + field.getName() + " is in a formatter tag, creating objects Element. ");
                f.getGenerator().add(node, field); // add the field
                value = "";
            } else { // do the rest as well.

                // if a value is really null, should it be past as null or cast?
                // I am leaning to the latter but it would break backward compatibility.
                // currently implemented this behavior for DateTime values (new fieldtype)
                // Maybe better is an attribute on fieldtag that determines this?
                // I.e. ifempty = "skip|asis|default"
                // where:
                //   skip: skips the field tag
                //   asis: returns null as a value
                //   default: returns a default value

                switch(helper.getVartype()) {
                case WriterHelper.TYPE_NODE:
                    value = node.getNodeValue(fieldName);
                    break;
                case WriterHelper.TYPE_FIELDVALUE:
                    value = node.getFieldValue(fieldName);
                    break;
                case WriterHelper.TYPE_FIELD:
                    value = node.getFieldValue(fieldName).getField();
                    break;
                default:
                    switch(field.getType()) {
                    case Field.TYPE_BYTE:
                        value = node.getByteValue(fieldName);
                        break;
                    case Field.TYPE_INTEGER:
                    case Field.TYPE_NODE:
                        value = new Integer(node.getIntValue(fieldName));
                        break;
                    case Field.TYPE_DOUBLE:
                        value = new Double(node.getDoubleValue(fieldName));
                        break;
                    case Field.TYPE_LONG:
                        value = new Long(node.getLongValue(fieldName));
                        break;
                    case Field.TYPE_FLOAT:
                        value = new Float(node.getFloatValue(fieldName));
                        break;
                    case Field.TYPE_DATETIME:
                        value = node.getValue(fieldName);
                        if (value != null) {
                            value = node.getDateValue(fieldName);
                        }
                        break;
                    case Field.TYPE_BOOLEAN:
                        value = Boolean.valueOf(node.getBooleanValue(fieldName));
                        break;
                    case Field.TYPE_LIST:
                        value = node.getListValue(fieldName);
                        break;
                    default:
                        value = convert(node.getStringValue(fieldName));
                    }
                }
            }
        }
        if (log.isDebugEnabled()) log.debug("value of " + fieldName + ": " + value);


        handleEditTag();

        helper.setValue(value);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        log.debug("end of doStartTag");
        return EVAL_BODY_BUFFERED;
    }


    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     * write the value of the field.
     **/
    public int doEndTag() throws JspTagException {
        log.debug("doEndTag van FieldTag");
        if ((! "".equals(helper.getString()) && getReferid() != null)) {
            throw new JspTagException("Cannot use body in reused field (only the value of the field was stored, because a real 'field' object does not exist in MMBase)");
        }
        node = null;
        nodeProvider = null;
        field = null;
        fieldName = null;
        helper.doEndTag();
        return super.doEndTag();
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import javax.xml.stream.*;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;


/**
 * This abstract implementation of WebService is based on an MMBase {@link Function}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractFunctionWebService.java 44704 2011-01-10 14:36:15Z michiel $
 */
public abstract class AbstractFunctionWebService extends AbstractWebService {

    protected abstract Function getFunction();

    @Override
    public void serve(final XMLStreamWriter writer, final Parameters params) throws javax.xml.stream.XMLStreamException {
        Function f = getFunction();
        FunctionRenderer.writeFunctionValue(writer, f.getName(), f.getFunctionValue(params));
    }

    @Override
    public Parameter[] getParameterDefinition() {
        return getFunction().getParameterDefinition();
    }

}

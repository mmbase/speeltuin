/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import javax.xml.stream.*;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Straight forward renderer wich renders the value of an MMBase {@link Function} to XML.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionRenderer.java 44704 2011-01-10 14:36:15Z michiel $
 * @todo EXPERIMENTAL
 * @since MMBase-1.9.2
 */
public class FunctionRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(FunctionRenderer.class);

    private static final String NS = "http://www.mmbase.org/webservice";

    private String set;
    private String name;


    public FunctionRenderer(Type t, Block parent) {
        super(t, parent);
    }

    public void setSet(String s) {
        set = s;
        if (name != null) {
            getFunction();
        }
    }

    public void setName(String n) {
        name = n;
        if (set != null) {
            getFunction();
        }
    }

    protected Function getFunction() {
        Function fun = FunctionFactory.getFunction(set, name);
        if (fun == null) {
            throw new IllegalArgumentException("No function '" + name + "' in function set '" + set + "'");
        }
        return fun;
    }

    @Override
    public Parameter<?>[] getParameters() {
        return getFunction().getParameterDefinition();
    }

    public static void writeFunctionValue(XMLStreamWriter writer, String name, Object o) throws XMLStreamException {
        //writer.writeStartDocument();
        writer.setPrefix("m", NS);
        writer.setDefaultNamespace(NS);
        writer.writeStartElement(NS,"functionValue");
        writer.writeAttribute("name", name);
        writer.writeNamespace("m", NS);
        writer.writeDefaultNamespace(NS);
        writer.writeCharacters("" + o);
        writer.writeEndElement();
    }

    @Override
    public void render(Parameters blockParameters, Writer w, RenderHints hints) throws FrameworkException {

        Function function = getFunction();
        try {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = output.createXMLStreamWriter(w);
            writeFunctionValue(writer, name, function.getFunctionValue(blockParameters));
            writer.flush();
        } catch (XMLStreamException e) {
            throw new FrameworkException(e);
        }
    }

    @Override
    public String toString() {
        if (set != null && name != null) {
            return "FN " + getFunction();
        } else {
            return "FN " + set + ":" + name;
        }

    }



}

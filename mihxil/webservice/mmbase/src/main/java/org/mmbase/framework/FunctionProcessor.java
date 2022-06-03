/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionProcessor.java 44704 2011-01-10 14:36:15Z michiel $
 * @todo EXPERIMENTAL
 * @since MMBase-1.9.2
 */
public class FunctionProcessor extends AbstractProcessor {
    private static final Logger log = Logging.getLoggerInstance(FunctionProcessor.class);

    private String set;
    private String name;


    public FunctionProcessor(Block parent) {
        super(parent);
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

    public Parameter<?>[] getParameters() {
        return getFunction().getParameterDefinition();
    }

    public void process(Parameters blockParameters) throws FrameworkException {
        Function function = getFunction();
        function.getFunctionValue(blockParameters);
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

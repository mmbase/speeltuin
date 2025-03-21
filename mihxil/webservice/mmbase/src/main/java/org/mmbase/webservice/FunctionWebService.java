/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import java.util.*;
import org.mmbase.util.functions.*;


/**
 * This implementation of WebService simply wraps a value of an MMBase function (for now only of a 'set').
 *
 * @author Michiel Meeuwissen
 * @version $Id: FunctionWebService.java 44704 2011-01-10 14:36:15Z michiel $
 */
public class  FunctionWebService extends AbstractFunctionWebService {

    private String set = null;
    private String name = null;

    public void setSet(String s) {
        set = s;
    }

    public void setName(String n) {
        name = n;
    }

    @Override
    protected Function getFunction() {
        Function fun = FunctionFactory.getFunction(set, name);
        if (fun == null) {
            throw new IllegalArgumentException("No function '" + name + "' in function set '" + set + "'");
        }
        return fun;
    }


    @Override
    public String toString() {
        return set + ":" + name + Arrays.asList(getParameterDefinition());
    }

}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import org.mmbase.util.functions.*;

/**
 * The main use of this WebService implementation is that it is implicetely used by the
 * configuration of {@link WebServiceRepository} to wrap anything that is not a {@link WebService}
 * itself, first in a {@link Function}, and then in this to make it a WebService.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AnyFunctionWebService.java 44704 2011-01-10 14:36:15Z michiel $
 */
public class  AnyFunctionWebService extends AbstractFunctionWebService {

    private final Function fun;

    public AnyFunctionWebService(Function f) {
        fun = f;
    }

    @Override
    protected Function getFunction() {
        return fun;
    }

}

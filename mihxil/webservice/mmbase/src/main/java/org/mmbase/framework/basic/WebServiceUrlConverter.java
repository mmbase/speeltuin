/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework.basic;
import org.mmbase.framework.*;
import java.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: WebServiceUrlConverter.java 37315 2009-07-28 17:00:50Z michiel $
 * @todo EXPERIMENTAL
 * @since MMBase-1.9.2
 */
public class WebServiceUrlConverter extends DirectoryUrlConverter {
    private final static long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(WebServiceUrlConverter.class);

    protected String renderJsp = "/mmbase/webservice.jspx";

    public WebServiceUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("/ws/");
        setDomain("localhost");
    }

    protected void getNiceDirectoryUrl(StringBuilder b, Block block,
                                         Parameters parameters,
                                                Parameters frameworkParameters,  boolean action) throws FrameworkException {
        throw new UnsupportedOperationException("Not yet ready");
    }

    protected Url getFilteredInternalDirectoryUrl(List<String> path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
        return Url.NOT;
    }


}

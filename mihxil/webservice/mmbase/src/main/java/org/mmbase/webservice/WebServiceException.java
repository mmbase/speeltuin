/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: WebServiceException.java 37674 2009-08-11 10:07:59Z michiel $
 */
public class WebServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    private int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    public WebServiceException() {
        super();
    }

    public WebServiceException(int status) {
        super();
        this.status = status;
    }



    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    public WebServiceException(Throwable cause) {
        super(cause);
    }
    public WebServiceException(int status, Throwable cause) {
        super(cause);
        this.status = status;
    }


    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}

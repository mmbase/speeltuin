/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.webservice;

import org.mmbase.servlet.*;
import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.xml.stream.*;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.CloudThreadLocal;
import org.mmbase.security.*;




/**

 * @version $Id: WebServiceServlet.java 44890 2011-01-14 18:15:01Z michiel $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9.2
 */
public class WebServiceServlet extends MMBaseServlet {

    private static final Logger log = Logging.getLoggerInstance(WebServiceServlet.class);

    protected String authenticate = "name/password";

    protected String defaultService = "list";
    protected String defaultExtension = ".xml";

    @Override
    protected Map<String, Integer> getAssociations() {
        Map<String, Integer> a = super.getAssociations();
        a.put("webservices",      100);
        return a;
    }


    @Override
    public void init() throws ServletException {
        super.init();
        String df = getInitParameter("defaultService");
        if (df != null && df.length() > 0) {
            defaultService = df;
        }

        String de = getInitParameter("defaultExtension");
        if (de != null && de.length() > 0) {
            defaultExtension = de;
        }
    }

    protected Logger getClientLogger(int status) {
        return Logging.getLoggerInstance(WebServiceServlet.class.getName() + ".CLIENT." + status);
    }

    private Cloud anonymous;

    protected Cloud getAnonymousCloud() {
        if (anonymous == null || ! anonymous.getUser().isValid()) {
            anonymous = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "anonymous", null);
        }
        return anonymous;
    }

    protected String getRealm(HttpServletRequest req) {
        String contextPath = req.getContextPath().replace('/', '_');
        return "MMBase" + contextPath + "@" + req.getServerName();
    }

    protected void fillStandardParameters(Parameters p, HttpServletRequest req, HttpServletResponse res, Cloud cloud)  {
        log.debug("Filling standard parameters");
        p.setIfDefined(Parameter.RESPONSE, res);
        p.setIfDefined(Parameter.REQUEST,  req);
        // locale parameters
        java.util.Locale locale = req.getLocale();
        if (locale != null) {
            p.setIfDefined(Parameter.LANGUAGE, locale.getLanguage());
            p.setIfDefined(Parameter.LOCALE, locale);
        }
        if (cloud != null) {
            p.setIfDefined(Parameter.CLOUD, cloud);
            p.setIfDefined(Parameter.USER, cloud.getUser());
        }
    }

    protected Cloud getCloud(WebService ws, HttpServletRequest req, HttpServletResponse res) throws IOException {
        Cloud cloud;
        Action action = ws.getAction();
        if (action == null) {
            throw new RuntimeException("No action defined for " + ws);
        }
        cloud = getAnonymousCloud();

        String mime_line = req.getHeader("Authorization");
        if (log.isDebugEnabled()) {
            log.debug("authent: " + req.getHeader("WWW-Authenticate") + " realm: " + getRealm(req) + " authorization " + mime_line);
        }
        if (mime_line != null) {
            // find logon, password with http authentication
            String userName = null;
            String password = null;
            String user_password = org.mmbase.util.Encode.decode("BASE64", mime_line.substring(6));
            StringTokenizer t = new StringTokenizer(user_password, ":");
            if (t.countTokens() == 2) {
                userName = t.nextToken();
                password = t.nextToken();
            }
            Map<String, Object> creds = new HashMap<String, Object>();
            creds.put("username", userName);
            creds.put("password", password);
            try {
                cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", authenticate, creds);
            } catch (org.mmbase.security.SecurityException se) {
                getClientLogger(HttpServletResponse.SC_UNAUTHORIZED).service("Unauthorized " + se.getMessage());
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("WWW-Authenticate", "Basic realm=\" " + getRealm(req) + "\"");
                return null;
            }
        }


        Authorization auth = org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getAuthorization();
        Parameters aParameters = action.createParameters();
        fillStandardParameters(aParameters, req, res, cloud);

        if (! auth.check(cloud.getUser(), action, aParameters)) {
            getClientLogger(HttpServletResponse.SC_UNAUTHORIZED).service("Unauthorized (Action " + action + " forbode it)");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setHeader("WWW-Authenticate", "Basic realm=\"" + getRealm(req) + "\"");
            return null;
        }
        res.setHeader("X-MMBase-User", cloud.getUser().getIdentifier());
        return cloud;
    }



    String[]  getMimetype(final HttpServletRequest req) {
        return null;
    }

    protected void doWebService(final HttpServletRequest req, final HttpServletResponse res, final WebService.Type defaultType) throws ServletException, IOException {
        WebService.Type type;
        String file;
        {
            String[] path = req.getPathInfo().split("/");
            if (path.length > 1) {
                type = path.length > 2 ? WebService.Type.valueOf(path[1]) : defaultType;
                file = path.length > 2 ? path[2] : path[1];
            } else {
                type = WebService.Type.GET;
                file = defaultService;
            }
            if (file.lastIndexOf(".") < 0) {
                //String acceptHeader = req.getHeader("Accept");
                //boolean acceptable = acceptHeader == null ? true : acceptHeader.indexOf(type) != -1;
                file = file + defaultExtension;
            }
        }

        String wantedMimeType = getServletContext().getMimeType(file);
        if (wantedMimeType == null) {
            getClientLogger(HttpServletResponse.SC_NOT_FOUND).service("No mimetype found for '" + file + "'");
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No mimetype found for '" + file + "'");
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Desired mime type " + wantedMimeType + " for requests " + req.getClass() + " " + req + " response: " + res.getClass() + " " + res);
        }

        //req.setAttribute(org.mmbase.framework.JspRenderer.NO_RESPONSE_WRAP, Boolean.TRUE);

        String[] fileParts = file.split("\\.");

        WebService ws = WebServiceRepository.getInstance().getWebService(fileParts[0], type);
        if (ws  == null) {
            getClientLogger(HttpServletResponse.SC_NOT_FOUND).service("No webservice '" + fileParts[0] + "' found for " + type);
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No webservice '" + fileParts[0] + "' found for " + type);
            return;
        }



        Cloud cloud = getCloud(ws, req, res);
        if (cloud == null) {
            return;
        }
        CloudThreadLocal.bind(cloud);
        try {
            res.setContentType(wantedMimeType);
            Parameters params = ws.createParameters();
            params.setAutoCasting(true);


            fillStandardParameters(params, req, res, cloud);


            StringBuilder illegalArguments = new StringBuilder();
            for(Map.Entry<String, Object> entry : ((Map<String, Object>)req.getParameterMap()).entrySet()) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting " + entry + " (" + org.mmbase.util.Casting.toString(entry.getValue()) + ")");
                }
                try {
                    params.set(entry.getKey(), entry.getValue());
                } catch (IllegalArgumentException iae) {
                    log.debug(iae.getMessage(), iae);
                    if (illegalArguments.length() > 0) {
                        illegalArguments.append(", ");
                    }
                    illegalArguments.append(entry.getKey()).append(": ").append(iae.getMessage());
                }
            }
            if (illegalArguments.length() > 0) {
                getClientLogger(HttpServletResponse.SC_BAD_REQUEST).service(illegalArguments.toString());
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, illegalArguments.toString());
                return;

            }

            if (log.isDebugEnabled()) {
                log.debug("Using  " + ws + params);
            }
            Collection<LocalizedString> errors = params.validate();
            if (errors.size() > 0) {
                getClientLogger(HttpServletResponse.SC_BAD_REQUEST).service(LocalizedString.toStrings(errors, req.getLocale()).toString());
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, LocalizedString.toStrings(errors, req.getLocale()).toString());
                return;
            }
            try {

                XMLOutputFactory output = XMLOutputFactory.newInstance();
                if (wantedMimeType.equals("application/xml")) {
                    XMLStreamWriter writer = output.createXMLStreamWriter(res.getOutputStream());
                    writer.writeStartDocument();
                    ws.serve(writer, params);
                    writer.close();
                } else {
                    XsltTransformer xslt = new XsltTransformer();
                    if (("application/json".equals(wantedMimeType) ||
                         "text/plain".equals(wantedMimeType))) {
                        xslt.setXslt("xslt/xml2json.xslt");
                    } else if (("text/html".equals(wantedMimeType) ||
                                "application/xhtml+xml".equals(wantedMimeType))) {
                        xslt.setXslt("xslt/xml2xhtml.xslt");
                    } else {
                        getClientLogger(HttpServletResponse.SC_NOT_IMPLEMENTED).service("Cannot transform to " + wantedMimeType);
                        res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Cannot transform to " + wantedMimeType);
                        return;
                    }

                    TransformingOutputStream pipe = new TransformingOutputStream(res.getOutputStream(), xslt);
                    //XMLStreamWriter writer = new StAXStreamWriter(pipe);
                    XMLStreamWriter writer = output.createXMLStreamWriter(pipe);
                    writer.writeStartDocument();
                    ws.serve(writer, params);
                    writer.flush();
                    pipe.close();
                    if (pipe.getException() != null) {
                        log.info("Found exception " + pipe.getException());
                        writer.writeStartElement("error");
                        writer.writeCharacters(pipe.getException().getMessage());
                        writer.writeEndElement();
                    }
                    writer.close();

                }
                getClientLogger(HttpServletResponse.SC_OK).service(ws.toString());

            } catch (WebServiceException we) {
                getClientLogger(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).warn(we.getMessage());
                if (we.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                    throw new ServletException(we);
                } else {
                    res.sendError(we.getStatus(), we.getMessage());
                    return;
                }
            } catch (Exception fe) {
                getClientLogger(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).warn(fe.getMessage());
                throw new ServletException(ws.toString() + ":" + fe.getClass() + ":" + fe.getMessage(), fe);
            }
        } finally {
            CloudThreadLocal.unbind();
        }
    }

    /**
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doWebService(req, res, WebService.Type.GET);
    }
    /**
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doWebService(req, res, WebService.Type.POST);
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doWebService(req, res, WebService.Type.PUT);
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doWebService(req, res, WebService.Type.DELETE);
    }

}

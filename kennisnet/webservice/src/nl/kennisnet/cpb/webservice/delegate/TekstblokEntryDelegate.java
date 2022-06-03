/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.jdom.Element;

import nl.kennisnet.cpb.webservice.RestWebserviceServlet;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * A webservice-delegate for handling the write request to create/update a
 * tekstblok (stored in CE artikel) Input: an Atom-entry document containing the
 * necessary field Output: the read-representation (Atom-entry)of the
 * created/update resource (equivalent to output of ContentEntryDelegate)
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class TekstblokEntryDelegate extends AbstractRestDelegate {

   public final static AbstractRestDelegate INSTANCE = new TekstblokEntryDelegate();

   private static Logger log = Logging
         .getLoggerInstance(TekstblokEntryDelegate.class.getName());

   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {

      Element ce = WebserviceHelper.retrieveJDOMFromRequest(req);
      Node n = dao.createCENode();

      // Set fields that are mandatory at creation
      n.setStringValue("elementtype", "Artikel");

      NodeWrapper nw = new NodeWrapper(n, Collections.EMPTY_LIST,
            Collections.EMPTY_LIST, Collections.EMPTY_LIST,Collections.EMPTY_LIST,Collections.EMPTY_LIST, null);
      ceService.updateTekstblok(nw, ce);

      ceService.assignNodeToRedactiegroep(n, ce);

      /*String contentXML = translationService.createContentXML(nw,
            WebserviceCommunicatorDAO.FIELDS_IN_CONTENT_XML, WebserviceHelper
                  .createCopaBaseURL(req));
      translationService.outputAtomFeed(n, contentXML, req, res);*/
      res.setHeader("Location", WebserviceHelper.createCopaBaseURL(req)
            + RestWebserviceServlet.WEBSERVICE_URL + "content/"
            + n.getStringValue("number"));
      res.setStatus(HttpURLConnection.HTTP_CREATED);
   }

   @Override
   public void doPut(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {

      // TODO security

      Integer tekstblokId = (Integer) req
            .getAttribute(AbstractRestDelegate.CONTENT_ID_ATTR);
      if (tekstblokId == null || "".equals(tekstblokId)) {
         throw new BadRequestException("Missing tekstblok id in url");
      }
      Element ce = WebserviceHelper.retrieveJDOMFromRequest(req);

      Node n = dao.getNodeById(tekstblokId);
      // TODO retrieve nodewrapper with all links/attachments/image in case the
      // webservice needs to be able to edit the individual links. This is
      // currently not a requirement

      NodeWrapper nw = new NodeWrapper(n, Collections.EMPTY_LIST,
            Collections.EMPTY_LIST, Collections.EMPTY_LIST,Collections.EMPTY_LIST,Collections.EMPTY_LIST,null);
      ceService.updateTekstblok(nw, ce);
      //TODO we should also incorporate the redactiegroep in the response, but this is currently not necessary for Vakcommunities
      translationService.outputAtomEntry(nw,req, res);
      res.setStatus(HttpURLConnection.HTTP_OK);
   }
}

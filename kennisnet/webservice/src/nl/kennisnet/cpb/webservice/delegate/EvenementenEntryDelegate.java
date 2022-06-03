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
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * A webservice-delegate for handling the write request to create/update/delete
 * an evenement. Input: an Atom-entry document containing the necessary field
 * Output: the read-representation (Atom-entry)of the created/update resource
 * (equivalent to output of ContentEntryDelegate)
 *
 * @author dekok01
 * @version $Revision: 1.0
 */
public class EvenementenEntryDelegate extends AbstractRestDelegate {

   public final static AbstractRestDelegate INSTANCE = new EvenementenEntryDelegate();

   private static Logger log = Logging
         .getLoggerInstance(EvenementenEntryDelegate.class.getName());

   private EvenementenEntryDelegate() {
   }

   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {

      EventWrapper ew = translationService.parseEvent(req);
      System.out.println("IN EVENEMENTEN found event: " + ew);

      Integer agendaId = (Integer) req.getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);   
      ew.setParentId("" + agendaId);
      ceService.save(ew);
      res.setStatus(HttpURLConnection.HTTP_CREATED);
  
      ew.setId(WebserviceHelper.createCopaBaseURL(req)
            + RestWebserviceServlet.WEBSERVICE_URL + "agenda/" + agendaId + "/evenement/" 
            + ew.getNumber());
      placeEvenementInAgenda(ew, req); 
      res.setHeader("Location", WebserviceHelper.createCopaBaseURL(req)
            + RestWebserviceServlet.WEBSERVICE_URL + "agenda/" + agendaId + "/evenement/" 
            + ew.getNumber());
      translationService.outputAtomEntry(ew, req, res);
   }

   /**
    * Convenience function for placing an evenement in an agenda. If the url has
    * an agenda id, we place the evenement in the agenda (unless it already is
    * part of that agenda).
    *
    * @param n
    *           the evenement
    * @param ce
    *           the input xml
    * @param req
    *           the request
    * @throws WebserviceException
    *            in case of missing input
    */
   private void placeEvenementInAgenda(EventWrapper ew,
         HttpServletRequest req) throws WebserviceException {
      Integer agendaId = (Integer) req
            .getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      String endDate = "" + ew.getEindDatum();
      if (agendaId != null) {
         if (endDate != null) {
            try {

               System.out.println("ADDING_EVENT_TO_AGENDA");
               ceService.assignNodeToAgenda(ew.getNode(), Integer.parseInt(endDate),
                     agendaId);
            } catch (NumberFormatException nfe) {
               throw new BadRequestException(
                     "Field datum2 should be set to a valid integer (number of seconds) when placing an evenement in an agenda");
            }
         } else {
            throw new BadRequestException(
                  "Field datum2 should be set when placing an evenement in an agenda");
         }
      }
   }
}

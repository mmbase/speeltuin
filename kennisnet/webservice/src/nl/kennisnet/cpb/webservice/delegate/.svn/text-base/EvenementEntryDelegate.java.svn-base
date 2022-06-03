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
public class EvenementEntryDelegate extends AbstractRestDelegate {

   public final static AbstractRestDelegate INSTANCE = new EvenementEntryDelegate();

   private static Logger log = Logging
         .getLoggerInstance(EvenementEntryDelegate.class.getName());

   private EvenementEntryDelegate() {
   }

   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {

      EventWrapper ew = translationService.parseEvent(req);
      System.out.println("found event: " + ew);

      Integer agendaId = (Integer) req.getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      Integer eventId = (Integer) req.getAttribute(AbstractRestDelegate.EVENEMENT_ID_ATTR);
      if (eventId != null) {
         ew.setParentId("" + agendaId);
         ceService.updateEvent(eventId, ew);
         res.setStatus(HttpURLConnection.HTTP_OK);
      } else {
         ew.setParentId("" + agendaId);
         ceService.save(ew);
         res.setStatus(HttpURLConnection.HTTP_CREATED);
      }
      ew.setId(WebserviceHelper.createCopaBaseURL(req)
            + RestWebserviceServlet.WEBSERVICE_URL + "agenda/" + agendaId + "/evenement/" 
            + ew.getNumber());
      
      res.setHeader("Location", WebserviceHelper.createCopaBaseURL(req)
            + RestWebserviceServlet.WEBSERVICE_URL + "agenda/" + agendaId + "/evenement/" 
            + ew.getNumber());
      translationService.outputAtomEntry(ew, req, res);
   }

/*
   AgendaWrapper kw = translationService.parseAgenda(req);
   Integer agendaId = (Integer) req.getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
   if (agendaId != null) {
      ceService.updateAgenda(agendaId, kw);
      res.setStatus(HttpURLConnection.HTTP_OK);
   } else {
      ceService.save(kw);
      res.setStatus(HttpURLConnection.HTTP_CREATED);
   }
   res.setHeader("Location", WebserviceHelper.createCopaBaseURL(req)
         + RestWebserviceServlet.WEBSERVICE_URL + "content/"
         + kw.getNumber());
   translationService.outputAtomFeed(kw, req, res);
   */

   @Override
   public void doPut(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      doPost(req, res);
   }
   
   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {

      Integer agendaId = (Integer)
         req.getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      Integer eventId = (Integer)
         req.getAttribute(AbstractRestDelegate.EVENEMENT_ID_ATTR);
      EventWrapper aw = dao.getEventWrapperById(eventId);
      translationService.outputAtomEntry(aw,req, res);
   }
   
   @Override
   public void doDelete(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      Integer evenementId = (Integer) req
            .getAttribute(AbstractRestDelegate.EVENEMENT_ID_ATTR);

      if (evenementId == null || "".equals(evenementId)) {
         throw new BadRequestException("Missing evenement id in url");
      }

      EventWrapper ew = dao.getEventWrapperById(evenementId);
      removeEvenementFromAgenda(ew, req);
      res.setStatus(HttpURLConnection.HTTP_NO_CONTENT);
   }


   /**
    * Convenience function for removing an evenement in an agenda
    * 
    * @param n
    *           the evenement
    * @param req
    *           the request
    * @throws WebserviceException
    *            in case of missing input
    */
   private void removeEvenementFromAgenda(EventWrapper ew, HttpServletRequest req)
         throws WebserviceException {
      Integer agendaId = (Integer) req
            .getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      
      if (agendaId != null) {
         ceService.removeNodeFromAgenda(ew.getNode(), agendaId);
      } else {
         throw new BadRequestException(
               "Agenda id needed to decouple the evenement from the agenda");
      }
   }
}

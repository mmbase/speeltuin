/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.jdom.Element;
import com.sun.syndication.feed.synd.SyndFeed;

import nl.kennisnet.cpb.webservice.RestWebserviceServlet;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.RedactiegroepWrapper;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAOImpl;
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
public class AgendaDelegate extends AbstractRestDelegate {

   public final static AbstractRestDelegate INSTANCE = new AgendaDelegate();

   private static Logger log = Logging
         .getLoggerInstance(AgendaDelegate.class.getName());

   private AgendaDelegate() {}

   @Override
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {

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
   }


   @Override
   public void doPut(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      doPost(req, res);
   }
  
   @Override
   public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, WebserviceException {
      Integer agendaId = (Integer) req.getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      if (agendaId != null) {
         System.out.println("FOUND ID AGENDA for delete=> " + agendaId);
         ceService.deleteAgendaById(agendaId);
      }
   }

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {
      final Integer ceId = (Integer) req
            .getAttribute(AbstractRestDelegate.AGENDA_ID_ATTR);
      AgendaWrapper aw = dao.getAgendaWrapperById(ceId);
      translationService.outputAtomFeed(aw,req, res);
   }

}

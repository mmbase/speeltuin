/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.kennisnet.cpb.webservice.domain.GallerijWrapper;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAOImpl;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * A webservice-delegate for handling the read request for a content entry
 * document. Input: a number containing the id of the content element/ Output:
 * an atom 1.0 XML-feed containing the data from the specified content element.
 *
 * @author dekok01
 *
 */
public class ContentEntryDelegate extends AbstractRestDelegate {

   public final static AbstractRestDelegate INSTANCE = new ContentEntryDelegate();
   private static Logger log = Logging
         .getLoggerInstance(ContentEntryDelegate.class.getName());

   private ContentEntryDelegate() {
   }

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {
      final Integer ceId = (Integer) req
            .getAttribute(AbstractRestDelegate.CONTENT_ID_ATTR);

      Node n = dao.getNodeById(ceId);
      // Retrieve relations for node, depending on the type of content element
      String contentXML;
      if ("Kanaal".equals(n.getStringValue("elementtype"))) {
         AgendaWrapper kw = dao.getAgendaWrapperById(n.getIntValue("number"));
         translationService.outputAtomEntry(kw,req, res);

      } else if ("Gallerij".equals(n.getStringValue("elementtype"))) {
         GallerijWrapper gw = dao.retrieveImagesOfNode(n, false);
         translationService.outputAtomEntry(gw, req, res);

      } else if ("LangArtikel".equals(n.getStringValue("elementtype"))){
         LangArtikelWrapper lw = dao.getLangArtikelWrapperByNumber(n.getIntValue("number"));
         translationService.outputAtomEntry(lw, req, res);

      } else { //Artikel, Plaatje,Attendering
         NodeWrapper nw = dao.retrieveRelationsOfNode(n, false,true,true,true);
         translationService.outputAtomEntry(nw, req, res);
      }
   }

   /**
    * Setter for the webservice dao. Could be used for dependency injection in
    * future
    *
    * @param dao
    *           the dao
    */
   public void setDao(WebserviceCommunicatorDAOImpl dao) {
      this.dao = dao;
   }
}

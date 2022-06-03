package nl.kennisnet.cpb.webservice.service;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.io.FeedException;

import nl.kennisnet.cpb.webservice.domain.GallerijWrapper;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;

import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO;

/**
 * Service for translating content elements (delivered in wrapper-classes) to an
 * Atom feed.
 *
 * Log: not needed
 *
 * @author dekok01
 * @version $Revision: 1.0
 */
public interface ContentElementTranslationService {

   public String KNWS_PREFIX = "ws";
   public String ATOM_TYPE = "atom_1.0";
   public String ENCODING = "UTF-8";
   public Namespace HUB_NS = Namespace.getNamespace("hub",
         "http://www.kennisnet.nl/hub");
   public Namespace ATOM_NS = Namespace
         .getNamespace("http://www.w3.org/2005/Atom");
   public Namespace KNWS_NS = Namespace
   .getNamespace("ws","http://www.kennisnet.nl/ws");
   String IMAGE_URL_SUFFIX = "/img.db?";


   /**
    * Send an Atom Entry document as response given the node and the
    * XML-representation of the content. Main function is to create the entry
    * envelope.
    *
    * @param n
    *           the node from the cloud
    * @param contentXML
    *           the XML-representation of the content element
    * @param req
    *           the request
    * @param res
    *           the response
    * @throws FeedException
    *            if there is a problem compiling the feed or outputting it as
    *            XML
    * @throws IOException
    *            if there is a problem the outputstream
    */

   public void setDAO(WebserviceCommunicatorDAO dao);


   public AgendaWrapper parseAgenda(HttpServletRequest req)
      throws WebserviceException, IOException;


   public EventWrapper parseEvent(HttpServletRequest req)
      throws WebserviceException, IOException;


   public void outputAtomEntry(NodeWrapper n, HttpServletRequest req,
         HttpServletResponse res)
      throws WebserviceException, IOException;


   public void outputAtomFeed(AgendaWrapper n, HttpServletRequest req,
         HttpServletResponse res)
      throws WebserviceException, IOException;


   public void outputAtomEntry(EventWrapper kw,
         HttpServletRequest req, HttpServletResponse res)
      throws WebserviceException, IOException;


   public void outputAtomEntry(GallerijWrapper kw,
         HttpServletRequest req, HttpServletResponse res)
      throws WebserviceException, IOException;


   public void outputAtomEntry(LangArtikelWrapper kw,
         HttpServletRequest req, HttpServletResponse res)
      throws WebserviceException, IOException;

   public String convertElementToString(Element elem)
      throws WebserviceException;

}

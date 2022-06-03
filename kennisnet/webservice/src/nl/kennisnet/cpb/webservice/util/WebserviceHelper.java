package nl.kennisnet.cpb.webservice.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;

import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;

public class WebserviceHelper {
   
   private final static String FEED_OPENTAG = "<feed xmlns=\"http://www.w3.org/2005/Atom\">";
   private final static String FEED_CLOSETAG = "</feed>";

   /**
    * Determine the URL of the server
    *
    * @param req
    *           the current request
    * @return a string containing the server-url.
    */
   public static String createCopaBaseURL(HttpServletRequest req) {
      //TODO which server should this point to
    //TODO configure with property file?
      return req.getRequestURL().substring(0,
            req.getRequestURL().toString().indexOf(req.getServletPath(), 0));
   }

   
   public static String createAttachmentBaseURL() {
      //TODO which server should this point to
      //TODO configure with property file?
      return "http://localhost:8080/cpbws/attachments/";
   }
   
   public static String createImageBaseURL() {
      //TODO which server should this point to
      //TODO configure with property file?
      return "http://localhost:8080/cpbws" + ContentElementTranslationService.IMAGE_URL_SUFFIX;
   }

   
   /** Retrieve the Atom entry from the request
    * @param req the httprequest
    * @return the atom entry represented as a JDOM element
    * @throws BadRequestException if the Atom entry is not present
    */
   public static SyndFeed retrieveFeedFromRequest(HttpServletRequest req)
         throws BadRequestException {
      SyndFeed feed = parseInputXML(getXmlReader(req));
      return feed;
   }

   private static Reader getXmlReader(HttpServletRequest req)
      throws BadRequestException {
      try {
         Reader r = null;
         if ("application/x-www-form-urlencoded".equals(req.getContentType())) {
            String xml = req.getParameter("xml");
            if (xml == null) {
               throw new BadRequestException(
                     "No xml parameter present in formencoded input");
            }
            r = new StringReader(xml);
         } else {
            r = new InputStreamReader(req.getInputStream());
         }
         return r;
      } catch (Exception e) {
         throw new BadRequestException(e);
      }
   }


   /** Retrieve the Atom entry from the request
    * @param req the httprequest
    * @return the atom entry represented as a JDOM element
    * @throws BadRequestException if the Atom entry is not present
    */
   public static SyndEntry retrieveEntryFromRequest(HttpServletRequest req)
         throws BadRequestException {
      try {
         Reader r = getXmlReader(req);
         StringBuffer buff = new StringBuffer();
         buff.append(FEED_OPENTAG);
         char[] cbuff = new char[100];
         int i = 0;
         while((i = r.read(cbuff)) > 0) {
            buff.append(cbuff, 0, i);
         }
         buff.append(FEED_CLOSETAG);
         StringReader sr = new StringReader(buff.toString());
         SyndFeed feed = parseInputXML(sr);
         if (feed.getEntries() != null && feed.getEntries().size() > 0 ) {
            return (SyndEntry) feed.getEntries().get(0);
         }
         return null;
      } catch (IOException ioe) {
         throw new BadRequestException(ioe.getMessage());
      }
   }

   /** Retrieve the Atom feed from the request
    * @param req the httprequest
    * @return the atom entry represented as a JDOM element
    * @throws BadRequestException if the Atom entry is not present
    */
   public static Element retrieveJDOMFromRequest(HttpServletRequest req)
         throws BadRequestException {
      Element res = null;
      try {
         Reader r = null;
         if ("application/x-www-form-urlencoded".equals(req.getContentType())) {
            String xml = req.getParameter("xml");
            if (xml == null) {
               throw new BadRequestException(
                     "No xml parameter present in formencoded input");
            }
            r = new StringReader(xml);
         } else {
            r = new InputStreamReader(req.getInputStream());
         }

         /*
          * BufferedReader br = new BufferedReader(r); String line; while ((line =
          * br.readLine()) != null) { System.out.println(line); }
          */

         SyndFeed feed = parseInputXML(r);
         List<Element> elements = (List<Element>) feed.getForeignMarkup();
         if (elements != null && elements.size() > 0) {
            res = elements.get(0).getChild("ce");
         }
         else {
            throw new BadRequestException("Missing ce tag in input");
         }
      } catch (Exception e) {
         throw new BadRequestException(e);
      }
      return res;
   }

   /** Create a feed from a reader
    * @param r the reader
    * @return the feed
    */
   public static SyndFeed parseInputXML(Reader r) {
      SyndFeed res = null;
      SyndFeedInput input = new SyndFeedInput(true);
      try {
         res = input.build(r);
      } catch (Exception e) {
         res = null;
      }
      return res;
   }

}

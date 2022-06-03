/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Query;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.jdom.CDATA;
import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.lucene.ImageQueryBuilder;
import nl.kennisnet.cpb.webservice.lucene.ImageResult;
import nl.kennisnet.cpb.webservice.lucene.ImageResults;
import nl.kennisnet.cpb.webservice.lucene.Searcher;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

/**
 * @author dekok01
 * 
 */
public class SearchImageDelegate extends AbstractRestDelegate {

   public final static SearchImageDelegate INSTANCE = new SearchImageDelegate();

   private SearchImageDelegate() {
   }

   private static Logger log = Logging
         .getLoggerInstance(SearchImageDelegate.class.getName());

   private static final String PARAM_SEARCH_TERM = "text";

   //Optional Parameter for redactiegroup
   private static final String PARAM_REDACTIEGROUP = "redactiegroup";
   /**
    * Optional paging parameters
    */
   private static final String PARAM_PAGENR = "pageNr";
   private static final String PARAM_NR_OF_RESULTS = "nrOfResults";
   private static final int DEFAULT_NR_OF_RESULTS = 10;

   

   // TODO refactor the feed generation to a service call

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {

      ImageQueryBuilder q = createSearchQuery(req);

      // Get specified number of results if present in request
      int nrOfResults = DEFAULT_NR_OF_RESULTS;
      if (req.getParameter(PARAM_NR_OF_RESULTS) != null
            && !"".equals(req.getParameter(PARAM_NR_OF_RESULTS))) {
         String nrOfResultsStr = req.getParameter(PARAM_NR_OF_RESULTS);
         try {
            nrOfResults = Integer.parseInt(nrOfResultsStr);
         } catch (Exception e) {
            throw new BadRequestException(e);
         }
      }
      if (nrOfResults > MAX_NR_SEARCH_RESULTS) {
         throw new BadRequestException("Number of results should be a reasonable number, maximum is set at " + MAX_NR_SEARCH_RESULTS);
      }

      // Get specified page number if present in request
      int offset = 0;
      if (req.getParameter(PARAM_PAGENR) != null
            && !"".equals(req.getParameter(PARAM_PAGENR))) {
         String pageNumStr = req.getParameter(PARAM_PAGENR);
         try {
            offset = nrOfResults * (Integer.parseInt(pageNumStr) - 1);
         } catch (Exception e) {
            throw new BadRequestException(e);
         }
      }
      if (log.isDebugEnabled()) {
         log.debug("Offset = " + offset + ",nr of results=" + nrOfResults);
      }

      Searcher searcher = new Searcher();
      searcher.init();

      ImageResults results = null;
      try {
         results = searcher.getImageResults(q, offset, nrOfResults);
      } catch (Exception e) {
         results = new ImageResults();
         results.setHitCount(0);
         results.setHits(new ArrayList<ImageResult>());
         try {
            log.error("error in user search query: "
                  + q.buildQuery().toString(), e);
         } catch (Exception ex) {
            log.error("error in user search query:", e);
         }
      } finally {
         searcher.close();
      }

      try {

         // Create atom feed from search results
         SyndFeed feed = createFeed(req, res, results, q.buildQuery());

         if (feed != null) {
            // Write feed to response
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, res.getWriter());
            res.getWriter().flush();
            res.setStatus(HttpURLConnection.HTTP_OK);
         }

      } catch (Exception e) {
         throw new WebserviceException(e);
      }

   }

   /**
    * Create the search query based on the input parameters in the request
    * 
    * @param req
    *           the request
    * @return the query
    */
   private ImageQueryBuilder createSearchQuery(HttpServletRequest req)
         throws WebserviceException {
      ImageQueryBuilder q = new ImageQueryBuilder();

      String searchTerm = req.getParameter(PARAM_SEARCH_TERM);

      //Add search term to query (optional)
      if (searchTerm != null && !"".equals(searchTerm)) {
         // Search for one of the terms, not an exact match
         q.setSearchText(searchTerm);
      } 
      
      //Add redactiegroup to query if present (optional)
      String redactiegroup = req.getParameter(PARAM_REDACTIEGROUP);
      if (redactiegroup != null && !"".equals(redactiegroup)) {
         q.setRedactieGroup(redactiegroup);
      } 

      return q;
   }

   /**
    * Create an Atom-feed for formatting the search results specified.
    * 
    * @param req
    *           the original request
    * @param res
    *           the original response
    * @param results
    *           the search results from the search index
    * @param booleanQuery
    * @return
    * @throws WebserviceException
    *            in case of missing input or a corrupt index
    */
   private SyndFeed createFeed(HttpServletRequest req, HttpServletResponse res,
         ImageResults results, Query q) throws WebserviceException {
      SyndFeed f = new SyndFeedImpl();

      f.setUri(req.getRequestURL() + "?" + req.getQueryString());
      f.setPublishedDate(new Date());
      f.setTitle("Zoek resultaat");
      f.setFeedType(ContentElementTranslationService.ATOM_TYPE);
      f.setEncoding(ContentElementTranslationService.ENCODING);

      // Create information about the total number of hits in the search
      Element info = new Element("feedInfo",
            ContentElementTranslationService.HUB_NS).addContent(new Element(
            "entryCount", ContentElementTranslationService.HUB_NS)
            .addContent("" + results.getHitCount()));
      f.setForeignMarkup(Collections.singletonList(info));

      // Add the individual entries
      List<SyndEntry> entries = new ArrayList<SyndEntry>(results.getHits()
            .size());
      for (ImageResult ce : results.getHits()) {
         entries.add(createEntry(req, res, ce, q));
      }
      f.setEntries(entries);

      return f;
   }

   /**
    * Create an Atom-entry for a content element based on the CEResult from the
    * search index
    * 
    * @param req
    *           the original request
    * @param res
    *           the original response
    * @param ce
    *           the search result from the search index
    * @param q
    * @return
    * @throws WebserviceException
    *            if the corresponding node cannot be found
    */
   private SyndEntry createEntry(HttpServletRequest req,
         HttpServletResponse res, ImageResult im, Query q) throws WebserviceException {

      SyndEntry e = new SyndEntryImpl();
      e.setUri(WebserviceHelper.createImageBaseURL() + im.getNumber());
      e.setTitle(im.getTitle());
      e.setAuthor("");
      e.setUpdatedDate(new Date());

      SyndContent summary = new SyndContentImpl();
      summary.setType("text");
      summary.setValue(im.getDescription());
      e.setDescription(summary);

      SyndContent c = new SyndContentImpl();
      c.setType("text/xml");
      c.setValue(translationService.convertElementToString(createImageElement(req,im)));

      e.setContents(Collections.singletonList(c));      
      
      return e;      
   }

   private Element createImageElement(HttpServletRequest req, ImageResult im) {
      Element res = new Element("image");
      res.addContent(new Element("number").addContent(new CDATA(""
            + im.getNumber())));
      res.addContent(new Element("title").addContent(new CDATA(im.getTitle())));
      res.addContent(new Element("description").addContent(new CDATA(im
            .getDescription())));
      res.addContent(new Element("notitie").addContent(new CDATA(im
            .getNotitie())));
      res.addContent(new Element("width").addContent(new CDATA(""
            + im.getWidth())));
      res.addContent(new Element("height").addContent(new CDATA(""
            + im.getHeight())));
      res.addContent(new Element("filesize").addContent(new CDATA(""
            + im.getFilesize())));
      res.addContent(new Element("url").addContent(new CDATA(WebserviceHelper
            .createImageBaseURL()            
            + im.getNumber())));
      return res;
   }
}

/**
 * 
 */
package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

import nl.kennisnet.cpb.webservice.lucene.CEQueryBuilder;
import nl.kennisnet.cpb.webservice.lucene.CEResult;
import nl.kennisnet.cpb.webservice.lucene.KanaalStatus;
import nl.kennisnet.cpb.webservice.lucene.Results;
import nl.kennisnet.cpb.webservice.lucene.SearchField;
import nl.kennisnet.cpb.webservice.lucene.Searcher;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;

/**
 * Handles search request issued to the Copa webservice. This works similar to
 * SearchAllCEAction.java
 * 
 * See Technical Design Vak Communities Chapter 10.2.12.1 for a description of
 * the search parameters
 * 
 * @author dekok01
 * 
 */
public class SearchDelegate extends AbstractRestDelegate {

   private static Logger log = Logging.getLoggerInstance(SearchDelegate.class
         .getName());

   public final static SearchDelegate INSTANCE = new SearchDelegate();

   private static final String PARAM_SEARCH_TERM = "text";
   private static final String PARAM_EXACT = "exact";
   private static final String TOKEN_EXACT_TRUE = "true";

   /**
    * Optional paging parameters
    */
   private static final String PARAM_PAGENR = "pageNr";
   private static final String PARAM_NR_OF_RESULTS = "nrOfResults";
   private static final int DEFAULT_NR_OF_RESULTS = 20;

   private static final String PARAM_FIELD = "field";
   private static final String TOKEN_FIELD_CE_TITEL = "title";
   private static final String TOKEN_FIELD_CE_BODY = "body";
   private static final String TOKEN_FIELD_CE_NOTITIE = "note";
   // TODO Is deze nodig? Zo ja, documenteren in TO?

   /**
    * A list of elementtypes, separated by whitespace
    */
   private static final String PARAM_TYPES = "types";
   private static final String PARAM_KOPPELING = "related";
   private static final String TOKEN_KOPPELING_PAGE = "page";
   private static final String TOKEN_KOPPELING_KANAAL = "kanaal";

   private static final String PARAM_REDACTIE = "redactiegroepen";

   private static final String PARAM_WHEN_PLACED = "whenPlaced";
   private static final String TOKEN_WHEN_PLACED_YEAR = "week";
   private static final String TOKEN_WHEN_PLACED_3MONTH = "month";
   private static final String TOKEN_WHEN_PLACED_MONTH = "3month";
   private static final String TOKEN_WHEN_PLACED_WEEK = "year";
   private static final String TOKEN_ALL = "all";
   
   private static final String PARAM_IS_AGENDA = "isAgenda"; 

   private SearchDelegate() {
   }

   // TODO refactor the feed generation to a service call

   @Override
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {
      CEQueryBuilder q = createSearchQuery(req);

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

      Results results = null;
      try {
         results = searcher.getResults(q, offset, nrOfResults);
      } catch (Exception e) {
         results = new Results();
         results.setHitCount(0);
         results.setHits(new ArrayList<CEResult>());
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
   private CEQueryBuilder createSearchQuery(HttpServletRequest req) {
      CEQueryBuilder q = new CEQueryBuilder();

      String exact = req.getParameter(PARAM_EXACT);
      String searchTerm = req.getParameter(PARAM_SEARCH_TERM);

      if (searchTerm != null && !"".equals(searchTerm)) {
         if (exact != null && TOKEN_EXACT_TRUE.equals(exact)) {
            q.setExact(true);
            q.setSearchText('"' + searchTerm + '"');
         } else {
            q.setExact(false);
            q.setSearchText(searchTerm);
         }
      }

      exact = req.getParameter(PARAM_KOPPELING);
      if (TOKEN_KOPPELING_KANAAL.equals(exact)) {
         String kp = req.getParameter(PARAM_WHEN_PLACED);
         if (TOKEN_WHEN_PLACED_YEAR.equals(kp)) {
            q.setKanaalState(KanaalStatus.ADDED_LAST_YEAR);
         } else if (TOKEN_WHEN_PLACED_3MONTH.equals(kp)) {
            q.setKanaalState(KanaalStatus.ADDED_LAST_3_MONTHS);
         } else if (TOKEN_WHEN_PLACED_MONTH.equals(kp)) {
            q.setKanaalState(KanaalStatus.ADDED_LAST_MONTH);
         } else if (TOKEN_WHEN_PLACED_WEEK.equals(kp)) {
            q.setKanaalState(KanaalStatus.ADDED_LAST_WEEK);
         } else if (TOKEN_ALL.equals(kp)) {
            q.setKanaalState(KanaalStatus.IN_KANAAL);
         }
      }

      if (TOKEN_KOPPELING_PAGE.equals(exact)) {
         q.setOnPage(true);
      }

      exact = req.getParameter(PARAM_FIELD);
      if (TOKEN_FIELD_CE_TITEL.equals(exact)) {
         q.setSearchField(SearchField.TITEL);
      } else if (TOKEN_FIELD_CE_NOTITIE.equals(exact)) {
         q.setSearchField(SearchField.NOTITIE);
      } else if (TOKEN_FIELD_CE_BODY.equals(exact)) {
         q.setSearchField(SearchField.BODY);
      } else {
         q.setSearchField(SearchField.ALL);
      }

      //Convert code of redactiegroep to number of redactiegroep
      String[] reds = req.getParameterValues(PARAM_REDACTIE);
      if (reds != null) {
         List<String> newList = new ArrayList(reds.length);
         for (int i = 0; i < reds.length; i ++) {
            if (reds[i].contains("*")) {         
               for (Node n: dao.findRedactieGroepByLike(reds[i])) {
                  newList.add(n.getStringValue("redactiegroepen.number"));
               }
            } else {               
               Node n = dao.findRedactieGroepByCode(reds[i]);
               if (n == null) {
                  log.warn("Searching for non-existing redactiegroep "  + reds[i]);
               } else {
                  newList.add(n.getStringValue("redactiegroepen.number"));         
               }
            }
         }         
         q.setRedactieGroups(newList.toArray(new String[newList.size()]));
      }

      exact = req.getParameter(PARAM_TYPES);
      q.setElementType(exact);
      
      /*
      String isAgenda= req.getParameter(PARAM_IS_AGENDA);
      if (isAgenda != null) {
         if ("1".equals(isAgenda)) {
            q.setIsAgenda("1");
         } else {
            q.setIsAgenda("0");
         }         
      }
      */
     
      
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
         Results results, Query q) throws WebserviceException {
      SyndFeed f = new SyndFeedImpl();

      f.setUri(req.getRequestURL() + "?" + req.getQueryString());
      f.setPublishedDate(new Date());
      f.setTitle("Zoek resultaat");
      f.setFeedType(ContentElementTranslationService.ATOM_TYPE);
      f.setEncoding(ContentElementTranslationService.ENCODING);

      // Create paging-links for all individual search results pages
      /*
       * String newURL = createRequestURLWithoutPage(req); List<SyndLink> links =
       * new ArrayList<SyndLink>( results.getHitCount() / 20 + 1); for (int i =
       * 0; i < results.getHitCount(); i += 20) { int curPageNr = i / 20 + 1;
       * links.add(createLink("link_" + curPageNr, newURL + "&page=" +
       * curPageNr)); }
       * 
       * //Create the first, next last and previous links for paging the results
       * int totalNrOfPages = (int) Math .ceil(((double) results.getHitCount()) /
       * 20.0); String page = req.getParameter(TOKEN_KOPPELING_PAGE); int pageNr =
       * 1; if (page != null) { try { pageNr = Integer.parseInt(page); } catch
       * (NumberFormatException nfe) { throw new BadRequestException("Missing or
       * malformed page number"); } } if (pageNr > 1 && totalNrOfPages > 1) {
       * links.add(createLink("first", newURL + "&page=1"));
       * links.add(createLink("previous", newURL + "&page=" + (pageNr - 1))); }
       * if (pageNr < totalNrOfPages) { links.add(createLink("last", newURL +
       * "&page=" + totalNrOfPages)); links.add(createLink("next", newURL +
       * "&page=" + (pageNr + 1))); } f.setLinks(links);
       */
      // Create information about the total number of hits in the search
      Element info = new Element("feedInfo",
            ContentElementTranslationService.HUB_NS).addContent(new Element(
            "entryCount", ContentElementTranslationService.HUB_NS)
            .addContent("" + results.getHitCount()));
      f.setForeignMarkup(Collections.singletonList(info));

      // Add the individual entries
      List<SyndEntry> entries = new ArrayList<SyndEntry>(results.getHits()
            .size());
      for (CEResult ce : results.getHits()) {
         entries.add(createEntry(req, res, ce,q));
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
         HttpServletResponse res, CEResult ce, Query q) throws WebserviceException {

      SyndEntry e = new SyndEntryImpl();
      e.setUri("" + ce.getNumber());
      
      //Handle title
      String strippedTitle = stripHTML(ce.getTitle());
      String titleResult = addHighlighting(strippedTitle,q);
      
      if (titleResult == null || titleResult.length() == 0) {
         e.setTitle(strippedTitle);         
      }
      else {
         e.setTitle(titleResult);
      }
      
      // Handle the body
      SyndContent cont = new SyndContentImpl();

      Node n = dao.getNodeById(new Long(ce.getNumber()).intValue());
      StringBuffer body = new StringBuffer();
      body.append(n.getStringValue("intro"));
      body.append(n.getStringValue("body"));
      String strippedBody = stripHTML(body.toString());
      
      cont.setType("html");
      String result = addHighlighting(strippedBody,q);
      if (result == null || result.length() == 0) {
         if (strippedBody != null && strippedBody.length() > 0) {
            cont.setValue(strippedBody.substring(0, Math.min(strippedBody.length() - 1, 50)));
         } else {
            cont.setValue("");
         }
      } else {
         cont.setValue(result);   
      }
      
      e.setDescription(cont);
      return e;      
   }

   private String addHighlighting(String text, Query q) throws WebserviceException {
      String res = "";
      Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<strong>","</strong>"), new QueryScorer(q));
      try {
         
         StandardAnalyzer sa = new StandardAnalyzer();
         res = highlighter.getBestFragments(sa.tokenStream(null, new StringReader(text)), text, 3,"...");
      } catch (IOException e) {
         throw new WebserviceException(e);
      }

      return res;
   }

   /**
    * Strip the tags from the body.
    * 
    * @param body
    *           the body of the ce
    * @return the body without tags
    */
   private String stripHTML(String body) {

      // TODO this regular expression has been copied from the
      // UtilityServiceImpl in the vakcommunities project. Convert this to a
      // single HTML stripping utility?
      return body.replaceAll("\\<.*?\\>", "");
   }

   /**
    * Cut off the servlet path "search" at the end of the request url
    * 
    * @param req
    * @return
    
   private String createBaseWebserviceURL(HttpServletRequest req) {
      return req.getRequestURL().substring(0,
            req.getRequestURL().lastIndexOf("/") + 1);
   }

   /**
    * Recreate the request url but leave out the page parameter
    * 
    * @param req
    * @return    
   private String createRequestURLWithoutPage(HttpServletRequest req) {
      StringBuffer newURL = new StringBuffer(req.getRequestURL() + "?");
      Enumeration<String> parms = req.getParameterNames();
      boolean isFirst = true;
      while (parms.hasMoreElements()) {
         String p = parms.nextElement();
         if (!TOKEN_KOPPELING_PAGE.equals(p)) {
            if (!isFirst) {
               newURL.append("&");
            }
            isFirst = false;
            newURL.append(p + "=" + req.getParameter(p));
         }
      }
      return newURL.toString();
   }*/
}

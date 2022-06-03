package nl.kennisnet.cpb.webservice.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.EscapeStrategy;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import nl.kennisnet.cpb.tree.CacheAccessor;
import nl.kennisnet.cpb.util.DateUtil;
import nl.kennisnet.cpb.webservice.RestWebserviceServlet;
import nl.kennisnet.cpb.webservice.domain.AttachmentWrapper;
import nl.kennisnet.cpb.webservice.domain.GallerijWrapper;
import nl.kennisnet.cpb.webservice.domain.ImageWrapper;
import nl.kennisnet.cpb.webservice.domain.InternalLinkWrapper;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper;
import nl.kennisnet.cpb.webservice.domain.LinkWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.domain.OrderedWrapper;
import nl.kennisnet.cpb.webservice.domain.PageWrapper;
import nl.kennisnet.cpb.webservice.domain.ParagraphWrapper;
import nl.kennisnet.cpb.webservice.domain.RedactiegroepWrapper;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.error.XMLTranslationException;
import nl.kennisnet.cpb.webservice.util.WebserviceHelper;

public class ContentElementTranslationServiceImpl implements
      ContentElementTranslationService {
   
  
   private WebserviceCommunicatorDAO dao = new WebserviceCommunicatorDAOImpl();
   
   private static Logger log = Logging
         .getLoggerInstance(ContentElementTranslationServiceImpl.class
               .getName());

   public void setDAO(WebserviceCommunicatorDAO dao) {
      this.dao = dao;
   }

   public AgendaWrapper parseAgenda(HttpServletRequest req) 
      throws WebserviceException, IOException {
      SyndFeed feed = WebserviceHelper.retrieveFeedFromRequest(req);
      AgendaWrapper aw = dao.createAgendaWrapper();
      Node n = aw.getNode();
      // Set fields that are mandatory at creation
      n.setStringValue("elementtype", "Kanaal");
      n.setStringValue("boolean1", "0"); //Display time. Not used in Vakcommunities, so defaulted here
      n.setStringValue("boolean2", "1"); //Type agenda      
      n.setStringValue("getal1", "10"); //Max items. Not used in Vakcommunities, default here.
      n.setStringValue("vrijetekst2", "none"); // Not used in Vakcommunities, default here.
      aw.setTitel(feed.getTitle());
      aw.setIntro(feed.getDescription());

      List l = (List)feed.getForeignMarkup();
      for (Object o: l) {
         Element e = (Element)o;
         if ("notitie".equals(e.getName())) {
            aw.setNotitie(e.getValue());
         /* } else if ("intro".equals(e.getName())) {
            aw.setIntro(e.getValue()); */
         } else if ("redactiegroep".equals(e.getName())) {
            List <RedactiegroepWrapper > rg = aw.getRedactiegroepen();
            if (rg == null ||  ! (rg instanceof ArrayList)) {
               rg = new ArrayList < RedactiegroepWrapper > ();
            }
            rg.add( new RedactiegroepWrapper(e.getValue()));
         }
      }
      List <EventWrapper> entries = new ArrayList <EventWrapper>();
      for(Object o1: feed.getEntries()) {
         SyndEntry s = (SyndEntry) o1;
         entries.add(parseEvent(s));
      }
      aw.setItems(entries);
      return aw;
   }


   private EventWrapper parseEvent(SyndEntry e) {
     EventWrapper ew = dao.createEventWrapper();
     ew.setTitel(e.getTitle());
     for (Object o : e.getContents()) {
        SyndContent c = (SyndContent) o;
        ew.setBody(c.getValue());
     }
     List l = (List)e.getForeignMarkup();
     for (Object o: l) {
        Element fm = (Element)o;
        if ("notitie".equals(fm.getName())) {
           ew.setNotitie(fm.getValue());
        } else if ("startdatum".equals(fm.getName())) {
           try {
              ew.setStartDatum(Integer.parseInt(fm.getValue()));
           } catch (Exception ex) {
              // ignore
           }
        } else if ("einddatum".equals(fm.getName())) {
           try {
              ew.setEindDatum(Integer.parseInt(fm.getValue()));
           } catch (Exception ex) {
              // ignore
           }
        } else if ("lokatie".equals(fm.getName())) {
           ew.setLokatie(fm.getValue());
        } else if ("telefoonnummer".equals(fm.getName())) {
           ew.setTelefoonnummer(fm.getValue());
        } else if ("email".equals(fm.getName())) {
           ew.setEmail(fm.getValue());
        }
     }
     return ew;
   }

   public EventWrapper parseEvent(HttpServletRequest req)
      throws WebserviceException, IOException {
      SyndEntry entry = WebserviceHelper.retrieveEntryFromRequest(req);
      if (entry == null) {
         throw new WebserviceException("Invalid entry xml");
      }
      return parseEvent(entry);
   }

   /**
    * Transform the content element to its XML representation
    * 
    * @param ce
    *           the specified content element
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return
    */
   private String createContentXML(NodeWrapper ce,
         String baseUrl) throws WebserviceException {
      return convertElementToString(createSimpleCEContent(ce, baseUrl));
   }

   /**
    * Transform the content element to its XML representation
    * 
    * @param ce
    *           the specified content element
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return
    */
   private String createContentXML(GallerijWrapper ce,
         String baseUrl) throws WebserviceException {
      Element root = createGallerijContent(ce, baseUrl);
      return convertElementToString(root);
   }

   /**
    * Transform the content element kanaal to its XML representation
    * 
    * @param ce
    *           the specified content element
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return
    */

 
   private SyndFeed newFeed(HttpServletRequest req) {
      // Create Atom entry document
      SyndFeed feed = newFeed();
      feed.setUri(req.getRequestURL().toString());
      return feed;
   }

   private SyndFeed newFeed() {
      // Create Atom entry document
      SyndFeed feed = new SyndFeedImpl();
      feed.setFeedType(ATOM_TYPE);
      feed.setEncoding(ENCODING);
      feed.setPublishedDate(new Date());
      return feed;
   }

   private List getNodeData(NodeWrapper nw) {
      List fData = new ArrayList();
      addToList(fData, "notitie", nw.getNotitie());
      if (nw.getRedactiegroepen() != null) {
         for (RedactiegroepWrapper rw: nw.getRedactiegroepen()) {
            addToList(fData, "redactiegroep", rw.getRedactieCode());
         }
      }
      if ( nw.getPages() != null) {
         for (PageWrapper pw: nw.getPages()) {
            addToList(fData, "pagina", pw.getUrl());
         }
      }
      return fData;
   }

   private void addToList(List l, String fieldName, String value) {
      if (value != null && !"".equals(value)) {
         l.add(new Element(fieldName, KNWS_NS).addContent(value));
      }
   }

   public void outputAtomFeed(AgendaWrapper kw, HttpServletRequest req,
         HttpServletResponse res) throws WebserviceException, IOException {
      SyndFeed feed = newFeed(req);
      // set agenda attribs
      feed.setDescription(kw.getIntro());
      feed.setTitle(kw.getTitel());
      feed.setForeignMarkup(getNodeData(kw));
      // a list for the feed's entries
      List entries = new ArrayList();
      for (EventWrapper item : kw.getItems()) {
         item.setId(WebserviceHelper.createCopaBaseURL(req)
         + RestWebserviceServlet.WEBSERVICE_URL + "/agenda/" + kw.getNumber() + "/evenement/"
         + item.getNumber());
         SyndEntry e = makeEntry(item, req);
         if (e != null) {
            entries.add(e);
         }
      }
      feed.setEntries(entries);
      writeFeedDoc(feed, res);
   }

   private SyndEntry makeEntry(EventWrapper item, HttpServletRequest req) {
      SyndEntry e = new SyndEntryImpl();
      if (item.getId() != null) {
         e.setUri(item.getId());
      } else {
         e.setUri(WebserviceHelper.createCopaBaseURL(req)
               + RestWebserviceServlet.WEBSERVICE_URL + "/content/"
               + item.getNumber());
      }
      e.setTitle(item.getTitel());
      e.setUpdatedDate(new Date());
      //e.setDescription();

      // html content
      SyndContent content = new SyndContentImpl();
      content.setValue(item.getBody());
      content.setType("html");
      ArrayList htmlContentList = new ArrayList();
      htmlContentList.add(content);
      e.setContents(htmlContentList);

      // foreign xml content
      List el = getNodeData(item);
      addToList(el, "startdatum", "" + item.getStartDatum());
      addToList(el, "einddatum", "" + item.getEindDatum());
      addToList(el, "lokatie", "" + item.getLokatie());
      addToList(el, "telefoonnummer", item.getTelefoonnummer());
      addToList(el, "email", "" + item.getEmail());

      String s = determinePeriod(item.getStartDatum(), item.getEindDatum());
      if (s != null && !"".equals(s)) {
         addToList(el, "period", s);
      }
      e.setForeignMarkup(el);
      return e;
   }
   
   private void writeFeedDoc(SyndFeed feed, HttpServletResponse res)
      throws IOException, WebserviceException {
      try {
         SyndFeedOutput output = new SyndFeedOutput();
         Document d = output.outputJDom(feed);
         Element root = d.getRootElement();
         root.addNamespaceDeclaration(HUB_NS);
         root.addNamespaceDeclaration(KNWS_NS);
         root.addNamespaceDeclaration(ATOM_NS);
         Format format = Format.getPrettyFormat();
         format.setEncoding(ENCODING);
         XMLOutputter outputter = new XMLOutputter(format);
         outputter.output(d, res.getWriter());
         res.getWriter().flush();
      } catch (FeedException e) {
         throw new WebserviceException(e);
      }
   }

   public SyndEntry entryFromNode(NodeWrapper n, String contentXML, HttpServletRequest req) {
      SyndEntry e = new SyndEntryImpl();
      SyndContent content = new SyndContentImpl();
      content.setValue(contentXML);
      content.setType("text/xml");
      List clist = new ArrayList();
      clist.add(content);
      e.setContents(clist);
      e.setTitle(n.getTitel());
      e.setUri(WebserviceHelper.createCopaBaseURL(req)
            + "content/" + n.getNumber());
      e.setUpdatedDate(new java.util.Date());
      List<SyndLink> linksList = new ArrayList<SyndLink>(3);
      linksList.add(createLink("self", req.getRequestURL().toString()));
      // linksList.add(createLink("edit", createEditWizardURL(req, n)));
      // TODO get copyurl from copa. Unclear how this should work though
      e.setLinks(linksList);
      return e;
   }

   public void outputAtomEntry(EventWrapper kw, HttpServletRequest req,
         HttpServletResponse res) throws WebserviceException, IOException {
      SyndEntry e = makeEntry(kw, req);
      writeEntryDoc(e, res);
   }

   public void outputAtomEntry(GallerijWrapper kw, HttpServletRequest req,
         HttpServletResponse res) throws WebserviceException, IOException {
      String s = createContentXML(kw,WebserviceHelper.createCopaBaseURL(req));
      SyndEntry e = entryFromNode(kw, s, req);
      writeEntryDoc(e, res);
   }

   public void outputAtomEntry(LangArtikelWrapper kw, HttpServletRequest req,
         HttpServletResponse res) throws WebserviceException, IOException {
      String s = createContentXML(kw,WebserviceHelper.createCopaBaseURL(req));
      SyndEntry e = entryFromNode(kw, s, req);
      writeEntryDoc(e, res);
   }

   public void outputAtomEntry(NodeWrapper n, HttpServletRequest req,
         HttpServletResponse res) throws WebserviceException, IOException {
      String s = createContentXML(n,WebserviceHelper.createCopaBaseURL(req));
      SyndEntry e = entryFromNode(n, s, req);
      writeEntryDoc(e, res);
   }

   private void writeEntryDoc(SyndEntry e, HttpServletResponse res)
      throws IOException, WebserviceException {
      SyndFeed feed = newFeed();
      List l = new ArrayList();
      l.add(e);
      feed.setEntries(l);
      try {
         SyndFeedOutput output = new SyndFeedOutput();
         Document d = output.outputJDom(feed);
         Element entry = d.getRootElement().getChild("entry",ATOM_NS);
         entry.addNamespaceDeclaration(HUB_NS);
         entry.addNamespaceDeclaration(KNWS_NS);
         entry.addNamespaceDeclaration(ATOM_NS);

         Format format = Format.getPrettyFormat();
         format.setEncoding(ENCODING);
         XMLOutputter outputter = new XMLOutputter(format);
         outputter.output(entry, res.getWriter());
         res.getWriter().flush();
      } catch (FeedException ex) {
         throw new WebserviceException(ex);
      }
   }

   /*
    * (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementTranslationService#createContentXML(nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper,
    *      java.lang.String[], java.lang.String)
    */
   private String createContentXML(LangArtikelWrapper ce,
         String baseUrl) throws WebserviceException {
      Element root = createLangArtikelContent(ce, baseUrl);
      return convertElementToString(root);
   }

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
   
   private void outputAtomFeed(Node n, String contentXML,
         HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, IOException {
      // Create Atom entry document
      SyndFeed feed = new SyndFeedImpl();
      feed.setFeedType(ATOM_TYPE);
      feed.setEncoding(ENCODING);

      SyndEntry e = new SyndEntryImpl();
      e.setUri(req.getRequestURL().toString());
      e.setTitle(n.getFieldValue("titel").toString());
      e.setAuthor("");
      e.setUpdatedDate(new Date());

      List<SyndLink> linksList = new ArrayList<SyndLink>(3);
      linksList.add(createLink("self", req.getRequestURL().toString()));
      // linksList.add(createLink("edit", createEditWizardURL(req, n)));
      // TODO get copyurl from copa. Unclear how this should work though
      e.setLinks(linksList);

      SyndContent summary = new SyndContentImpl();
      summary.setType("text");

      // TODO Not sure if this will be enough for all ce's.
      summary.setValue(n.getStringValue("intro"));

      e.setDescription(summary);
      SyndContent c = new SyndContentImpl();
      c.setType("text/xml");
      c.setValue(contentXML);

      e.setContents(Collections.singletonList(c));
      feed.setEntries(Collections.singletonList(e));

      try {
         SyndFeedOutput output = new SyndFeedOutput();
         Document d = output.outputJDom(feed);
         Document entryDoc = new Document();
         if (d != null) {
            Element elem = d.getRootElement().getChild("entry", ATOM_NS);
            elem.addNamespaceDeclaration(HUB_NS);
            elem.addNamespaceDeclaration(KNWS_NS);
            elem.addNamespaceDeclaration(ATOM_NS);
            entryDoc.setRootElement((Element) elem.detach());
         }
         Format format = Format.getPrettyFormat();
         format.setEncoding(ENCODING);

         XMLOutputter outputter = new XMLOutputter(format);
         outputter.output(entryDoc, res.getWriter());
         res.getWriter().flush();
      } catch (FeedException ex) {
         throw new WebserviceException(ex);
      }
   }

   /**
    * Helper function for creating simple links for the atom document
    *
    * @param rel
    *           the input for attribute rel
    * @param href
    *           the input for attribute href
    * @return a new syndlink for use in enclosing it in an atom document
    */
   private SyndLink createLink(String rel, String href) {
      SyndLink l = new SyndLinkImpl();
      l.setRel(rel);
      l.setHref(href);
      return l;
   }

   /**
    * Convert a simple domain object to its XML-representation. This includes
    * artikel, attendering, htmlblok, plaatje. Every field is mapped onto a node
    * with the same name
    * 
    * @param n
    *           the specified node
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return a JDOM element containing the XML-representation of the node
    */
   private Element createSimpleCEContent(NodeWrapper nw,
         String baseUrl) {
      Node n = nw.getNode();

      Element root = new Element("ce", Namespace.NO_NAMESPACE);

      // Print standard fields of ce
      for (String f : WebserviceCommunicatorDAO.FIELDS_IN_CONTENT_XML) {
         root.addContent(new Element(f).addContent(new CDATA(n
               .getStringValue(f))));
      }
      // Print related nodes
      Element related = new Element("related");
      addImageToRelated(nw.getImage(), related, baseUrl + IMAGE_URL_SUFFIX);
      addRelatedItemsToRelated(nw.getRelatedItems(), related, baseUrl);

      Element pages = new Element("pages");
      if (nw.getPages() != null ) {
         for (PageWrapper rel : nw.getPages()) {
            pages.addContent(createElementForPage(rel, baseUrl));
         }
      }
      Element redactiegroepen = new Element("redactiegroepen");
      if (nw.getRedactiegroepen() != null) {
         for (RedactiegroepWrapper rel : nw.getRedactiegroepen()) {
            redactiegroepen.addContent(createElementForRedactiegroep(rel));
         }
      }
      root.addContent(related);
      root.addContent(pages);
      root.addContent(redactiegroepen);

      return root;
   }

   /**
    * Convert a gallerij domain object to its XML-representation. Every field is
    * mapped onto a node with the same name
    * 
    * @param gw
    *           the wrapper around the gallerij
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return a JDOM element containing the XML-representation of the node
    */
   private Element createGallerijContent(GallerijWrapper gw,
         String baseUrl) {
      Node n = gw.getNode();

      Element root = new Element("ce", Namespace.NO_NAMESPACE);

      // Print standard fields of ce
      for (String f : WebserviceCommunicatorDAO.FIELDS_IN_CONTENT_XML) {
         root.addContent(new Element(f).addContent(new CDATA(n
               .getStringValue(f))));
      }

      Element related = new Element("related");
      addImagesToRelated(gw, related, baseUrl + IMAGE_URL_SUFFIX);
      root.addContent(related);

      return root;
   }

  

   /**
    * Convert a lang artikel domain object to its XML-representation. Every
    * field is mapped onto a node with the same name
    * 
    * @param ce
    *           the specified node
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return a JDOM element containing the XML-representation of the node
    */
   private Element createLangArtikelContent(LangArtikelWrapper ce,
          String baseUrl) {
      Node n = ce.getNode();

      Element root = new Element("ce", Namespace.NO_NAMESPACE);

      // Print standard fields of ce
      for (String f : WebserviceCommunicatorDAO.FIELDS_IN_CONTENT_XML) {
         root.addContent(new Element(f).addContent(new CDATA(n
               .getStringValue(f))));
      }

      // Print related nodes
      Element related = new Element("related");
      addImageToRelated(ce.getImageBeforeIntro(), related, baseUrl
            + IMAGE_URL_SUFFIX);
      addImageToRelated(ce.getImageBeforeFirstParagraph(), related, baseUrl
            + IMAGE_URL_SUFFIX);
      addRelatedItemsToRelated(ce.getRelatedItems(), related, baseUrl);

      for (ParagraphWrapper pw : ce.getParagraphs()) {
         root.addContent(createParagraphContent(pw,baseUrl));
      }

      Element pages = new Element("pages");
      for (PageWrapper rel : ce.getPages()) {
         pages.addContent(createElementForPage(rel, baseUrl));
      }
      Element redactiegroepen = new Element("redactiegroepen");
      for (RedactiegroepWrapper rel : ce.getRedactiegroepen()) {
         redactiegroepen.addContent(createElementForRedactiegroep(rel));
      }
      root.addContent(related);
      root.addContent(pages);
      root.addContent(redactiegroepen);
      return root;
   }

   /**
    * Convert a paragraph from a lang artikel to its XML-representation
    * @param pw
    *           the paragraph data
    * @param fields
    *           the fields for the XML
    * @param baseUrl
    *           the url of the server
    * @return a JDOM-element representing the paragraph
    */
   private Element createParagraphContent(ParagraphWrapper pw, 
         String baseUrl) {
      Element root = new Element("paragraph", Namespace.NO_NAMESPACE);

      // Print standard fields of ce
      for (String f :WebserviceCommunicatorDAO.FIELDS_IN_CONTENT_XML) {
         root.addContent(new Element(f).addContent(new CDATA(pw.getNode()
               .getStringValue(f))));
      }
      root.addContent(new Element("pos").addContent("" + pw.getPos()));
      // Print related nodes
      Element related = new Element("related");
      addImageToRelated(pw.getImage(), related, baseUrl + IMAGE_URL_SUFFIX);
      addRelatedItemsToRelated(pw.getRelatedItems(), related, baseUrl);
      root.addContent(related);
      return root;
   }

 

   /**
    * Extract from the Node domain object the values from the specified fields
    * and convert them to a XML-format where every field is mapped onto a node
    * with the same name
    * 
    * Also add the period (based on datum1, datum2) as a convenience node for
    * evenement
    * 
    * @param nw
    *           the specified node
    * @param fields
    *           the node-fields that should be entered into the feed.
    * @param baseUrl
    *           the URL of the server
    * @return a JDOM element containing the XML-representation of the node
    */
   private Element createEvenementContent(NodeWrapper nw, String[] fields,
         String baseUrl) {
      Element ce = new Element("ce", Namespace.NO_NAMESPACE);

      // Print standard fields of ce
      for (String f : fields) {
         ce.addContent(new Element(f).addContent(new CDATA(nw.getNode()
               .getStringValue(f))));
      }

      // Convenience function formatting the interval
      ce.addContent(new Element("period").addContent(new CDATA(determinePeriod(
            nw.getNode().getIntValue("datum1"), nw.getNode().getIntValue(
                  "datum2")))));

      Element related = new Element("related");
      addRelatedItemsToRelated(nw.getRelatedItems(), related, baseUrl);
      ce.addContent(related);
      return ce;
   }

   /**
    * Convenience function for formatting the start and enddate as a period
    * 
    * @param startDate
    *           the start date in seconds
    * @param endDate
    *           the end date in seconds
    * @return a string representation of the period
    */
   private String determinePeriod(int startDate, int endDate) {
      if (startDate < 1) {
         return "";
      }
      String startDateFormatted = DateUtil.formattedTimestamp(startDate,
            "E d MMMMM yyyy", "nl", "NL");
      String res = startDateFormatted;
      if (startDate < endDate) {
         String eindDateFormatted = DateUtil.formattedTimestamp(endDate,
               "E d MMMMM yyyy", "nl", "NL");
         if (!eindDateFormatted.equals(startDateFormatted)) {
            res += " t/m " + eindDateFormatted;
         }
      }
      return res;
   }

   /**
    * Transform a JDOM Element to its String representation
    * 
    * @param elem
    *           the JDOM element
    * @return its String representation
    */
   public String convertElementToString(Element elem)
         throws WebserviceException {
      // Output to String format
      StringWriter res = new StringWriter(1000);
      try {
         XMLOutputter outputter = new XMLOutputter();
         Format f = Format.getPrettyFormat();
         f.setEscapeStrategy(new EscapeStrategy() {
            public boolean shouldEscape(char c) {
               return false;
            }
         });
         f.setOmitDeclaration(true);
         outputter.setFormat(f);
         outputter.output(new Document(elem), res);
      } catch (java.io.IOException e) {
         log.error("Error while converting JDOM-element to String:" + elem);
         throw new XMLTranslationException(e);
      }

      return res.toString();
   }

   /**
    * Append the XML-representation of the (optional) images of the specified
    * cloud-node within the related-node and include the specified type
    * 
    * @param iw
    *           the wrapper around the image data
    * @param related
    *           reference to the JDOM-node in the result tree which should be
    *           filled (further)
    * @param imageUrl
    *           the base URL of the image-servlet
    */
   private void addImageToRelated(ImageWrapper iw, Element related,
         String imageUrl) {

      if (iw != null) {
         Element e = new Element("image");
         addImageFields(iw, e, imageUrl);
         related.addContent(e);
      }
   }

   /**
    * Append the XML-representation of the (optional) images of the specified
    * cloud-node within the related-node and include the specified type
    * 
    * @param n
    *           the node from the cloud
    * @param related
    *           reference to the JDOM-node in the result tree which should be
    *           filled (further)
    * @param imageUrl
    *           the base URL of the image-servlet
    */
   private void addImagesToRelated(GallerijWrapper gw, Element related,
         String imageUrl) {

      for (ImageWrapper i : gw.getImages()) {
         Element e = new Element("image");
         addImageFields(i, e, imageUrl);
         related.addContent(e);
      }

      // TODO fix the gallery ce here
      /*
       * For the gallery-CE, we need to embed the thumnail image in the xml.
       * This should work via a direct lookup on the image servlet, but the code
       * below gives an exception in the mmbase_ws.log concerning the
       * imagemagick conversion List<String> arguments = new ArrayList<String>();
       * arguments.add("s(260)"); int iCacheNodeNumber =
       * n.getFunctionValue("cache", arguments).toInt();
       * 
       * System.out.println("iCacheNodeNumber van image" +
       * n.getStringValue("number") + " is:"+ iCacheNodeNumber);
       * related.addContent(new Element("thumbnailurl").addContent(new
       * CDATA(imageUrl + iCacheNodeNumber)));
       */
   }

   /**
    * Fill the element e with the fields from node n and construct the url using
    * the specified imageUrl
    * 
    * @param n
    *           the cloud-node representing an image
    * @param e
    *           reference to the JDOM-node in the result tree which should be
    *           filled (further)
    * @param imageUrl
    *           the base URL of the image-servlet
    * @param number
    */
   private void addImageFields(ImageWrapper i, Element res, String imageUrl) {
      res.addContent(new Element("number").addContent(new CDATA(""
            + i.getNumber())));
      // res.addContent(new Element("owner").addContent(new
      // CDATA(i.getOwner())));
      res.addContent(new Element("title").addContent(new CDATA(i.getTitle())));
      res.addContent(new Element("description").addContent(new CDATA(i
            .getDescription())));
      res.addContent(new Element("itype").addContent(new CDATA(i.getItype())));
      res.addContent(new Element("popup").addContent(new CDATA(""
            + i.getPopup())));
      res.addContent(new Element("notitie")
            .addContent(new CDATA(i.getNotitie())));
      res.addContent(new Element("thumbnail").addContent(new CDATA(""
            + i.getThumbnail())));
      res.addContent(new Element("width").addContent(new CDATA(""
            + i.getWidth())));
      res.addContent(new Element("height").addContent(new CDATA(""
            + i.getHeight())));
      res.addContent(new Element("pos").addContent(new CDATA("" + i.getPos())));

      res.addContent(new Element("url").addContent(new CDATA(imageUrl
            + i.getNumber())));
   }

   /**
    * Append the XML-representation of the attachments and (internal) links of
    * the specified cloud-node within the related-node and include the specified
    * type
    * 
    * @param l
    *           the list or related items
    * @param related
    *           reference to the JDOM-node which should be filled (further)
    * @param baseUrl
    *           the url of the server
    */
   private void addRelatedItemsToRelated(List<OrderedWrapper> l,
         Element related, String baseUrl) {
      for (OrderedWrapper rel : l) {
         related.addContent(createElementForRelatedItem(rel, baseUrl));
      }

   }

   /**
    * Create the XML representation for the given related link/attachment.
    * 
    * @param node
    *           the related node
    * @param baseUrl
    *           the base url of the copa install
    * @return the XML-element representing the related node
    */
   private Element createElementForRelatedItem(OrderedWrapper node,
         String baseUrl) {
      Element res = new Element("item");

      if (node instanceof AttachmentWrapper) {
         addAttachmentToNode((AttachmentWrapper) node, res, baseUrl);
      } else if (node instanceof InternalLinkWrapper) {
         addInternalLinkToNode((InternalLinkWrapper) node, res, baseUrl);
      } else if (node instanceof LinkWrapper) {
         addLinkToNode((LinkWrapper) node, res);
      }
      return res;
   }

   /**
    * Create the XML representation for the given related page
    * 
    * @param node
    *           the related node
    * @param baseUrl
    *           the base url of the copa install
    * @return the XML-element representing the related node
    */
   private Element createElementForPage(PageWrapper page, String baseUrl) {
      Element res = new Element("page");
      res.addContent(new Element("number").addContent(new CDATA(""
            + page.getCategorieId())));
      res.addContent(new Element("url")
            .addContent(new CDATA("" + page.getUrl())));

      return res;
   }

   /**
    * Create the XML representation for the given redactiegroep
    * 
    * @param rel
    *           the redactiegroep
    * @return the xml
    */
   private Element createElementForRedactiegroep(RedactiegroepWrapper rel) {
      Element res = new Element("redactiegroep");
      res.addContent(rel.getRedactieCode());
      return res;
   }

   /**
    * Fill the XML-element with the data from the link
    * 
    * @param n
    *           the link to be converted
    * @param res
    *           the element to be filled
    * @param baseUrl
    *           the base url of the copa-install
    */
   private void addLinkToNode(LinkWrapper n, Element res) {
      res.addContent(new Element("number").addContent(new CDATA(""
            + n.getNumber())));
      // res.addContent(new Element("owner").addContent(new
      // CDATA(n.getOwner())));
      res.addContent(new Element("naam").addContent(new CDATA(n.getNaam())));
      res.addContent(new Element("omschrijving").addContent(new CDATA(n
            .getOmschrijving())));
      res.addContent(new Element("notitie")
            .addContent(new CDATA(n.getNotitie())));
      res.addContent(new Element("w_breedte").addContent(new CDATA(""
            + n.getW_breedte())));
      res.addContent(new Element("w_hoogte").addContent(new CDATA(""
            + n.getW_hoogte())));
      res.addContent(new Element("w_scrollable").addContent(new CDATA(""
            + n.getW_scrollable())));
      res.addContent(new Element("notification").addContent(new CDATA(""
            + n.getNotification())));
      res.addContent(new Element("url").addContent(new CDATA(n.getUrl())));
      res.addContent(new Element("nodetype").addContent(new CDATA(
            LinkWrapper.NODETYPE)));
   }

   /**
    * Fill the XML-element with the data from the internal link
    * 
    * @param n
    *           the internal link to be converted
    * @param res
    *           the element to be filled
    * @param baseUrl
    *           the base url of the copa-install
    */
   private void addInternalLinkToNode(InternalLinkWrapper n, Element res,
         String baseUrl) {
      res.addContent(new Element("number").addContent(new CDATA(""
            + n.getNumber())));
      // res.addContent(new Element("owner").addContent(new
      // CDATA(n.getOwner())));
      res.addContent(new Element("naam").addContent(new CDATA(n.getNaam())));
      res.addContent(new Element("omschrijving").addContent(new CDATA(n
            .getOmschrijving())));
      res.addContent(new Element("notitie")
            .addContent(new CDATA(n.getNotitie())));
      res.addContent(new Element("w_breedte").addContent(new CDATA(""
            + n.getW_breedte())));
      res.addContent(new Element("w_hoogte").addContent(new CDATA(""
            + n.getW_hoogte())));
      res.addContent(new Element("w_scrollable").addContent(new CDATA(""
            + n.getW_scrollable())));
      res.addContent(new Element("notification").addContent(new CDATA(""
            + n.getNotification())));
      res.addContent(new Element("popup").addContent(new CDATA(n.getPopup())));
      res.addContent(new Element("nodetype").addContent(new CDATA(
            InternalLinkWrapper.NODETYPE)));

      // Determine download URL
      // TODO Jonathan: use redirect url to copa here?
      res.addContent(new Element("url").addContent(new CDATA("http://"
            + CacheAccessor.getPageUrl(n.getCategorieNumber()))));
   }

   /**
    * Fill the XML-element with the data from the attachment
    * 
    * @param n
    *           the attachment to be converted
    * @param res
    *           the element to be filled
    * @param baseUrl
    *           the base url of the copa-install
    */
   private void addAttachmentToNode(AttachmentWrapper n, Element res,
         String baseUrl) {
      res.addContent(new Element("number").addContent(new CDATA(""
            + n.getNumber())));
      // res.addContent(new Element("owner").addContent(new
      // CDATA(n.getOwner())));
      res.addContent(new Element("title").addContent(new CDATA(n.getTitle())));
      res.addContent(new Element("description").addContent(new CDATA(n
            .getDescription())));
      res.addContent(new Element("notitie")
            .addContent(new CDATA(n.getNotitie())));
      res.addContent(new Element("mimetype").addContent(new CDATA(""
            + n.getMimetype())));
      res.addContent(new Element("filename").addContent(new CDATA(""
            + n.getFilename())));
      res.addContent(new Element("size")
            .addContent(new CDATA("" + n.getSize())));
      res.addContent(new Element("nodetype").addContent(new CDATA(
            AttachmentWrapper.NODETYPE)));

      // Determine download URL
      // TODO Jonathan: use redirect url to copa here?
      res.addContent(new Element("url").addContent(new CDATA(WebserviceHelper.createAttachmentBaseURL() + n.getNumber())));
   }

   /**
    * Construct the URL of the editwizard for the given content element
    * 
    * @param req
    *           the current request
    * @param ce
    *           the CE from the index
    * @return a string containing the edit-wizard URL
    * 
    * protected String createEditWizardURL(HttpServletRequest req, CEResult ce) {
    * return createEditWizardURL(req, "" + ce.getNumber(), ce.getCeWizard()); }
    * 
    * /** Construct the URL of the editwizard for the given content element
    * 
    * @param req
    *           the current request
    * @param n
    *           the cloud-node of the content element
    * @return a string containing the edit-wizard URL
    * 
    * protected String createEditWizardURL(HttpServletRequest req, Node n) {
    * return createEditWizardURL(req, "" + n.getNumber(),
    * nl.kennisnet.cpb.Constants.giveWizardName(n
    * .getStringValue("elementtype"))); }
    * 
    * /** Construct the URL of the editwizard for the given content element
    * 
    * @param req
    *           the current request
    * @param ceId
    *           the ID of the content element
    * @param wizardName
    *           the name of the wizard
    * @return a string containing the edit-wizard URL
    * 
    * protected String createEditWizardURL(HttpServletRequest req, String ceId,
    * String wizardName) {
    * 
    * StringBuffer s = new StringBuffer(150); s.append(createCopaBaseURL(req));
    * s.append("/edit/editwizards/jsp/wizard.jsp?referrer="); try { // TODO
    * Determine which referrer-URL should be used here?
    * s.append(URLEncoder.encode("/edit/editwizards/goPage.jsp", "UTF-8"));
    * s.append("&template=" + URLEncoder.encode("xsl/list.xsl", "UTF-8"));
    * s.append("&sessionkey=" + System.currentTimeMillis()); s.append("&wizard=" +
    * URLEncoder.encode("wizards/" + wizardName + "/" + wizardName, "UTF-8"));
    * s.append("&objectnumber=" + ceId + "&language=nl&proceed=false"); } catch
    * (UnsupportedEncodingException e) { } return s.toString(); }
    */
}

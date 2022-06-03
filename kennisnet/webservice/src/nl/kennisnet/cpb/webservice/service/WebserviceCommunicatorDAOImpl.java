package nl.kennisnet.cpb.webservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.RelationManager;
import org.mmbase.storage.search.CompositeConstraint;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.storage.search.Step;
import org.mmbase.storage.search.StepField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.kennisnet.cpb.cloud.CloudFactory;
import nl.kennisnet.cpb.tree.CacheAccessor;
import nl.kennisnet.cpb.webservice.domain.AttachmentWrapper;
import nl.kennisnet.cpb.webservice.domain.GallerijWrapper;
import nl.kennisnet.cpb.webservice.domain.ImageWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.domain.InternalLinkWrapper;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper;
import nl.kennisnet.cpb.webservice.domain.LinkWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.domain.OrderedWrapper;
import nl.kennisnet.cpb.webservice.domain.PageWrapper;
import nl.kennisnet.cpb.webservice.domain.ParagraphWrapper;
import nl.kennisnet.cpb.webservice.domain.RedactiegroepWrapper;
import nl.kennisnet.cpb.webservice.error.EntryNotFoundException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.lucene.Constants;
import nl.kennisnet.cpb.webservice.util.ImageHelper;
import nl.kennisnet.cpb.webservice.util.ValidationHelper;

/**
 * Provides MMBase cloud read functionality related to kanaal through methods.
 * This class is partly copied from the KanaalCommunicator to ensure that
 * Copa-functionaliteit is unaffected
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class WebserviceCommunicatorDAOImpl implements WebserviceCommunicatorDAO {

   private Logger log = Logging
         .getLoggerInstance(WebserviceCommunicatorDAOImpl.class.getName());

   
   
   /**
    * Cloud reference.
    */
   // TODO Jonathan: use klas security
   protected static Cloud fullAccessCloud = CloudFactory.getFullAccessCloud();

   private final int SECONDS_YEAR = 31556926;
   private final int SECONDS_MONTH = 2629743;
   private final int SECONDS_WEEK = 604800;
   private final int SECONDS_2WEEK = 1209600;
   private final int SECONDS_DAY = 86400;

   public WebserviceCommunicatorDAOImpl() {
      super();
   }

   
   public void delete(NodeWrapper n) {
      Node node = n.getNode();
      if (node!=null) {
         node.delete();
      }
   }

   public void save(NodeWrapper n) {
      Node node = n.getNode();
      if (node!=null) {
         node.commit();
      }
   }


   /**
    * Create a content element as a node in the cloud
    * 
    * @return
    */
   public Node createCENode() {
      return fullAccessCloud.getNodeManager("contentelementen").createNode();
   }

   public AgendaWrapper createAgendaWrapper() {
       Node n = createCENode();
       n.setStringValue("elementtype", "Kanaal");
       return new AgendaWrapper(n,Collections.EMPTY_LIST,Collections.EMPTY_LIST,Collections.EMPTY_LIST);
   }

   public EventWrapper createEventWrapper() {
      Node n = createCENode();
      n.setStringValue("elementtype", "Evenement");
      return new EventWrapper(n, null);
   }

   /*
    * (non-Javadoc)
    * 
    * @see nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO#createLink()
    */
   public Node createLink() {
      return fullAccessCloud.getNodeManager("links").createNode();
   }

   /**
    * Convenience function for retrieve a relation manager by name
    * 
    * @param name
    *           the name of the manager
    * @return the requested relationmanager
    */
   public RelationManager getRelationManagerByName(String name) {
      return fullAccessCloud.getRelationManager(name);
   }

   /**
    * Find the node from the cloud with the specified number
    *
    * @param number
    *           the id of the node
    * @return the node (or NotFoundException if it is not available)
    * @throws WebserviceException
    */
   public Node getNodeById(Integer number) throws WebserviceException {
      try {
         return fullAccessCloud.getNode(number);
         // Set roleNames = new HashSet(1, 1f);
         // roleNames.add(RoleNames.ANONYMOUS);
         // return CloudFactory.getCloud(roleNames, null).getNode(number);
      } catch (NotFoundException nfe) {
         throw new EntryNotFoundException(nfe);
      }
   }
   
   

   public EventWrapper getEventWrapperById(Integer number) throws WebserviceException {
      try {
         Node n = fullAccessCloud.getNode(number);
         return new EventWrapper(n, null);
      } catch (NotFoundException nfe) {
         throw new EntryNotFoundException(nfe);
      }
   }


   /*
    * (non-Javadoc)
    * 
    * @see nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO#getAgendaWrapperByNumber(java.lang.Integer)
    */
   public AgendaWrapper getAgendaWrapperById(Integer number)
         throws WebserviceException {
      try {
         Node n = fullAccessCloud.getNode(number);
         List<NodeWrapper> evenementen = getKanaalContent(n
               .getIntValue("number"), false, (int) Math.floor(System
               .currentTimeMillis() / 1000.0), 0, 10, true,
               FIELDS_IN_CONTENT_XML);

         List <EventWrapper> elist = new ArrayList <EventWrapper> ();
         for (NodeWrapper node: evenementen) {
            elist.add(new EventWrapper(node));
         }
         NodeWrapper nw = retrieveRelationsOfNode(n, false, false, true, true);
         return new AgendaWrapper(nw.getNode(), elist, nw.getPages(), nw
               .getRedactiegroepen());
      } catch (NotFoundException nfe) {
         throw new EntryNotFoundException(nfe);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO#getLangArtikelWrapperByNumber(java.lang.Integer)
    */
   public LangArtikelWrapper getLangArtikelWrapperByNumber(Integer number)
         throws WebserviceException {
      try {
         Node n = fullAccessCloud.getNode(number);
         return retrieveRelationsOfLongArticle(n);
         // Set roleNames = new HashSet(1, 1f);
         // roleNames.add(RoleNames.ANONYMOUS);
         // return CloudFactory.getCloud(roleNames, null).getNode(number);
      } catch (NotFoundException nfe) {
         throw new EntryNotFoundException(nfe);
      }
   }
  
   
   /**
    * Retrieve the content of the kanaal with specified id as a list of nodes
    * (wrapped with their related nodes)
    * 
    * @param kanaalId
    *           the id of the kanaal
    * @param showAll
    *           whether or not to show all kanaal items or just the ones that
    *           fall in a certain period
    * @param viewDate
    *           the date in seconds used in combination with showAll=false to
    *           determine if this date is in the period the items should be
    *           showed
    * @param offset
    *           the offset used in pagination
    * @param max
    *           the maximum number of items
    * @param sortByEnddate
    *           whether to sort ascending on the enddate or descending, true
    *           meaning ascending
    * @param ceFields
    *           the fields of the kanaal itself that should be fetched from the
    *           cloud
    * @return a list of nodes, wrapped together with their related nodes (links,
    *         internal links and attachments)
    */
   public List<NodeWrapper> getKanaalContent(Integer kanaalId, boolean showAll,
         int viewDate, int offset, int max, boolean sortByEnddate,
         String[] ceFields) {
      log.debug(viewDate);
      // All kanalen
      String startNode = Integer.toString(kanaalId);
      String nodePath = "contentelementen,parentrel,contentelementen1";
      String fields = "contentelementen.vrijetekst2,contentelementen.getal1,contentelementen1.number";
      boolean distinct = true;

      String bestBefore = "";
      // set default max items to 10
      if (max == 0) {
         max = 10;
      }

      SortedSet<Integer> set = new TreeSet<Integer>();

      // get the parents of this node
      NodeList nodes = fullAccessCloud.getList(startNode, nodePath, fields,
            null, null, null, null, distinct);
      for (NodeIterator iterator = nodes.nodeIterator(); iterator.hasNext();) {
         Node node = iterator.nextNode();
         set.add(new Integer(node.getIntValue("contentelementen1.number")));
         if ("".equals(bestBefore)) {
            bestBefore = node.getStringValue("contentelementen.vrijetekst2");
            int t = node.getIntValue("contentelementen.getal1");
            if (t > 0) {
               max = t;
            }
         }
      }

      // set.size()==1 check for parents of that parent (may be mix)
      if (set.size() == 1) {
         String mixParent = set.first().toString();
         NodeList nodes2 = fullAccessCloud.getList(mixParent, nodePath, fields,
               null, null, null, null, distinct);
         for (NodeIterator iterator = nodes2.nodeIterator(); iterator.hasNext();) {
            Node node = iterator.nextNode();
            set.add(new Integer(node.getIntValue("contentelementen1.number")));
            if ("".equals(bestBefore)) {
               bestBefore = node.getStringValue("contentelementen.vrijetekst2");
               int t = node.getIntValue("contentelementen.getal1");
               if (t > 0) {
                  max = t;
               }
            }
         }
      }
      set.add(kanaalId);

      // Build query
      RelationManager itemrelManager = fullAccessCloud
            .getRelationManager("itemrel");
      NodeManager ceManager = fullAccessCloud
            .getNodeManager("contentelementen");
      Query q = fullAccessCloud.createQuery();

      Step step1 = q.addStep(ceManager);
      Step step2 = q.addStep(itemrelManager);
      StepField ceNumberField = q.addField(ceManager.getName() + ".number");
      StepField sourceField = q.createStepField(itemrelManager.getName()
            + ".snumber");
      StepField destField = q.createStepField(itemrelManager.getName()
            + ".dnumber");
      StepField endDateField = q
            .addField(itemrelManager.getName() + ".enddate");
      StepField startDateField = q.addField(itemrelManager.getName()
            + ".startdate");

      q.addField(itemrelManager.getName() + ".enddate");
      q.addField(itemrelManager.getName() + ".startdate");

      for (String field : ceFields) {
         q.addField(ceManager.getName() + "." + field);
      }

      // parents & self & join
      Constraint s1 = q.createConstraint(sourceField, set);
      Constraint s2 = q.createConstraint(ceNumberField,
            FieldCompareConstraint.EQUAL, destField);
      Constraint c1 = q.createConstraint(s1, CompositeConstraint.LOGICAL_AND,
            s2);

      Constraint qConst = null;
      if (!showAll) {
         // date
         Constraint c2 = q.createConstraint(endDateField,
               FieldCompareConstraint.NOT_EQUAL, new Integer(0));
         Constraint c3 = q.createConstraint(startDateField,
               FieldCompareConstraint.LESS_EQUAL, new Integer(viewDate));
         Constraint c4 = q.createConstraint(endDateField,
               FieldCompareConstraint.GREATER_EQUAL, new Integer(viewDate));
         // make boolean query
         Constraint b1 = q.createConstraint(c2,
               CompositeConstraint.LOGICAL_AND, c3);
         Constraint b2 = q.createConstraint(b1,
               CompositeConstraint.LOGICAL_AND, c4);

         // calculate best before start date
         int bestBeforeStart = 0;
         if ("year".equalsIgnoreCase(bestBefore)) {
            bestBeforeStart = viewDate - SECONDS_YEAR;
         } else if ("month".equalsIgnoreCase(bestBefore)) {
            bestBeforeStart = viewDate - SECONDS_MONTH;
         } else if ("2 weeks".equalsIgnoreCase(bestBefore)) {
            bestBeforeStart = viewDate - SECONDS_2WEEK;
         } else if ("week".equalsIgnoreCase(bestBefore)) {
            bestBeforeStart = viewDate - SECONDS_WEEK;
         } else if ("day".equalsIgnoreCase(bestBefore)) {
            bestBeforeStart = viewDate - SECONDS_DAY;
         }

         Constraint c5 = q.createConstraint(endDateField,
               FieldCompareConstraint.EQUAL, new Integer(0));
         Constraint c6 = q.createConstraint(startDateField,
               FieldCompareConstraint.GREATER_EQUAL, new Integer(
                     bestBeforeStart));
         Constraint c7 = q.createConstraint(startDateField,
               FieldCompareConstraint.LESS_EQUAL, new Integer(viewDate));
         Constraint b3 = q.createConstraint(c5,
               CompositeConstraint.LOGICAL_AND, c6);
         Constraint b4 = q.createConstraint(b3,
               CompositeConstraint.LOGICAL_AND, c7);
         // made or query with both date posibilities
         Constraint b5 = q.createConstraint(b2, CompositeConstraint.LOGICAL_OR,
               b4);
         qConst = q.createConstraint(c1, CompositeConstraint.LOGICAL_AND, b5);
      } else {
         qConst = c1;
      }
      q.setMaxNumber(max);
      q.setConstraint(qConst);

      if (sortByEnddate) {
         log.debug("sort order ascending");
         q.addSortOrder(startDateField, SortOrder.ORDER_ASCENDING);
      } else {
         q.addSortOrder(startDateField, SortOrder.ORDER_DESCENDING);
      }

      log.debug("setting max = " + max);

      // OOPS max isn't working
      q.setDistinct(true);
      if (offset > 0) {
         q.setOffset(offset);
      }
      log.debug("setting offset = " + offset);
      log.debug(q.toSql());

      // q.setCachePolicy(CachePolicy.ALWAYS);
      NodeList evenementen = fullAccessCloud.getList(q);

      return retrieveRelationsOfNode(evenementen, true, true, false, false);
   }

 
   
   /**
    * Decorate a CE/node with its related nodes and wrap it into a single
    * Nodewrapper
    * 
    * @param node
    *           the node containing the ce
    * @param hasPrefixedFields
    *           whether or not the fieldname of the number of the ce is prefixed
    *           or not
    * @param includeLinksImagesAttachments
    *           whether or not to include the links, attachments and images
    * @param includePages
    *           whether or not to include the pages this ce is placed on
    * @param includeRedactiegroups
    *           whether or not to include the redactiegroups linked to this ce
    * @return a node decorated with its related nodes
    */
   public NodeWrapper retrieveRelationsOfNode(Node node,
         boolean hasPrefixedFields, boolean includeLinksImagesAttachments,
         boolean includePages, boolean includeRedactiegroups) {
      return retrieveRelationsOfNode(Collections.singletonList(node),
            hasPrefixedFields, includeLinksImagesAttachments, includePages,
            includeRedactiegroups).get(0);
   }

   /**
    * Decorate a gallerij-node with its images and wrap it into a single
    * Gallerijwrapper
    * 
    * @param node
    *           the node containing the gallerij
    * @param hasPrefixedFields
    *           whether or not the fieldname of the number of the ce is prefixed
    *           or not
    * @return a node decorated with its related nodes
    */
   public GallerijWrapper retrieveImagesOfNode(Node node,
         boolean hasPrefixedFields) {
      String nrFieldName = "number";
      if (hasPrefixedFields) {
         nrFieldName = "contentelementen.number";
      }

      String startNode = node.getStringValue(nrFieldName);
      Map<Integer, List<ImageWrapper>> map = findRelatedImages(startNode);
      List<ImageWrapper> images = map.get(node.getIntValue(nrFieldName));
      if (images == null) {
         images = Collections.emptyList();
      }

      return new GallerijWrapper(node, images);
   }

   /**
    * Decorates a list of ce's (nodes) with their related nodes and wraps each
    * of them in a Nodewrapper
    *
    * @param nodes
    *           a list of nodes
    * @param hasPrefixedFields
    *           whether or not the fieldname of the number of the ce is prefixed
    *           or not
    * @param includePages
    *           whether or not to include the pages this ce is placed on
    * @param includeRedactiegroups
    *           whether or not to include the redactiegroups linked to this ce
    * @return a list of nodes, decorated with their related nodes
    */
   public List<NodeWrapper> retrieveRelationsOfNode(List<Node> nodes,
         boolean hasPrefixedFields, boolean includeLinksImagesAttachments,
         boolean includePages, boolean includeRedactiegroups) {

      String nrFieldName = "number";
      if (hasPrefixedFields) {
         nrFieldName = "contentelementen.number";
      }

      if (nodes == null || nodes.size() == 0) {
         return Collections.emptyList();
      }
      String startNode = nodes.get(0).getStringValue(nrFieldName);

      if (nodes.size() > 1) {
         for (Node n : nodes.subList(1, nodes.size())) {
            startNode += "," + n.getStringValue(nrFieldName);
         }
      }

      // Performance shortcut, skip links,attachment,images when possible
      Map<Integer, List<OrderedWrapper>> links = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> iLinks = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> attachments = Collections.EMPTY_MAP;
      Map<Integer, List<ImageWrapper>> images = Collections.EMPTY_MAP;
      if (includeLinksImagesAttachments) {
         links = findRelatedLinks(startNode);
         iLinks = findRelatedInternalLinks(startNode);
         attachments = findRelatedAttachments(startNode);
         images = findRelatedImages(startNode);
      }

      // Performance shortcut: skip pages when possible
      Map<Integer, List<PageWrapper>> pages = Collections.EMPTY_MAP;
      if (includePages) {
         pages = findRelatedPages(startNode);
      }

      // Performance shortcut: skip redactiegroups when possible
      Map<Integer, List<RedactiegroepWrapper>> reds = Collections.EMPTY_MAP;
      if (includeRedactiegroups) {
         reds = findRelatedRedactiegroepen(startNode);
      }

      List<NodeWrapper> res = new ArrayList<NodeWrapper>(nodes.size());
      for (Node n : nodes) {
         int nr = n.getIntValue(nrFieldName);
         List<OrderedWrapper> linksList = links.get(nr);
         if (linksList == null) {
            linksList = Collections.emptyList();
         }
         List<OrderedWrapper> iLinksList = iLinks.get(nr);
         if (iLinksList == null) {
            iLinksList = Collections.emptyList();
         }
         List<OrderedWrapper> attachmentsList = attachments.get(nr);
         if (attachmentsList == null) {
            attachmentsList = Collections.emptyList();
         }

         List<ImageWrapper> imageList = images.get(nr);
         ImageWrapper image = null;
         if (imageList != null && imageList.size() > 0) {
            image = imageList.get(0);
         }

         List<PageWrapper> pagesList = pages.get(nr);
         if (pagesList == null) {
            pagesList = Collections.emptyList();
         }

         List<RedactiegroepWrapper> redsList = reds.get(nr);
         if (redsList == null) {
            redsList = Collections.emptyList();
         }

         Node tobeWrapped = n;
         if (hasPrefixedFields) {
            tobeWrapped = n.getNodeValue("contentelementen");
         }

         NodeWrapper k = new NodeWrapper(tobeWrapped, linksList, iLinksList,
               attachmentsList, pagesList, redsList, image);
         res.add(k);
      }

      return res;
   }

   /** Decorates a LangArtikel with its related nodes and wraps it 
    * in a LangArtikelWrapper
    * @param n the cloud node
    * @return  the wrapped LangArtikel
    */
   public LangArtikelWrapper retrieveRelationsOfLongArticle(Node n) {

      String nrFieldName = "number";
      String startNode = n.getStringValue(nrFieldName);

      Map<Integer, List<OrderedWrapper>> links = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> iLinks = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> attachments = Collections.EMPTY_MAP;
      Map<Integer, List<ImageWrapper>> images = Collections.EMPTY_MAP;

      links = findRelatedLinks(startNode);
      iLinks = findRelatedInternalLinks(startNode);
      attachments = findRelatedAttachments(startNode);
      images = findRelatedImages(startNode);

      Map<Integer, List<PageWrapper>> pages = findRelatedPages(startNode);
      Map<Integer, List<RedactiegroepWrapper>> reds = findRelatedRedactiegroepen(startNode);

      int nr = n.getIntValue(nrFieldName);
      List<OrderedWrapper> linksList = links.get(nr);
      if (linksList == null) {
         linksList = Collections.emptyList();
      }
      List<OrderedWrapper> iLinksList = iLinks.get(nr);
      if (iLinksList == null) {
         iLinksList = Collections.emptyList();
      }
      List<OrderedWrapper> attachmentsList = attachments.get(nr);
      if (attachmentsList == null) {
         attachmentsList = Collections.emptyList();
      }

      List<ImageWrapper> imageList = images.get(nr);
      if (imageList == null) {
         imageList = Collections.emptyList();
      }

      ImageWrapper firstImage = null;
      ImageWrapper secondImage = null;      
      for (ImageWrapper iw : imageList) {
         if (iw.getPos() == 0) {
            firstImage = iw;
         } else if (iw.getPos() == 1) {
            secondImage = iw;
         }
      }

      List<PageWrapper> pagesList = pages.get(nr);
      if (pagesList == null) {
         pagesList = Collections.emptyList();
      }

      List<RedactiegroepWrapper> redsList = reds.get(nr);
      if (redsList == null) {
         redsList = Collections.emptyList();
      }

      return new LangArtikelWrapper(n, linksList, iLinksList, attachmentsList,
            pagesList, redsList, firstImage, secondImage,
            findRelatedParagraphs(startNode));
   }
   
   /** Decorates a list of paragraphs with their related nodes and wraps each
    * of them in a Paragraphwrapper
    * @param nodes the list of nodes
    * @param prefix the prefix before the field names
    * @return a list of paragraphs and their dependencies
    */
   public List<ParagraphWrapper> retrieveRelationsOfParagraphs(List<Node> nodes,
         String prefix) {

      String nrFieldName = prefix + ".number";

      if (nodes == null || nodes.size() == 0) {
         return Collections.emptyList();
      }
      String startNode = nodes.get(0).getStringValue(nrFieldName);

      if (nodes.size() > 1) {
         for (Node n : nodes.subList(1, nodes.size())) {
            startNode += "," + n.getStringValue(nrFieldName);
         }
      }

      // Performance shortcut, skip links,attachment,images when possible
      Map<Integer, List<OrderedWrapper>> links = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> iLinks = Collections.EMPTY_MAP;
      Map<Integer, List<OrderedWrapper>> attachments = Collections.EMPTY_MAP;
      Map<Integer, List<ImageWrapper>> images = Collections.EMPTY_MAP;
         links = findRelatedLinks(startNode);
         iLinks = findRelatedInternalLinks(startNode);
         attachments = findRelatedAttachments(startNode);
         images = findRelatedImages(startNode);

      List<ParagraphWrapper> res = new ArrayList<ParagraphWrapper>(nodes.size());
      for (Node n : nodes) {
         int nr = n.getIntValue(nrFieldName);
         List<OrderedWrapper> linksList = links.get(nr);
         if (linksList == null) {
            linksList = Collections.emptyList();
         }
         List<OrderedWrapper> iLinksList = iLinks.get(nr);
         if (iLinksList == null) {
            iLinksList = Collections.emptyList();
         }
         List<OrderedWrapper> attachmentsList = attachments.get(nr);
         if (attachmentsList == null) {
            attachmentsList = Collections.emptyList();
         }

         List<ImageWrapper> imageList = images.get(nr);
         ImageWrapper image = null;
         if (imageList != null && imageList.size() > 0) {
            image = imageList.get(0);
         }
         
         List<OrderedWrapper> relatedItems = new ArrayList<OrderedWrapper> ();
         relatedItems.addAll(linksList);
         relatedItems.addAll(iLinksList);
         relatedItems.addAll(attachmentsList);         
         
         ParagraphWrapper k = new ParagraphWrapper(n.getIntValue("posrel.pos"),n.getNodeValue(prefix),relatedItems, image);
         res.add(k);
      }

      return res;
   }

   /**
    * Retrieve the attachments of a node, given the start of the query
    * containing the node id
    * 
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         attachments grouped per ce-id.
    */
   private Map<Integer, List<OrderedWrapper>> findRelatedAttachments(
         String startNode) {
      final String PREFIX = "attachments.";
      final String NUMBER = PREFIX + "number";
      final String OWNER = PREFIX + "owner";
      final String TITLE = PREFIX + "title";
      final String DESCRIPTION = PREFIX + "description";
      final String NOTITIE = PREFIX + "notitie";
      final String MIMETYPE = PREFIX + "mimetype";
      final String FILENAME = PREFIX + "filename";
      final String SIZE = PREFIX + "size";

      String nodePath = "contentelementen,swaprel,attachments";
      StringBuffer fields = new StringBuffer(
            "contentelementen.number,contentelementen.elementtype,swaprel.pos");
      fields.append("," + NUMBER);
      fields.append("," + OWNER);
      fields.append("," + TITLE);
      fields.append("," + DESCRIPTION);
      fields.append("," + NOTITIE);
      fields.append("," + MIMETYPE);
      fields.append("," + FILENAME);
      fields.append("," + SIZE);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map<Integer, List<OrderedWrapper>> res = new HashMap<Integer, List<OrderedWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<OrderedWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<OrderedWrapper>();
            res.put(number, curList);
         }

         // Create attachments
         AttachmentWrapper l = new AttachmentWrapper(n
               .getIntValue("swaprel.pos"));
         l.setNumber(n.getIntValue(NUMBER));
         l.setOwner(n.getStringValue(OWNER));
         l.setTitle(n.getStringValue(TITLE));
         l.setDescription(n.getStringValue(DESCRIPTION));
         l.setNotitie(n.getStringValue(NOTITIE));
         l.setMimetype(n.getStringValue(MIMETYPE));
         l.setFilename(n.getStringValue(FILENAME));
         l.setSize(n.getIntValue(SIZE));

         curList.add(l);
      }

      return res;
   }

   /**
    * Retrieve the internal links of a node, given the start of the query
    * containing the node id
    * 
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         internal links grouped per ce-id.
    */
   private Map<Integer, List<OrderedWrapper>> findRelatedInternalLinks(
         String startNode) {
      final String PREFIX = "internelinks.";
      final String NUMBER = PREFIX + "number";
      final String OWNER = PREFIX + "owner";
      final String NAAM = PREFIX + "naam";
      final String OMSCHRIJVING = PREFIX + "omschrijving";
      final String NOTITIE = PREFIX + "notitie";
      final String W_BREEDTE = PREFIX + "w_breedte";
      final String W_HOOGTE = PREFIX + "w_hoogte";
      final String W_SCROLLABLE = PREFIX + "w_scrollable";
      final String NOTIFICATION = PREFIX + "notification";
      final String POPUP = PREFIX + "popup";
      final String CATEGORIE_NR = "categorieen.number";

      String nodePath = "contentelementen,swaprel,internelinks,linksto,categorieen";
      StringBuffer fields = new StringBuffer(
            "contentelementen.number,contentelementen.elementtype,swaprel.pos");
      fields.append("," + NUMBER);
      fields.append("," + OWNER);
      fields.append("," + NAAM);
      fields.append("," + OMSCHRIJVING);
      fields.append("," + NOTITIE);
      fields.append("," + W_BREEDTE);
      fields.append("," + W_HOOGTE);
      fields.append("," + W_SCROLLABLE);
      fields.append("," + NOTIFICATION);
      fields.append("," + POPUP);
      fields.append("," + CATEGORIE_NR);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map<Integer, List<OrderedWrapper>> res = new HashMap<Integer, List<OrderedWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<OrderedWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<OrderedWrapper>();
            res.put(number, curList);
         }

         // Create internal link
         InternalLinkWrapper l = new InternalLinkWrapper(n
               .getIntValue("swaprel.pos"));
         l.setNumber(n.getIntValue(NUMBER));
         l.setOwner(n.getStringValue(OWNER));
         l.setNaam(n.getStringValue(NAAM));
         l.setOmschrijving(n.getStringValue(OMSCHRIJVING));
         l.setNotitie(n.getStringValue(NOTITIE));
         l.setW_breedte(n.getIntValue(W_BREEDTE));
         l.setW_hoogte(n.getIntValue(W_HOOGTE));
         l.setW_scrollable(n.getIntValue(W_SCROLLABLE));
         l.setNotification(n.getIntValue(NOTIFICATION));
         l.setPopup(n.getStringValue(POPUP));
         l.setCategorieNumber(n.getIntValue(CATEGORIE_NR));

         curList.add(l);
      }

      return res;
   }

   /**
    * Retrieve the links of a node, given the start of the query containing the
    * node id
    * 
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         links grouped per ce-id.
    */
   private Map<Integer, List<OrderedWrapper>> findRelatedLinks(String startNode) {
      final String PREFIX = "links.";
      final String NUMBER = PREFIX + "number";
      final String OWNER = PREFIX + "owner";
      final String NAAM = PREFIX + "naam";
      final String OMSCHRIJVING = PREFIX + "omschrijving";
      final String NOTITIE = PREFIX + "notitie";
      final String W_BREEDTE = PREFIX + "w_breedte";
      final String W_HOOGTE = PREFIX + "w_hoogte";
      final String W_SCROLLABLE = PREFIX + "w_scrollable";
      final String NOTIFICATION = PREFIX + "notification";
      final String URL = PREFIX + "url";

      String nodePath = "contentelementen,swaprel,links";
      StringBuffer fields = new StringBuffer(
            "contentelementen.number,contentelementen.elementtype,swaprel.pos");
      fields.append("," + NUMBER);
      fields.append("," + OWNER);
      fields.append("," + NAAM);
      fields.append("," + OMSCHRIJVING);
      fields.append("," + NOTITIE);
      fields.append("," + W_BREEDTE);
      fields.append("," + W_HOOGTE);
      fields.append("," + W_SCROLLABLE);
      fields.append("," + NOTIFICATION);
      fields.append("," + URL);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map<Integer, List<OrderedWrapper>> res = new HashMap<Integer, List<OrderedWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<OrderedWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<OrderedWrapper>();
            res.put(number, curList);
         }

         // Create link
         LinkWrapper l = new LinkWrapper(n.getIntValue("swaprel.pos"));
         l.setNumber(n.getIntValue(NUMBER));
         l.setOwner(n.getStringValue(OWNER));
         l.setNaam(n.getStringValue(NAAM));
         l.setOmschrijving(n.getStringValue(OMSCHRIJVING));
         l.setNotitie(n.getStringValue(NOTITIE));
         l.setW_breedte(n.getIntValue(W_BREEDTE));
         l.setW_hoogte(n.getIntValue(W_HOOGTE));
         l.setW_scrollable(n.getIntValue(W_SCROLLABLE));
         l.setNotification(n.getIntValue(NOTIFICATION));
         l.setUrl(n.getStringValue(URL));

         // Add link to current List
         curList.add(l);
      }

      return res;
   }

   /**
    * Retrieve the pages where this node is present, given the start of the
    * query containing the node id
    *
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         pages grouped per ce-id.
    */
   private Map<Integer, List<PageWrapper>> findRelatedPages(String startNode) {
      final String CATEGORIE_NR = "categorieen.number";

      String nodePath = "contentelementen,ceposrel,categorieen";
      StringBuffer fields = new StringBuffer(
            "contentelementen.number,ceposrel.pos," + CATEGORIE_NR);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map <Integer, List < PageWrapper > > res
         = new HashMap<Integer, List<PageWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<PageWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<PageWrapper>();
            res.put(number, curList);
         }

         // Create page
         PageWrapper p = new PageWrapper(n.getIntValue("ceposrel.pos"));
         p.setCategorieId(n.getIntValue(CATEGORIE_NR));
         p.setUrl("http://"
               + CacheAccessor.getPageUrl(n.getIntValue(CATEGORIE_NR)));

         curList.add(p);
      }

      return res;
   }

   /**
    * Retrieve the links of a node, given the start of the query containing the
    * node id
    * 
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         links grouped per ce-id.
    */
   private Map<Integer, List<ImageWrapper>> findRelatedImages(String startNode) {
      final String PREFIX = "images.";
      final String NUMBER = PREFIX + "number";
      final String OWNER = PREFIX + "owner";
      final String TITLE = PREFIX + "title";
      final String DESCRIPTION = PREFIX + "description";
      final String ITYPE = PREFIX + "itype";
      final String POPUP = PREFIX + "popup";
      final String NOTITIE = PREFIX + "notitie";
      final String THUMBNAIL = PREFIX + "thumbnail";
      final String HANDLE = PREFIX + "handle";
      // final String WIDTH = PREFIX + "width";
      // final String HEIGHT = PREFIX + "height";

      String nodePath = "contentelementen,posrel,images";
      StringBuffer fields = new StringBuffer(
            "contentelementen.number,contentelementen.elementtype,posrel.pos");
      fields.append("," + NUMBER);
      fields.append("," + OWNER);
      fields.append("," + TITLE);
      fields.append("," + DESCRIPTION);
      fields.append("," + ITYPE);
      fields.append("," + POPUP);
      fields.append("," + NOTITIE);
      fields.append("," + THUMBNAIL);
      fields.append("," + HANDLE);
      // fields.append("," + WIDTH);
      // fields.append("," + HEIGHT);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map<Integer, List<ImageWrapper>> res = new HashMap<Integer, List<ImageWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<ImageWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<ImageWrapper>();
            res.put(number, curList);
         }

         // Create image
         ImageWrapper i = new ImageWrapper(n.getIntValue("posrel.pos"));
         i.setNumber(n.getIntValue(NUMBER));
         i.setOwner(n.getStringValue(OWNER));
         i.setTitle(n.getStringValue(TITLE));
         i.setDescription(n.getStringValue(DESCRIPTION));
         i.setItype(n.getStringValue(ITYPE));
         i.setPopup(n.getIntValue(POPUP));
         i.setNotitie(n.getStringValue(NOTITIE));
         i.setThumbnail(n.getIntValue(THUMBNAIL));

         // Compute the width
         ImageHelper help = new ImageHelper();
         i.setWidth(help.getImageWidth(n.getByteValue(HANDLE)));
         i.setHeight(help.getImageHeight(n.getByteValue(HANDLE)));
         curList.add(i);
      }
      return res;
   }

   /**
    * Find the paragraphs corresponding to a long article
    *
    * @param startNode
    *           the first part of the query
    * @return an ordered list of paragraphs
    */
   private List<ParagraphWrapper> findRelatedParagraphs(String startNode) {
      String nodePath = "contentelementen,posrel,contentelementen1";
      StringBuffer fields = new StringBuffer(
            "posrel.pos");
      //contentelementen1.number as number   ,contentelementen1.titel,contentelementen1.toontitel,contentelementen1.body,contentelementen1.notitie,contentelementen1.elementtype
      for (String f : FIELDS_IN_CONTENT_XML) {
         fields.append(",contentelementen1." + f);
      }

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);
      return retrieveRelationsOfParagraphs(nl, "contentelementen1");      
   }

   /**
    * Retrieve the redactiegroups of a node, given the start of the query
    * containing the node id
    *
    * @param startNode
    *           the first part of the query
    * @return a map of (ce-id, list of related nodes), containing the list of
    *         redactiegroups grouped per ce-id.
    */
   private Map<Integer, List<RedactiegroepWrapper>> findRelatedRedactiegroepen(
         String startNode) {
      final String REDACTIECODE = "redactiegroepen.code";

      String nodePath = "contentelementen,authorrel,redactiegroepen";
      StringBuffer fields = new StringBuffer("contentelementen.number,"
            + REDACTIECODE);

      List<Node> nl = fullAccessCloud.getList(startNode, nodePath, fields
            .toString(), null, null, null, null, true);

      Map<Integer, List<RedactiegroepWrapper>> res
         = new HashMap<Integer, List<RedactiegroepWrapper>>(
            nl.size());
      for (Node n : nl) {

         // Find current list
         int number = n.getIntValue("contentelementen.number");
         List<RedactiegroepWrapper> curList = res.get(number);
         if (curList == null) {
            curList = new LinkedList<RedactiegroepWrapper>();
            res.put(number, curList);
         }

         // Create redactiegroep
         RedactiegroepWrapper r =
            new RedactiegroepWrapper(n.getStringValue(REDACTIECODE));
         curList.add(r);
      }

      return res;
   }

   /**
    * Find the redactiegroep on their unique code/name
    * 
    * @param code
    *           the unique code of the redactiegroep
    * @return the redactiegroep node, or null if it cannot be found
    */
   public Node findRedactieGroepByCode(String code) {
      String fields = "redactiegroepen.number";
      String nodePath = "redactiegroepen";

      if (ValidationHelper.validateToken(code)) {
         String constraints = "code = '" + code + "'";

         List<Node> nl = fullAccessCloud.getList(null, nodePath, fields,
               constraints, null, null, null, true);

         Node res = null;
         if (nl != null && nl.size() > 0) {
            res = nl.get(0);
         }

         return res;
      }
      return null;
   }
   
   /**
    * Find the redactiegroep on a search term with wildcards, i.e. ws* becomes like 'ws%' 
    * 
    * @param code
    *           the unique code of the redactiegroep
    * @return the redactiegroep node, or null if it cannot be found
    */
   public List<Node> findRedactieGroepByLike(String code) {
      String fields = "redactiegroepen.number";
      String nodePath = "redactiegroepen";      

      if (ValidationHelper.validateToken(code)) {
         
         code = code.replace('*', '%');
         String constraints = "code like '" + code + "'";         
         List<Node> nl = fullAccessCloud.getList(null, nodePath, fields,
               constraints, null, null, null, true);

         return nl;
      }
      return Collections.emptyList();
   }

   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO#saveImage(byte[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   public int saveImage(byte[] file, String title, String alt, String tags, String redactiegroup) {
      Node n = fullAccessCloud.getNodeManager("images").createNode();
      n.setByteValue("handle", file);
      n.setStringValue("title", title);
      n.setStringValue("description", alt);
      
      //Images don't have a redactiegroup associated with them. For Vakcommunities, we do need to connect 
      //a redactiegroup to an image, so we store it using this marker in the redactienotitie.
      n.setStringValue("notitie", tags + " " + Constants.IMAGE_REDACTIEGROEP_MARKER_PREFIX + redactiegroup);      
      n.setStringValue("popup", "0"); //Seems to be the standard value for this field      
      n.commit();
      
      if (log.isDebugEnabled()) {
         log.debug("Image created with number " + n.getNumber());
      }
      return n.getNumber();
   }


   public int saveAttachment(byte[] file, String title, String filename) {
      Node n = fullAccessCloud.getNodeManager("attachments").createNode();
      n.setByteValue("handle", file);
      n.setStringValue("title", title);
      n.setStringValue("filename", filename);
      n.commit();
      
      if (log.isDebugEnabled()) {
         log.debug("Attachment created with number " + n.getNumber());
      }
      return n.getNumber();
   }
   
   
   

}

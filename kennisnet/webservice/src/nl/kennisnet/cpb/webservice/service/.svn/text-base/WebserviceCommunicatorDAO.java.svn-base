package nl.kennisnet.cpb.webservice.service;

import java.util.List;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.RelationManager;

import nl.kennisnet.cpb.webservice.domain.GallerijWrapper;
import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.LangArtikelWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.error.WebserviceException;

public interface WebserviceCommunicatorDAO {
   
   public final static String[] FIELDS_IN_CONTENT_XML = { "number", "titel", "intro", "body", "datum1", "datum2", "elementtype",
      "notitie", "toontitel", "vrijetekst1", "vrijetekst2", "vrijetekst3",
      "vrijetekst4", "vrijetekst5", "boolean1", "boolean2", "getal1",
      "getal2", "vrijetekst6" };
   
   /**
    * Create a content element as a node in the cloud
    * 
    * @return the newly created ce
    */
   public Node createCENode();
   
   /** Create a link as a node in the cloud
    * @return the newly created link
    */
   public Node createLink();

   
   public AgendaWrapper createAgendaWrapper(); 
   
   
   public EventWrapper createEventWrapper();
   

   public void save(NodeWrapper n);
   
   
   public void delete(NodeWrapper aw);
   
   /**
    * Convenience function for retrieve a relation manager by name
    * 
    * @param name
    *           the name of the manager
    * @return the requested relationmanager
    */
   public RelationManager getRelationManagerByName(String name);

   /**
    * Find the node from the cloud with the specified number
    * 
    * @param number
    *           the id of the node
    * @return the node (or NotFoundException if it is not available)
    * @throws WebserviceException
    */
   public Node getNodeById(Integer number) throws WebserviceException;
   
   /** Retrieves an agenda and its related evenementen
    * @param number the id of the agenda
    * @return the agenda and its related nodes
    * @throws WebserviceException if the agenda cannot be found
    */
   public AgendaWrapper getAgendaWrapperById(Integer number) throws WebserviceException;

   /** Retrieves a single event
    * @param number the id of the event
    * @return the event and its related nodes
    * @throws WebserviceException if the agenda cannot be found
    */
   public EventWrapper getEventWrapperById(Integer number) throws WebserviceException;

   
   
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
         String[] ceFields);

   /**
    * Decorate a CE/node with its related nodes and wrap it into a single
    * Nodewrapper
    * 
    * @param node
    *           the node containing the ce
    * @param hasPrefixedFields
    *           whether or not the fieldname of the number of the ce is prefixed
    *           or not
    * @param includeRedactiegroups whether or not to include the redactiegroups linked to this ce          
    * @param includePages
    *           whether or not to fetch the pages this node is included in as well
    * @param includeRedactiegroups whether or not to include the redactiegroups as well          
    * @return a node decorated with its related nodes
    */
   public NodeWrapper retrieveRelationsOfNode(Node node,
         boolean hasPrefixedFields, boolean includeLinksImagesAttachments, boolean includePages, boolean includeRedactiegroups);

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
         boolean hasPrefixedFields);

   /**
    * Decorates a list of ce's (nodes) with their related nodes and wraps each
    * of them in a Nodewrapper
    * 
    * @param nodes
    *           a list of nodes
    * @param hasPrefixedFields
    *           whether or not the fieldname of the number of the ce is prefixed
    *           or not
    *  @param includeRedactiegroups whether or not to include the redactiegroups linked to this ce         
    *  @param includePages
    *           whether or not to fetch the pages this node is included in as well
    *  @param includeRedactiegroups whether or not to include the redactiegroups linked to this ce
    * @return a list of nodes, decorated with their related nodes
    */
   public List<NodeWrapper> retrieveRelationsOfNode(List<Node> nodes,
         boolean hasPrefixedFields, boolean includeLinksImagesAttachments, boolean includePages, boolean includeRedactiegroups);

   /**
    * Find the redactiegroep on their unique code/name
    * 
    * @param code
    *           the unique code of the redactiegroep
    * @return the redactiegroep node, or null if it cannot be found
    */
   public Node findRedactieGroepByCode(String code);
   
   /**
    * Find the redactiegroep on a search term with wildcards, i.e. ws* becomes like 'ws%' 
    * 
    * @param code
    *           the unique code of the redactiegroep
    * @return the redactiegroep node, or null if it cannot be found
    */
   public List<Node> findRedactieGroepByLike(String code);

   /** Retrieves a long article and its related paragraphs
    * @param number the id of the long article
    * @return the long article and its related nodes
    * @throws WebserviceException if the long article cannot be found
    */
   public LangArtikelWrapper getLangArtikelWrapperByNumber(Integer number) throws WebserviceException;

   /** Save an image using the specified fields
    * @param file the binary data of the image
    * @param title the title of the image
    * @param alt the alt text of the image
    * @param tags the search tags of the image
    * @param redactiegroup the redactiegroup of the image (used on in vakcommunities)
    * @return the number of the newly stored image in the cloud
    */
   public int saveImage(byte[] file, String title, String alt, String tags, String redactiegroup);

   /** Save an attachment using the specified fields
    * @param file the binary data of the file
    * @param title the title of the attachment
    * @param filename the filename of the attachment
    * @return the number of the newly stored attachment in the cloud
    */
   public int saveAttachment(byte[] file, String title, String filename);
}

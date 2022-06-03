/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mmbase.bridge.Node;

/**
 * A wrapper for a node and its related (swaprel) items.
 * This includes links, internal links, attachments and its redactiegroups. 
 * The image or images are included within this wrapper.
 * Also used for items in a kanaal. 
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class NodeWrapper {

   private String id;
  
   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getType() {
      return getNode().getStringValue("elementtype");
   }

   public String getNotitie() {
      return getNode().getStringValue("notitie");
   }

   public void setNotitie(String notitie) {
      getNode().setStringValue("notitie", notitie);
   }

   public String getTitel() {
      return getNode().getStringValue("titel");
   }

   public void setTitel(String titel) {
      getNode().setStringValue("titel", titel);
   }

   public String getIntro() {
      return getNode().getStringValue("intro");
   }

   public void setIntro(String intro) {
      getNode().setStringValue("intro", intro);
   }

   /**
    * The base content element wrapped here.
    */
   private Node node;
   /**
    * Ordered list of links, internal links and attachments related to base content element. Can be empty.
    */
   private List<OrderedWrapper> relatedItems;
   
   /**
    * Ordered list of pages this ce has been added to.
    */
   private List<PageWrapper> pages;
   
   /**
    * Unordered list of redactiegroups owning this ce.
    */
   private List<RedactiegroepWrapper> redactiegroepen;
   /**
    * Optional image
    */
   private ImageWrapper image;

   public NodeWrapper(Node n) {
      this(n, null, null, null, null, null, null);
   }

   public NodeWrapper(Node n, List<OrderedWrapper> links, List<OrderedWrapper> iLinks, List<OrderedWrapper> attachments, List<PageWrapper> pages, List<RedactiegroepWrapper> redactiegroepen, ImageWrapper image) {
      node = n;
      relatedItems = new ArrayList<OrderedWrapper> ();
      if (links != null) {
         relatedItems.addAll(links);
      }
      if (iLinks != null) {
         relatedItems.addAll(iLinks);
      }
      if (attachments != null) {
         relatedItems.addAll(attachments);
      }
      if (relatedItems != null) {
         Collections.sort(relatedItems);
      }
      if (pages != null) {
         Collections.sort(pages);
      }
      this.pages = pages;
      this.redactiegroepen = redactiegroepen;
      this.image = image;
   }

   
   public String getBody() {
      return getNode().getStringValue("body");
   }

   public void setBody(String body) {
      getNode().setStringValue("body", body);
   }

   public int getNumber() {
      return node.getNumber();
   }

   
   public Node getNode() {
      return node;
   }

   public List<OrderedWrapper> getRelatedItems() {
      return relatedItems;
   }

   public void setRelatedItems(List<OrderedWrapper> related) {
      this.relatedItems = related;
      Collections.sort(this.relatedItems);
   }

   public ImageWrapper getImage() {
      return image;
   }

   public List<PageWrapper> getPages() {
      return pages;
   }

   public List<RedactiegroepWrapper> getRedactiegroepen() {
      return redactiegroepen;
   }
}

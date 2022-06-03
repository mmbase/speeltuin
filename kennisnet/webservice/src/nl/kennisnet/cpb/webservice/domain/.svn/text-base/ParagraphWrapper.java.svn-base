/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;

import java.util.Collections;
import java.util.List;

import org.mmbase.bridge.Node;

/**
 * A wrapper for a paragraph that is nested inside a long article
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class ParagraphWrapper extends OrderedWrapper{
   
   private Node node;
   
   /**
    * Ordered list of links, internal links and attachments related to base content element. Can be empty.
    */
   private List<OrderedWrapper> relatedItems;
   
   /**
    * Optional image
    */
   private ImageWrapper image;

   public ParagraphWrapper(int pos, Node n, List<OrderedWrapper> relatedItems,ImageWrapper image) {
      super(pos);
      this.number = n.getIntValue("number");
      this.node = n;
      this.relatedItems = relatedItems;
      Collections.sort(relatedItems);
      this.image = image;
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


   public Node getNode() {
      return node;
   }
}

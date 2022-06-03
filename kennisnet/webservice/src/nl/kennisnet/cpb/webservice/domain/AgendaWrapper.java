/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;

import java.util.List;

import org.mmbase.bridge.Node;


/**
 * A wrapper for a kanaal. Contains a list of kanaal items (evenementen,
 * artikelen, attenderingen) with their related nodes
 *
 * @author dekok01
 * @version $Revision: 1.0
 */
public class AgendaWrapper extends NodeWrapper {

   private List<EventWrapper> items;

   public AgendaWrapper(Node node, List<EventWrapper> items, List<PageWrapper> pages, List<RedactiegroepWrapper> redactiegroepen) {
      super(node, null, null, null , pages, redactiegroepen, null );
      this.items = items;
   }

   public List <EventWrapper> getItems() {
      return items;
   }

   public void setItems(List<EventWrapper> items) {
      this.items = items;
   }
}

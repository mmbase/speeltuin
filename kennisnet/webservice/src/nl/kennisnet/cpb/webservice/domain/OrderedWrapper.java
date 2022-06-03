package nl.kennisnet.cpb.webservice.domain;

/**
 * Superclass for related nodes connected to a content element, used in the
 * webservice. The primary function of this superclass is to provide a single
 * related node type and to ensure sortability of the related nodes (so the
 * order is still preserved in the webservice).
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public abstract class OrderedWrapper implements
      Comparable<OrderedWrapper> {

   private int pos;
   protected Integer number;

   public OrderedWrapper(int pos) {
      super();
      this.pos = pos;
   }

   public int compareTo(OrderedWrapper rnw) {
      return this.getPos() - rnw.getPos();
   }

   public int getPos() {
      return pos;
   }

   public void setPos(int pos) {
      this.pos = pos;
   }
   
   public Integer getNumber() {
      return number;
   }
   public void setNumber(Integer number) {
      this.number = number;
   }
}

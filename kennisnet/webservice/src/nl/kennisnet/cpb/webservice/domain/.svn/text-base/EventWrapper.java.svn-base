package nl.kennisnet.cpb.webservice.domain;

import org.mmbase.bridge.Node;

public class EventWrapper extends NodeWrapper {

   private String parentId;

   public EventWrapper(Node n, String parentId) {
      super(n);
      this.parentId = parentId;
   }
   
   public EventWrapper(NodeWrapper wrap) {
      super(wrap.getNode(), null, null, null, wrap.getPages(), wrap.getRedactiegroepen(), null);   
   }
   
   public String getTitel() {
      return getNode().getStringValue("titel");
   }

   public void setTitel(String titel) {
      getNode().setStringValue("titel", titel);
   }

   public String getParentId() {
      return parentId;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   public int getEindDatum() {
      return getNode().getIntValue("datum2");
   }

   public void setEindDatum(int einddatum) {
      getNode().setIntValue("datum2", einddatum);
   }

   public int getStartDatum() {
      return getNode().getIntValue("datum1");
   }

   public void setStartDatum(int startdatum) {
      getNode().setIntValue("datum1", startdatum);
   }
   
   public String getEmail() {
      return getNode().getStringValue("vrijetekst3");
   }

   public void setEmail(String email) {
      getNode().setStringValue("vrijetekst3", email);
   }

   public String getLokatie() {
      return getNode().getStringValue("vrijetekst1");
   }

   public void setLokatie(String lokatie) {
      getNode().setStringValue("vrijetekst1", lokatie);
   }

   public String getTelefoonnummer() {
      return getNode().getStringValue("vrijetekst2");
   }

   public void setTelefoonnummer(String telefoonnummer) {
      getNode().setStringValue("vrijetekst2", telefoonnummer);
   }

}

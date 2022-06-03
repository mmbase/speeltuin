package nl.kennisnet.cpb.webservice.service;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

import org.jdom.Element;

import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
import nl.kennisnet.cpb.webservice.domain.LinkWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.domain.OrderedWrapper;
import nl.kennisnet.cpb.webservice.error.BadRequestException;
import nl.kennisnet.cpb.webservice.error.EntryNotFoundException;
import nl.kennisnet.cpb.webservice.error.UnexpectedNodeStateException;
import nl.kennisnet.cpb.webservice.error.WebserviceException;

/**
 * Service for node management. This includes handing the creation process of
 * new cloud-nodes (setting their fields), updating certain fields of the node
 * and deleting nodes.
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class ContentElementManagementServiceImpl implements ContentElementManagementService {
   
   private WebserviceCommunicatorDAO dao = new WebserviceCommunicatorDAOImpl();
      

   public void deleteAgendaById(int id) throws WebserviceException {
      AgendaWrapper aw = dao.getAgendaWrapperById(id);
      dao.delete(aw);
   }

   public void deleteEventById(int id) throws WebserviceException {
      EventWrapper aw = dao.getEventWrapperById(id);
      dao.delete(aw);
   }

   public void save(EventWrapper aw) throws WebserviceException {
      dao.save(aw);
   }

   public void save(AgendaWrapper aw) throws WebserviceException {
      dao.save(aw);
   }

   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementManagementService#updateTekstblok(org.mmbase.bridge.Node, org.jdom.Element)
    */
   public void updateTekstblok(NodeWrapper nw, Element ce) throws WebserviceException {
      Node n = nw.getNode();
      
      //Check mandatory input
      String titel = ce.getChildText("titel");
      if (titel == null || "".equals(titel)) {
         throw new BadRequestException("Missing content element field titel");
      }
      //Update mandatory input
      n.setStringValue("titel", titel);
      //Update optional input
      updateTextField(n,"body",ce.getChildText("body"));
      updateTextField(n,"notitie",ce.getChildText("notitie"));
      updateBooleanField(n,"toontitel",ce.getChildText("toontitel"));
      updateBooleanField(n,"boolean2",ce.getChildText("boolean2"));
      n.commit();
   }


   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementManagementService#updateEvenement(org.mmbase.bridge.Node, org.jdom.Element)
    */
   public void updateEvent(int id, EventWrapper data) throws WebserviceException {
      EventWrapper aw = dao.getEventWrapperById(id);
      if (aw != null) {
         if (data.getBody() != null)  aw.setBody(data.getBody());
         if (data.getIntro() != null)  aw.setIntro(data.getIntro());
         if (data.getNotitie() != null) aw.setNotitie(data.getNotitie());
         if (data.getTitel() != null)  aw.setTitel(data.getTitel());
         if (data.getEindDatum() > 0) aw.setEindDatum(data.getEindDatum());
         if (data.getStartDatum() > 0) aw.setStartDatum(data.getStartDatum());
         if (data.getEmail() != null) aw.setEmail(data.getEmail());
         if (data.getLokatie() != null) aw.setLokatie(data.getLokatie());
         if (data.getTelefoonnummer() != null) aw.setParentId(data.getTelefoonnummer());
      }

      if (aw.getTitel() == null || "".equals(aw.getTitel())) {
         throw new BadRequestException("Missing content element field titel");
      }
      save(aw);
   }

   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementManagementService#updateAgenda(nl.kennisnet.cpb.webservice.domain.KanaalWrapper, org.jdom.Element)
    */
   public void updateAgenda(int id, AgendaWrapper data)
         throws WebserviceException {

      AgendaWrapper aw = dao.getAgendaWrapperById(id);
      if (aw != null) {
         if (data.getBody() != null)  aw.setBody(data.getBody());
         if (data.getIntro() != null)  aw.setIntro(data.getIntro());
         if (data.getNotitie() != null) aw.setNotitie(data.getNotitie());
         if (data.getTitel() != null)  aw.setTitel(data.getTitel());
      }
      if (aw.getTitel() == null || "".equals(aw.getTitel())) {
         throw new BadRequestException("Missing content element field titel");
      }
      save(aw);
   }

   private void addRedactieGroup(NodeWrapper nw, String redactieGroep)
      throws Exception {
      Node n = nw.getNode();
      if (n != null) {
         Node redacNode = dao.findRedactieGroepByCode(redactieGroep);
         if (redacNode == null) {
            throw new EntryNotFoundException("The redactiegroep "
                  + redactieGroep + " could not be located");
         }
         redacNode = redacNode.getNodeValue("redactiegroepen");
         Relation r = n.createRelation(redacNode,
               dao.getRelationManagerByName("authorrel"));
         r.commit();
      }
   }


   /**
    * Updates or creates an information link for an evenement. Based on the
    * current requirements, an evenement has a maximum of single link. (no
    * attachments, no internal link)
    *
    * @param nw
    * @param ce
    * @throws WebserviceException
    */
   private void updateInformationLink(NodeWrapper nw, Element ce)
         throws WebserviceException {
      Node n = nw.getNode();
      // Update/add informatielink
      List<OrderedWrapper> relatedNodes = nw.getRelatedItems();
      Element related = ce.getChild("related");
      Element item = null;
      if (related != null && (item = related.getChild("item")) != null) {
         if (relatedNodes == null || relatedNodes.size() == 0) {
            // Assumption: no links/attachments should be reused. We just
            // create a new list to overwrite the old one

            // Create node and linkwrapper
            LinkWrapper lw = new LinkWrapper(1);
            lw.setW_breedte(0);
            lw.setW_hoogte(0);
            lw.setW_scrollable(0);
            lw.setNotification(1);
            Node newLinkNode = dao.createLink();
            // Sensible defaults
            newLinkNode.setIntValue("w_breedte", 0);
            newLinkNode.setIntValue("w_hoogte", 0);
            newLinkNode.setIntValue("w_scrollable", 0);
            newLinkNode.setIntValue("notification", 1);
            updateLink(lw, newLinkNode, item);
            newLinkNode.commit();

            lw.setNumber(newLinkNode.getIntValue("number"));

            // Perform administration of new relation
            Relation r = n.createRelation(newLinkNode, dao
                  .getRelationManagerByName("swaprel"));
            r.setIntValue("pos", 1);
            r.commit();
            List<OrderedWrapper> newRelatedNodes = new ArrayList<OrderedWrapper>(
                  1);
            newRelatedNodes.add(lw);
            nw.setRelatedItems(newRelatedNodes);
         } else {
            // Assumption: only links have been added to an evenement that we
            // can modify. In fact, one link at most.
            OrderedWrapper rnw = relatedNodes.get(0);
            if (rnw == null || !(rnw instanceof LinkWrapper)) {
               throw new UnexpectedNodeStateException(
                     "The related node of this evenement must be a (single) link");
            }
            LinkWrapper lw = (LinkWrapper) rnw;
            Node linkNode = dao.getNodeById(lw.getNumber());
            updateLink(lw, linkNode, item);
            linkNode.commit();
         }
      } else {
         if (relatedNodes != null && relatedNodes.size() == 1) {
            OrderedWrapper rnw = relatedNodes.get(0);
            if (rnw == null || !(rnw instanceof LinkWrapper)) {
               throw new UnexpectedNodeStateException(
                     "The related node of this evenement must be a (single) link");
            }
            removeLinkFromEvent(n,((LinkWrapper) rnw).getNumber());
         }
      }
   }

   /**
    * Set the redactiegroep of the newly created node
    * 
    * @param n
    *           the new node
    * @param ce
    *           the redactiegroep containing the ce.
    */   
   public void assignNodeToRedactiegroep(Node n, Element ce) throws WebserviceException{
      String redactieGroep = null;
      Element reds = ce.getChild("redactiegroepen");
      if (reds != null) {
         List<Element> redsList =  reds.getChildren("redactiegroep");
         
         if (redsList != null && redsList.size() > 0) {
            redactieGroep = (redsList.get(0).getText());     
         }
      }      
      
      if (redactieGroep == null || "".equals(redactieGroep)) {
         throw new BadRequestException("Missing content element field redactiegroep");
      }            
      Node redacNode = dao.findRedactieGroepByCode(redactieGroep);
      if (redacNode == null) {
         throw new EntryNotFoundException("The redactiegroep " + redactieGroep + " could not be located");
      }
      redacNode = redacNode.getNodeValue("redactiegroepen");
      Relation r = n.createRelation(redacNode, dao.getRelationManagerByName("authorrel"));
      r.commit();     
   }  
   
   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementManagementService#assignNodeToAgenda(org.mmbase.bridge.Node, java.lang.Integer, java.lang.String)
    */
   public void assignNodeToAgenda(Node evenement, Integer evenementEndDate,
         Integer agendaId) throws WebserviceException {
      int evenementNr = evenement.getNumber();
      
      Node agenda = dao.getNodeById(agendaId);      
      RelationList rl = agenda.getRelations("itemrel");
      for (Relation r : (List<Relation>) rl) {
         if (r.getDestination() != null && evenementNr == r.getDestination().getNumber()) {
          //Only add relation. If it already exists, do nothing            
            return;
         }
      }     
      Relation r = evenement.createRelation(agenda, dao.getRelationManagerByName("itemrel"));
      r.setIntValue("startdate", (int) Math.floor(System.currentTimeMillis() / 1000.0));
      r.setIntValue("enddate", evenementEndDate);
      r.commit();      
   }

   /* (non-Javadoc)
    * @see nl.kennisnet.cpb.webservice.service.ContentElementManagementService#removeNodeFromAgenda(org.mmbase.bridge.Node, java.lang.String)
    */
   public void removeNodeFromAgenda(Node evenement, Integer agendaId)
         throws WebserviceException {
      int evenementNr = evenement.getNumber();

      Node agenda = dao.getNodeById(agendaId);
      RelationList rl = agenda.getRelations("itemrel");
      Relation agenda2evenement = null;
      for (Relation r : (List<Relation>) rl) {
         if (r.getDestination() != null
               && evenementNr == r.getDestination().getNumber()) {
            //Found the relation
            agenda2evenement = r;
         }
      }
      if (agenda2evenement != null) {
         agenda2evenement.delete();
         //agenda2evenement.commit();
      } else {
         throw new UnexpectedNodeStateException("Evenement cannot be released from the agenda, because they have no relation to begin with");
      }
   }

   /**
    * Update the linkwrapper and the node using the fields in the element ce.
    * 
    * @param lw
    *           the linkwrapper
    * @param linkNode
    *           the cloud node
    * @param ce
    *           the element containing the input fields
    * @throws WebserviceException
    */
   private void updateLink(LinkWrapper lw, Node linkNode, Element item) throws WebserviceException {
      String url = item.getChildText("url");
      if (url == null) {
         throw new BadRequestException("No field url present in request to create link");
      }
      String naam = item.getChildText("naam");
      if (naam == null) {
         throw new BadRequestException("No field naam present in request to create link");
      }
      lw.setUrl(url);
      lw.setNaam(naam);
    //lw.setNotification(1); //TODO is dit default een externe link?
      linkNode.setStringValue("url", url);
      linkNode.setStringValue("naam", naam);      
   }
   
   /** Remove the connection between the evenement and the link. Both the evenement and the link are unchanged by this action
    * @param evenement the evenement
    * @param linkId the id of the link    
    * @throws WebserviceException in case the link is not connected to the evenement
    */   
   private void removeLinkFromEvent(Node evenement, Integer linkId) throws WebserviceException {

      RelationList rl = evenement.getRelations("swaprel");
      Relation linkRelation = null;
      for (Relation r : (List<Relation>) rl) {
         if (r.getDestination() != null
               && linkId == r.getDestination().getNumber()) {
            // Found the relation
            linkRelation = r;
         }
      }
      if (linkRelation != null) {
         linkRelation.delete();
         //linkRelation.commit();
      } else {
         throw new UnexpectedNodeStateException("Link cannot be released from the evenement, because they have no relation");
      }
   }

   
   /** Update the field of node n with the specified fieldName. If the input-value is null, set the field to empty 
    * @param n the node from the cloud 
    * @param fieldName the fieldname
    * @param childText the new value 
    */   
   private void updateTextField(Node n, String fieldName, String childText) {
      if (childText != null) {
         n.setStringValue(fieldName,childText);
      } else {
         n.setStringValue(fieldName,"");
      }
   }
   
   /** Update the field of node n with the specified fieldName. If the input-value is null, set the field to 0 
    * @param n the node from the cloud 
    * @param fieldName the fieldname
    * @param childText the new value 
    */   
   private void updateIntegerField(Node n, String fieldName, String childText) throws BadRequestException{
      if (childText != null) {
         try {
         n.setIntValue(fieldName,Integer.parseInt(childText));
         } catch (NumberFormatException nfe) {
            throw new BadRequestException(nfe);
         }
      } else {
         n.setIntValue(fieldName, 0);
      }
   }
   
   /** Update the field of node n with the specified fieldName.If the input-value is null, set the field to false 
    * @param n the node from the cloud 
    * @param fieldName the fieldname
    * @param childText the new value 
    */   
   private void updateBooleanField(Node n, String fieldName, String childText) throws BadRequestException{
      if (childText != null) {
            if (("0".equals(childText) || "1".equals(childText))) {
               n.setStringValue(fieldName,childText);
            } else {
               throw new BadRequestException("Field " + fieldName + " can only have 0 or 1 as value");
            }       
      } else {
         n.setStringValue(fieldName,"0");
      }
   }
}

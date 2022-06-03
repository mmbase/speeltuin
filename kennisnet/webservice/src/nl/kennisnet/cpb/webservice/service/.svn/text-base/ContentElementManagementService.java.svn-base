package nl.kennisnet.cpb.webservice.service;

import org.mmbase.bridge.Node;

import org.jdom.Element;

import nl.kennisnet.cpb.webservice.domain.AgendaWrapper;
import nl.kennisnet.cpb.webservice.domain.NodeWrapper;
import nl.kennisnet.cpb.webservice.domain.EventWrapper;
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
public interface ContentElementManagementService {

   
   public void deleteAgendaById(int id)
      throws WebserviceException;

   public void save(AgendaWrapper aw)
      throws WebserviceException;

   public void deleteEventById(int id)
      throws WebserviceException;

   public void save(EventWrapper aw)
      throws WebserviceException;

   /**
    * Overwrite the value of the fields of the specified tekstblok using the
    * fields from the JDOM node. Note 1: if the field is not present, the value
    * is reset to a default value is present in the JDOM element as a tag Note
    * 2: the node is committed after the updating process. Note 3: the
    * nodewrapper is updated to reflect the changes, also in its related nodes
    *
    * @param nw
    *           the wrapper around the cloud node
    * @param ce
    *           the JDOM element
    * @throws WebserviceException
    *            in case of invalid values in the input
    */
   public void updateTekstblok(NodeWrapper nw, Element ce)
         throws WebserviceException;

   /**
    * Overwrite the value of the fields of the specified Evenement using the
    * fields from the JDOM node. Note 1: if the field is not present, the value
    * is reset to a default value Note 2: the node is committed after the
    * updating process. Note 3: the nodewrapper is updated to reflect the
    * changes, also in its related nodes
    *
    * @param nw
    *           the wrapper around the cloud node
    * @param ce
    *           the JDOM element
    * @throws WebserviceException
    *            in case of invalid values in the input
    */
   public void updateEvent(int id, EventWrapper data)
         throws WebserviceException;

   /** Overwrite/set the base fields for an agenda
    * @param kw the wrapper around the agenda
    * @param ce the JDOM element which contains the new values
    */
   public void updateAgenda(int id, AgendaWrapper data)
         throws WebserviceException;

   /**
    * Set the redactiegroep of the newly created node
    *
    * @param n
    *           the new node
    * @param ce
    *           the redactiegroep containing the ce.
    */
   public void assignNodeToRedactiegroep(Node n, Element ce)
         throws WebserviceException;

   /**
    * Add the evenement to the agenda with the specified id or do nothing if
    * this relation already exists
    *
    * @param evenement
    *           the evenement
    * @param evenementEndDate
    *           the ending date of the evenement
    * @param agendaId
    *           the unique number of the agenda
    */
   public void assignNodeToAgenda(Node evenement, Integer evenementEndDate,
         Integer agendaId) throws WebserviceException;

   /**
    * Release the coupling between the evenement and the agenda. The evenement
    * will remain in the cloud, only it is no longer connected to this
    * particular agenda
    *
    * @param evenement
    *           the evenement
    * @param agendaId
    *           the unique number of the agenda
    */
   public void removeNodeFromAgenda(Node evenement, Integer agendaId)
         throws WebserviceException;
}

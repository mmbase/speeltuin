package nl.kennisnet.cpb.webservice.delegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;

import org.apache.commons.lang.NotImplementedException;

import nl.kennisnet.cpb.webservice.error.WebserviceException;
import nl.kennisnet.cpb.webservice.error.WebserviceNotImplementedException;
import nl.kennisnet.cpb.webservice.service.ContentElementManagementService;
import nl.kennisnet.cpb.webservice.service.ContentElementManagementServiceImpl;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationService;
import nl.kennisnet.cpb.webservice.service.ContentElementTranslationServiceImpl;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAO;
import nl.kennisnet.cpb.webservice.service.WebserviceCommunicatorDAOImpl;

/**
 * The abstract delegate which is subclassed by all delegates for the
 * REST-webservice of Copacabana/MMBase. It defines default(error-throwing)
 * implementations for the handling GET,POST,PUT and DELETE methods and defines
 * a few helper functions for creating valid Atom-documents
 * 
 * @author dekok01
 * 
 */
public abstract class AbstractRestDelegate {

   public static final String CONTENT_ID_ATTR = "copa.content_id";
   public static final String AGENDA_ID_ATTR = "copa.agenda_id";
   public static final String EVENEMENT_ID_ATTR = "copa.evenement_id";
   public static final String SITE_ID_ATTR = "copa.site_id";
   public static final String PAGE_ID_ATTR = "copa.page_id";
   public static final String FEED_ID_ATTR = "copa.feed_id";
   public static final String PAGE_NR_ATTR = "copa.page_number";
   public static final String IMAGE_ID_ATTR = "copa.image_id";
   public static final String CONTENT_REPRESENTATION = "copa.content.repr";
   protected static final int MAX_NR_SEARCH_RESULTS = 250;
   
   protected WebserviceCommunicatorDAO dao = new WebserviceCommunicatorDAOImpl();
   protected ContentElementTranslationService translationService = new ContentElementTranslationServiceImpl();
   protected ContentElementManagementService ceService = new ContentElementManagementServiceImpl();
   
   /**
    * Defines the default implementation for the handling of DELETE requests to
    * REST-delegates 
    * 
    * @param req the request
    * @param res the response
    * @throws WebserviceException in case a known error scenario is encountered    
    * @throws ServletException common servlet exception 
    * @throws IOException common IO exception
    */
   public void doDelete(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      throw new NotImplementedException("DELETE-method not implemented for this resource");
   }

   /**
    * Defines the default implementation for the handling of GET requests to
    * REST-delegates 
    * 
    * @param req the request
    * @param res the response
    * @throws WebserviceException in case a known error scenario is encountered    
    * @throws ServletException common servlet exception 
    * @throws IOException common IO exception
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res)
         throws WebserviceException, ServletException, IOException {
      throw new NotImplementedException("GET-method not implemented for this resource");
   }

   /**
    * Defines the default implementation for the handling of POST requests to
    * REST-delegates 
    * 
    * @param req the request
    * @param res the response
    * @throws WebserviceException in case a known error scenario is encountered    
    * @throws ServletException common servlet exception 
    * @throws IOException common IO exception
    */
   public void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      throw new NotImplementedException("POST-method not implemented for this resource");
   }

   /**
    * Defines the default implementation for the handling of PUT requests to
    * REST-delegates 
    * 
    * @param req the request
    * @param res the response
    * @throws WebserviceException in case a known error scenario is encountered
    * @throws ServletException common servlet exception 
    * @throws IOException common IO exception
    */
   public void doPut(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException, WebserviceException {
      throw new WebserviceNotImplementedException("PUT-method not implemented for this resource");
   }

   /** Setter for future dependency injection
    * @param dao the new propertt
    */
   public void setDao(WebserviceCommunicatorDAO dao) {
      this.dao = dao;
   }

   /** Setter for future dependency injection
    * @param translationService the new propertt
    */
   public void setTranslationService(
         ContentElementTranslationServiceImpl translationService) {
      this.translationService = translationService;
   }

   public void setCeService(ContentElementManagementService ceService) {
      this.ceService = ceService;
   }

}

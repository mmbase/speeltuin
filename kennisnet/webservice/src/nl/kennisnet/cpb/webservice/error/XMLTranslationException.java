/**
 * 
 */
package nl.kennisnet.cpb.webservice.error;

/**
 * Application Exception thrown when the ce could not be converted to XML 
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class XMLTranslationException extends WebserviceException {

   public XMLTranslationException(String message) {
      super(message);    
   }

   public XMLTranslationException(Throwable cause) {
      super(cause);
   }
}

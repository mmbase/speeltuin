/**
 * 
 */
package nl.kennisnet.cpb.webservice.error;

/**
 * Application Exception thrown when the nodes in the cloud have an unexpected state.
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class UnexpectedNodeStateException extends WebserviceException {

   public UnexpectedNodeStateException(String message) {
      super(message);    
   }

   public UnexpectedNodeStateException(Throwable cause) {
      super(cause);
   }
}

package nl.kennisnet.cpb.webservice;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class for KLAS-related functions. Typically, this would be used for
 * retrieving data on a specific user using the Entree-id.
 * 
 * @author dekok01
 * 
 */
public class KlasUtil {

   /**
    * Returns a set of Strings, containing the redactiecodes belonging to the
    * user with the specified entree-id
    * 
    * @param entreeId
    * @return
    */
   public static Set findRedactieCodesByEntreeId(String entreeId) {

      //TODO implement integration with KLAS. Also change KlasLoginModule in LoginModuleFactory to throw SecurityException
      
      // Stub implementation:
      Set s = new HashSet();
      s.add("redac1");
      return s;

   }
}

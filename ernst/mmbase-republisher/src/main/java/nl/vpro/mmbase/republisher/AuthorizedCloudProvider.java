package nl.vpro.mmbase.republisher;

import java.util.HashMap;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.LocalContext;

public final class AuthorizedCloudProvider implements CloudProvider {
    
    private Map<String,String> credentials;
    
    

    public AuthorizedCloudProvider(String username, String password) {
        credentials = new HashMap<String, String>();
        credentials.put("username", username);
        credentials.put("password", password);
        
    }



    public Cloud getCloud() {
        
        return LocalContext.getCloudContext().getCloud("mmbase-vob", "name/password", credentials);
    }

}

package nl.vpro.mmbase.republisher;

import nl.vpro.mmbase.republisher.endpoint.DummyPublicationEndPoint;
import nl.vpro.mmbase.republisher.serializer.ToStringSerializer;
import nl.vpro.mmbase.vob.EntityConfigLoader;
import nl.vpro.mmbase.vob.Populator;

import org.apache.commons.lang.StringUtils;
import org.mmbase.core.event.EventManager;
import org.mmbase.module.Module;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public final class RepublisherModule extends Module{ 
	private static Logger log = Logging.getLoggerInstance(RepublisherModule.class.getName());
	private RepublisherEventListener republisher;
    private String pathToScan;
    private String username;
    private String password;
	
	

	@Override
	public void init() {
	    super.init();
		log.info("MyGlobalEventListener starting...");
		
		pathToScan = getMandatoryProperty("pathToScan");
		username = getMandatoryProperty("username");
		password = getMandatoryProperty("password");
		log.service("Scanning classpath from: "+pathToScan);
		EventManager.getInstance().addEventListener(initRepublisher());
	}
	
	private String getMandatoryProperty(String property){
	    String propertyValue = getInitParameter(property);
        if(StringUtils.isBlank(propertyValue)){
            throw new IllegalStateException(String.format("Parameter '%' is mandatory on module Republisher", property));
        }
        return propertyValue;
	}

	
	private RepublisherEventListener initRepublisher(){
	    //TODO: this configurations should be externalized. perhaps an xml configured spring application context
	    republisher =  new RepublisherEventListener();
        republisher.setDocumentSerializer(new ToStringSerializer());
        republisher.setPublicationEndPoint(new DummyPublicationEndPoint());
        republisher.setCloudProvider(new AuthorizedCloudProvider(username, password));
        republisher.init();
        republisher.setPopulator(new Populator(new EntityConfigLoader(pathToScan)));
        return republisher;
	}

    @Override
    protected void shutdown() {
        super.shutdown();
        republisher.shutdown();
    }
}

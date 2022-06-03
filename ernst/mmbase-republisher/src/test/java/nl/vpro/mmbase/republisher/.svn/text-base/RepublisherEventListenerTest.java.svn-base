package nl.vpro.mmbase.republisher;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import nl.vpro.mmbase.republisher.endpoint.*;
import nl.vpro.mmbase.republisher.serializer.DocumentSerializer;
import nl.vpro.mmbase.republisher.validator.DocumentValidator;
import nl.vpro.mmbase.vob.EntityConfigLoader;
import nl.vpro.mmbase.vob.Populator;

import org.junit.Before;
import org.junit.Test;
import org.mmbase.bridge.Cloud;
import org.mmbase.core.event.NodeEvent;

/**
 * <pre>
 * This class will test if right actions are produced by node events.
 * validation is not tested here!
 * serialization is not tested here!
 * publication is not tested here!
 * 
 * What do we need to junit test the Republisher event listener?
 * - a mmbase-vob decorated set of objects.
 * - a Populator instance
 * - a node event instance
 * - a mockedup of queryHelper, that will be used by the populator to create 
 *   a real entity instance.
 * </pre>
 * 
 * @author Ernst Bunders
 * 
 */
public class RepublisherEventListenerTest {
    private static final String SERIALIZED_DOCUMENT = "serialized document";
    private Populator populator;
    private RepublisherEventListener eventListener;
    private DocumentValidator validator;
    private DocumentSerializer serializer;
    private DummyEndpoint endPoint;
    private MyNewsDummy queryHelper;
    private CloudProvider cloudProvider;
    private Cloud cloud;
    private EntityConfigLoader entityConfigLoader;

    @Before
    public void init() {
        eventListener = new RepublisherEventListener();
        queryHelper = new MyNewsDummy();
        entityConfigLoader = new EntityConfigLoader("nl.vpro.mmbase.republisher.domain");
        populator = new Populator(entityConfigLoader, queryHelper);
        eventListener.setPopulator(populator);
        
        validator = createMock(DocumentValidator.class);
        expect(validator.isValid(anyObject())).andReturn(true).atLeastOnce();
        
        serializer = createMock(DocumentSerializer.class);
        expect(serializer.serializeDocument(anyObject())).andReturn(SERIALIZED_DOCUMENT).atLeastOnce();

        cloud= createMock(Cloud.class);
        
        cloudProvider= createMock(CloudProvider.class);
        expect(cloudProvider.getCloud()).andReturn(cloud).atLeastOnce();
        
        endPoint = new DummyEndpoint();
        
        replay(validator);
        replay(serializer);
        replay(cloudProvider);
        
        eventListener.setDocumentValidator(validator);
        eventListener.setDocumentSerializer(serializer);
        eventListener.setPublicationEndPoint(endPoint);
        eventListener.setCloudProvider(cloudProvider);
        
        
    }

//    @Test
    public void testCloudProvider(){
        assertNotNull(cloudProvider.getCloud());
    }
    
    /**
     * if a node is created for which a (valid) root entity can be created, the entity should be pubished
     */
    @Test
    public void testCreateNodeEventOnRootEntity() {
        //setup
        NodeEvent event = createNodeEvent("news", 1234, NodeEvent.TYPE_NEW);
        expect(cloud.getNode(1234)).andReturn(queryHelper.getNews()).atLeastOnce();
        replay(cloud);
        
        assertEquals("test news",cloud.getNode(1234).getStringValue("title"));
        
        //do it
        eventListener.notify(event);
        
        //check
        assertNotNull(endPoint.getDocumentModification());
        assertEquals(ModificationType.CREATE, endPoint.getDocumentModification().getType());
        assertEquals(SERIALIZED_DOCUMENT, endPoint.getDocumentModification().getSerializedDocument());
    }
    
    /**
     * The entity should not be published
     */
    @Test
    public void testCreateNodeEventOnNonRootEntity() {
      //setup
        NodeEvent event = createNodeEvent("images", 1234, NodeEvent.TYPE_NEW);
        expect(cloud.getNode(1234)).andReturn(queryHelper.getImages().get(0)).atLeastOnce();
        replay(cloud);
        
        assertEquals("img1",cloud.getNode(1234).getStringValue("title"));
        
        //do it
        eventListener.notify(event);
        
        //check
        assertNull(endPoint.getDocumentModification());
    }
    
    
   
    
    
    /**
     * updated node events that map to a  root entity should republish this entity when valid
     */
    @Test
    public void TestUpdateNodeEventOnRootEntity(){
      //setup
        NodeEvent event = createNodeEvent("news", 1234, NodeEvent.TYPE_CHANGE);
        expect(cloud.getNode(1234)).andReturn(queryHelper.getNews()).atLeastOnce();
        replay(cloud);
        
        assertEquals("test news",cloud.getNode(1234).getStringValue("title"));
        
        //do it
        eventListener.notify(event);
        
        //check
        assertEquals(SERIALIZED_DOCUMENT, endPoint.getDocumentModification().getSerializedDocument());
    }
    
    /**
     * updated node events that map to a non root entity should find all aggregating
     * root entities, and publish them when they are valid 
     */
    @Test
    public void TestUpdateNodeEventOnNonrootEntity(){
        NodeEvent event = createNodeEvent("news", 1234, NodeEvent.TYPE_CHANGE);
        expect(cloud.getNode(1234)).andReturn(queryHelper.getNews()).atLeastOnce();
        replay(cloud);
        
        assertEquals("test news",cloud.getNode(1234).getStringValue("title"));
        
        //do it
        eventListener.notify(event);
        
        //check
        assertEquals(SERIALIZED_DOCUMENT, endPoint.getDocumentModification().getSerializedDocument());
    }
    
    
    /**
     * if a node has changed that is aggregated by a root entity, it should publish
     */
    @Test
    public void TestUpdateNodeEventOnNodeAggregatedByRootEntity(){
        
    }
    
    /**
     * if an node has changed that is aggregated by a non-root entity, all root entities should
     * be published.
     * TODO: a node can be aggregated by more than one entity. This will normally not happen, and
     * should be implemented at a later time. 
     */
    public void TestUpdateNodeEventOnNodeAggregatedByNonRootEntity(){
        
    }
    
    
    private NodeEvent createNodeEvent(String nodeType, int nodenr,  int type){
        return new NodeEvent("localhost", nodeType, nodenr, null, null, type);
    }
    
    private static final class DummyEndpoint implements PublicationEndPoint{
        private DocumentModification documentModification;

        public void init() {}
        public void publishDocument(DocumentModification documentModification) throws PublicationException {
            this.documentModification = documentModification;
        }
        public void shutdown() {}
        public DocumentModification getDocumentModification(){
            return documentModification;
        }
    }
    
    
}

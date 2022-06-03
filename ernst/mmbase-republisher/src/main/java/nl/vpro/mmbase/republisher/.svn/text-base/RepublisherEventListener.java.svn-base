package nl.vpro.mmbase.republisher;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.mmbase.republisher.endpoint.*;
import nl.vpro.mmbase.republisher.serializer.DocumentSerializer;
import nl.vpro.mmbase.republisher.validator.DocumentValidator;
import nl.vpro.mmbase.vob.*;
import nl.vpro.mmbase.vob.EntityConfigLoader.EntityEmbedding;
import nl.vpro.mmbase.vob.EntityConfigLoader.Path;

import org.apache.commons.lang.SerializationException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.event.*;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public final class RepublisherEventListener implements NodeEventListener, RelationEventListener {
    private Populator populator;
    private PublicationEndPoint publicationEndPoint;
    private DocumentSerializer documentSerializer;
    private DocumentValidator documentValidator;
    private CloudProvider cloudProvider;
    private Cloud cloud;
    private EntityConfigLoader entityConfigLoader;
    private static final Logger log = Logging.getLoggerInstance(RepublisherEventListener.class);
    
    
    

    CloudProvider getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(CloudProvider cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    public void init() {
        if (publicationEndPoint != null) {
            publicationEndPoint.init();
            cloud = getCloud();
        }
    }

    public void shutdown() {
        if (publicationEndPoint != null) {
            publicationEndPoint.shutdown();
        }
    }

    DocumentSerializer getDocumentSerializer() {
        return documentSerializer;
    }

    public void setDocumentSerializer(DocumentSerializer documentSerializer) {
        this.documentSerializer = documentSerializer;
    }

    PublicationEndPoint getPublicationEndPoint() {
        return publicationEndPoint;
    }

    public void setPublicationEndPoint(PublicationEndPoint publicationEndPoint) {
        this.publicationEndPoint = publicationEndPoint;
    }

    Populator getPopulator() {
        return populator;
    }

    public void setPopulator(Populator populator) {
        this.populator = populator;
        this.entityConfigLoader = populator.getEntityConfigLoader();
    }

    DocumentValidator getDocumentValidator() {
        return documentValidator;
    }

    public void setDocumentValidator(DocumentValidator documentValidator) {
        this.documentValidator = documentValidator;
    }

    public void notify(NodeEvent event) {
        if (event.getType() == NodeEvent.TYPE_NEW) {
            handleNodeCreate(event);
        } else if (event.getType() == NodeEvent.TYPE_DELETE) {
            handleNodeDelete(event);
        } else if (event.getType() == NodeEvent.TYPE_CHANGE) {
            handleNodeUpdate(event);
        }
    }

    public void notify(RelationEvent event) {
        if (event.getType() == RelationEvent.TYPE_NEW) {
            handleRelationCreate(event);
        } else if (event.getType() == RelationEvent.TYPE_DELETE) {
            handleRelationDelete(event);
        } else if (event.getType() == RelationEvent.TYPE_CHANGE) {
            handleRelationUpdate(event);
        }
    }

    /**
     * When a node is created and the node type can be mapped to a root entity: try to publish it.: 
     * @param event
     */
    private void handleNodeCreate(NodeEvent event) {
            Object entity = findEntity(event.getBuilderName(), event.getNodeNumber());
            if (entity != null) {
                if (EntityConfigLoader.entityIsRoot(entity)) {
                        tryToPublishDocument(entity, ModificationType.CREATE);
                }else{
                    //do nothing. only root entities need to be published 
                }
            }
    }
    
    /**
     * <pre>
     * TODO:The question is: can we still do all the stuff we do with updated nodes: is the node still there or is it 
     * already deleted.  if so: the event is quite useless, and we can not do a thing.
     * I am for now assuming that the node is still there, and we can check if the document was valid, so published.
     * 
     * and:
     * with node deletes the situation can arise that a document was valid, has been published, and now is no longer valid.
     * in this case it should be republished with modification type 'delete'. the practical implication is that 'tryToPublish' should inverse it's
     * workings: an entity will be published when it is NOT valid.  
     * The question is: how can we assert that  the entity was both valid before the node delete and invalid after: 
     * When the event is handled two things are possible:
     * 
     * - the 'deleted' node is still there, and we can check if the document is/was valid with this node.  
     * - the 'deleted' node is not there anymore, and we could check if the entity is still valid, if we can find the entity (which is hard when the node is gone).
     * 
     * What to do: discuss this, and find a reasonable yet simple behavior for deleted nodes.
     * </pre>
     * 
     * @param event
     */
    private void handleNodeDelete(NodeEvent event) {
        handleNodeChange(event, ModificationType.DELETE);
    }

    
    private void handleNodeUpdate(NodeEvent event) {
        handleNodeChange(event, ModificationType.UPDATE);
    }

    /**
     * <pre>
     * The following possibilities should be considered:
     * - the node can be mapped to a root entity: try to publish that entity
     * - the node can be mapped to a non-root entity: find all aggregating root entities, and publish those
     * - the node can not be mapped to an entity, but nodes of this type can be embedded by on or more entities: for each such possibility:
     *      -find the embedding entity:
     *        - is it a root entity: try to publish it
     *        - is it a non-root entity: find all aggregating root entities, and publish those
     * </pre>
     * @param event the node event
     * @param modificationType the type of document modification (could be resolved from node event)
     */
    private void handleNodeChange(NodeEvent event, ModificationType modificationType) {
//        if(event.getType()= = NodeEvent.TYPE_CHANGE)
        Object entity = findEntity(event.getBuilderName(), event.getNodeNumber());
        if(entity != null){
            if(EntityConfigLoader.entityIsRoot(entity)){
                tryToPublishDocument(entity, modificationType);
            }else{
                tryToPublishRootEntities(cloud.getNode(event.getNodeNumber()), entity.getClass(), modificationType);
            }
        }else{
                for(EmbeddingEntity embeddingEntity: findEmbeddingEntities(event)){
                    if(EntityConfigLoader.entityIsRoot(embeddingEntity.entity)){
                        tryToPublishDocument(embeddingEntity.entity, modificationType);
                    }else{
                        tryToPublishRootEntities(embeddingEntity.entityNode, embeddingEntity.entity.getClass(), modificationType);
                    }
                }
        }
    }


    /**
     * @param node event
     * @return a list of entities that embed this event's node. These are not just possible embeddings, 
     * but loaded entities that are related to the given node. 
     */
    private List<EmbeddingEntity> findEmbeddingEntities(NodeEvent event) {
        Node node = cloud.getNode(event.getNodeNumber());
        List<EmbeddingEntity> embeddingEntities = new ArrayList<EmbeddingEntity>();

        //we invert the query directory, because we run the query backwards (from the embedded node to the embedding node)
        for(EntityEmbedding embedding: entityConfigLoader.findEntitiesThatEmbed(event.getBuilderName())){
            NodeList nl = node.getRelatedNodes(
                    cloud.getNodeManager(embedding.embeddingType), 
                    embedding.relationRole, 
                    inverseDirection(embedding.queryDirection).toString());

            //it's not likely but possible more than one node is embedding the event's node.
            for(NodeIterator ni = nl.nodeIterator(); ni.hasNext();){
                Node entityNode = ni.nextNode();
                String managerName = entityNode.getNodeManager().getName();
                embeddingEntities.add(new EmbeddingEntity(entityNode, populator.unmarshallNode(entityNode, managerName)));
            }
        }
        return embeddingEntities;
    }

    private QueryDirection inverseDirection(QueryDirection queryDirection) {
        if(queryDirection == QueryDirection.SOURCE){
            return QueryDirection.DESTINATION;
        }else if(queryDirection == QueryDirection.DESTINATION){
            return QueryDirection.SOURCE;
        }else{
            return QueryDirection.BOTH;
        }
    }

    /**
     * find all the root entities that aggregate this entity, directly or indirectly.
     * The root entities are found by first finding all the possible paths to root entities, and then query all these to find the actual existing nodes.
     * Then for all found root entity nodes entity instances are created and publication is attempted in the ordinary way.
     * @param node the node that represents the aggregated entity
     * @param entity the aggregated entity
     */
    private void tryToPublishRootEntities( Node node, Class<?> entityClass, ModificationType modificationType) {
        List<Path> possibleRootEntityPaths = entityConfigLoader.findPathsToAggregatingEntities(entityClass) ;
        List<Node> rootEntities = new ArrayList<Node>();
        for(Path path : possibleRootEntityPaths){
            rootEntities.addAll(findRootEntityNodes(node, path));
        }
        
        for(Node rootNode: rootEntities){
            Object rootEntity = populator.unmarshallNode(rootNode, rootNode.getNodeManager().getName());
            tryToPublishDocument(rootEntity, modificationType);
        }
    }

    /**
     * Finds nodes that represent root entities that are actually connected to a given entity node.
     * the type of the returned nodes is always equal to the root field of the given path.
     * @param node 
     * @param path a query path from 'a' root entity to the entity this node represents
     * @return a list of nodes of the root entity type that are actually connected to this node through the path
     */
    private List<Node> findRootEntityNodes(Node node, Path path) {
        List<Node>rootNodes = new ArrayList<Node>();
        Query query =cloud.createQuery();
        List<Step>steps = Queries.addPath(query, path.pathAsString(), path.queryDirectionsAsString());
        //TODO:test if this is the proper way to add the constraint on the node.
        steps.get(steps.size() - 1).addNode(node.getNumber());
        
        for(NodeIterator ni = cloud.getList(query).nodeIterator(); ni.hasNext();){
            rootNodes.add(ni.nextNode().getNodeValue(path.root));
        }
        return rootNodes;
    }

    private void handleRelationCreate(RelationEvent event) {
        // TODO Auto-generated method stub
    }

    private void handleRelationDelete(RelationEvent event) {
        // TODO Auto-generated method stub
    }

    private void handleRelationUpdate(RelationEvent event) {
        int relSrc = event.getRelationSourceNumber();
        int relDest = event.getRelationDestinationNumber();
        String role = getRole(event.getRole());
        
        if (entityExistsFor(nodeManager(relSrc)) || entityExistsFor(nodeManager(relDest))) {
            if (isEntityThatAggregates(relSrc, relDest, role) || isEntityThatEmbeds(relSrc, relDest, role)) {
                Object entity = populator.unmarshallNode(cloud.getNode(relSrc), nodeManager(relSrc));
                if(EntityConfigLoader.entityIsRoot(entity)){
                    //publish entity
                }else{
                    //find root entities and publish those
                }
            } else if (isEntityThatAggregates(relDest, relSrc, role) || isEntityThatEmbeds(relDest, relSrc, role)) {
                Object entity = populator.unmarshallNode(cloud.getNode(relDest), nodeManager(relDest));
                if(EntityConfigLoader.entityIsRoot(entity)){
                    //publish entity
                }else{
                    //find root entities and publish those
                }
            }
        }
    }
    
    private boolean isEntityThatAggregates(int sourceNode, int destNode, String role) {
        if(entityExistsFor(nodeManager(sourceNode)) && entityExistsFor(nodeManager(destNode))){
            return entityAggregates(entityClass(sourceNode), entityClass(destNode), role);
        }
        return false;
    }

    private boolean entityAggregates(Object entityClass, Object entityClass2, String role) {
        // TODO Auto-generated method stub
        return false;
    }

    private Object entityClass(int sourceNode) {
        return entityConfigLoader.getEntityRegistry().get(nodeManager(sourceNode));
    }

    private boolean isEntityThatEmbeds(int entityNode, int embeddedNode, String role) {
        // TODO Auto-generated method stub
        return false;
    }

    private String getRole(int role) {
        return cloud.getNode(role).getStringValue("sname");
    }


    private String nodeManager(int nodenr){
        return cloud.getNode(nodenr).getNodeManager().getName();
    }
    
    private boolean entityExistsFor(String nodeManager){
        return entityConfigLoader.getEntityRegistry().get(nodeManager)  != null; 
    }
    
    /**
     * Publish the entity when:
     * - the validator validates it.
     * - it can be serialized without problems
     * - it can be handled by the publication endpoint without problems.
     * @param entity
     * @param modificationType
     * @throws SerializationException, PublicationException
     */
    private void tryToPublishDocument(Object entity, ModificationType modificationType) {
        try{
            if(documentValidator.isValid(entity)){
                String document = documentSerializer.serializeDocument(entity);
                publicationEndPoint.publishDocument(new DocumentModification(modificationType, document));
            }else{
                log.debug(String.format("Entityt %s is not a valid document and wil not be published",entity));
            }
        }catch(SerializationException se){
            //...
        }catch(PublicationException pe){
            //..
        }
    }

    /**
     * TODO: if the node is embedded, it is not an entity, but it is part of one.
     * how to deal with that? how to create the entity:
     * - find the path to the embedding entity
     * - query the node
     * - get the entity for that node
     * 
     * @param nodeType
     * @param nodeNr
     * @return
     */
    private Object findEntity(String nodeType, int nodeNr){
        if (entityConfigLoader.hasEntityFor(nodeType)) {
            Node node =   getCloud().getNode(nodeNr);
            return  populator.unmarshallNode(node, nodeType);
        }else{
            log.debug(String.format("no entity found for node type %s", nodeType));
        }
        return null;
    }

    private Cloud getCloud(){
        if(cloud == null){
            cloud = cloudProvider.getCloud();
        }
        return cloud;
    }
    
    static final class EmbeddingEntity{
        final Node entityNode;
        final Object entity;
        public EmbeddingEntity(Node entityNode, Object entity) {
            this.entityNode = entityNode;
            this.entity = entity;
        }
    }

}

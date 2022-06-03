package nl.vpro.mmbase.vob;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import nl.vpro.mmbase.vob.annotations.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;

public class EntityConfigLoader {
    public static final class EntityEmbedding{
            public final String relationRole;
            public final String embeddingType;
            public final String embeddedType;
            public final Class<?> EntityClass;
            public final boolean isRoot;
            public final QueryDirection queryDirection;
            public EntityEmbedding(String relationRole, String embeddingType, String embeddedType, Class<?> entityClass, boolean isRoot, QueryDirection direction) {
                this.isRoot = isRoot;
                this.queryDirection = direction;
                this.relationRole = relationRole;
                this.embeddingType = embeddingType;
                this.embeddedType = embeddedType;
                EntityClass = entityClass;
            }
            public String toString(){
                return new ReflectionToStringBuilder(this).toString();
            }
      }

    public static final class EntityAggregation{
        public final String relationRole;
        public final Class<?> aggregatingEntityClass;
        public final Class<?> aggregatedEntityClass;
        public final QueryDirection queryDirection;
        public EntityAggregation(String relationRole, Class<?>aggregatingEntityClass, Class<?> aggregatedEntityClass, QueryDirection direction) {
            this.queryDirection = direction;
            this.relationRole = relationRole;
            this.aggregatingEntityClass = aggregatingEntityClass;
            this.aggregatedEntityClass = aggregatedEntityClass;
        }
        public String toString(){
            return new ReflectionToStringBuilder(this).toString();
        }
    }

    public static final class QueryStep{
        public String step;
        public String relationRole;
        public QueryDirection direction;
        public QueryStep(String step, String relationRole, QueryDirection direction) {
            this.step = step;
            this.relationRole = relationRole;
            this.direction = direction;
        }
        public String toString(){
            return new ReflectionToStringBuilder(this).toString();
        }
    }

    public static final class Path{
        public  final List<QueryStep> steps;
        public final String root;
        
        public Path(String root) {
            this.root = root;
            steps = new ArrayList<QueryStep>();
        }
        
        public Path clone(){
            Path clone = new Path(this.root);
            clone.steps.addAll(this.steps);
            return clone;
        }
        
        public String toString(){
            return new ReflectionToStringBuilder(this).toString();
        }
        
        public String pathAsString(){
            StringBuilder sb = new StringBuilder(root);
            sb.append(",");
            for(QueryStep step:steps){
                sb.append(step.relationRole);
                sb.append(",");
                sb.append(step.step);
                sb.append(",");
            }
            return sb.toString().substring(0, sb.length() - 1);
        }
        
        public String queryDirectionsAsString(){
            StringBuilder sb = new StringBuilder();
            for(QueryStep step:steps){
                sb.append(step.direction.toString());
                sb.append(",");
            }
            return sb.toString().substring(0, sb.length() - 1);
        }  
    }

    private static Logger log = Logging.getLoggerInstance(EntityConfigLoader.class.getName());
    private Map<String, Class<?>> entityRegistry = new HashMap<String, Class<?>>();
    private boolean configLoaded = false;
    
    /**
     * @param queryHelper The query helper to use when executing subqueries.
     * @param pathToScan The base path the scan for entities
     */
    public EntityConfigLoader(final String pathToScan) {
        scanClasspathForEntities(pathToScan);
    }
    
    /**
     * Searches the given path for object annotated with the @entity annotation.
     * 
     * When found, entities are added to a registry to be used for quick lookups
     * will converting.
     * 
     * @param pathToScan The base path the scan for entities
     */
    private void scanClasspathForEntities(final String pathToScan) {
        if (!configLoaded) {
            final GenericApplicationContext context = new GenericApplicationContext();
            final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
            scanner.resetFilters(false);
            scanner.addIncludeFilter(new EntityFilter(entityRegistry));
            scanner.scan(pathToScan);
            log.service(entityRegistry.size() + " Entity mappings scanned on classpath " + pathToScan);
            entityRegistry = Collections.unmodifiableMap(entityRegistry);
            configLoaded = true;
        }
    }

    /**
     * @param builderName
     * @return true if There is an entity mapping for given node type.
     */
    public boolean hasEntityFor(final String builderName) {
        return entityRegistry.containsKey(builderName);
    }

    /**
     * @param builderName
     * @return an entity class mapped to this builder
     */
    public Class<?> getEntityFor(final String builderName) {
        return entityRegistry.get(builderName);
    }
    
    /**
     * @return an unmodifiable map with entity classes mapped to the name of the builder they
     * are mapped to
     * @throws IllegalStateException when this method is called before configuration loading is complete.
     */
    public Map<String, Class<?>> getEntityRegistry(){
        if(configLoaded){
            return entityRegistry;
        }else{
            throw new IllegalStateException("Called too soon. configuration not loaded yet");
        }
    }

    //    public List<EntityAggregation> findAggregatingEntitiesFor(Class<?> entity){
    //        List<EntityAggregation> aggregatingEntities = new ArrayList<EntityAggregation>();
    //        for (Class<?> entityClass : entityRegistry.values()) {
    //            for (Field field : entityClass.getDeclaredFields()) {
    //                
    //                //TODO: double code. refactor the Rel entity so Posrel entity can be dumped.
    //                //try the Rel entity
    //                Rel relAnnotation = field.getAnnotation(Rel.class);
    //                if(relAnnotation != null){
    //                    Class<?> aggregatingEntity= entityRegistry.get(field.getName());
    //                    if(aggregatingEntity != null){
    //                        aggregatingEntities.add(new EntityAggregation("related", aggregatingEntity, entityClass, relAnnotation.queryDirection() ));
    //                    }else{
    //                        log.warn(String.format("Field '%s' in entity '%s' can not be mapped to entity %S", field.getName(), entityClass.getName()));
    //                    }
    //                }
    //                
    //                //try the Posrel entity
    //                PosRel posrelAnnotation = field.getAnnotation(PosRel.class);
    //                if(posrelAnnotation != null){
    //                    Class<?> aggregatingEntity= entityRegistry.get(field.getName());
    //                    if(aggregatingEntity != null){
    //                        aggregatingEntities.add(new EntityAggregation("posrel",  aggregatingEntity, entityClass, posrelAnnotation.queryDirection() ));
    //                    }else{
    //                        log.warn(String.format("Field '%s' in entity '%s' can not be mapped to another entity", field.getName(), entityClass.getName()));
    //                    }
    //                }
    //            }
    //        }
    //        return aggregatingEntities;
    //    }
        
        public List<EntityAggregation> findAggregatedEntitiesFor(Class<?> entityClass){
            List<EntityAggregation> aggregatedEntities = new ArrayList<EntityAggregation>();
            for (Field field : entityClass.getDeclaredFields()) {
                
                if(field.getAnnotation(Rel.class) != null){
                    Rel rel = field.getAnnotation(Rel.class);
                    aggregatedEntities.add(new EntityAggregation("related", entityClass,  retrieveActualType(field), rel.queryDirection()));
                }
                
                if(field.getAnnotation(PosRel.class) != null){
                    PosRel posrel = field.getAnnotation(PosRel.class);
                    aggregatedEntities.add(new EntityAggregation("posrel", entityClass, retrieveActualType(field), posrel.queryDirection()));
                }
            }
            return aggregatedEntities;
        }
        
        /**
     * this method will create query paths to from a given entity to all root entities that aggregate this entity
     * directly or indirectly.
     * @param entityClass
     * @return
     */
    public List<Path> findPathsToAggregatingEntities(Class<?> entityClass) {
        List<Path> result = new ArrayList<Path>();
        for (Class<?> rootEntity : findRootEntities()) {
            result.addAll(findPathsToEntity(rootEntity, entityClass, null, null));
        }
        return result;
    }

    private List<Path> findPathsToEntity(Class<?> rootEntity, Class<?> targetEntity, Path path, EntityAggregation previous) {
        List<Path> result = new ArrayList<Path>();
        
        if(path == null){
            path = new Path(determineBuilder(rootEntity));
        }
        
        if(previous != null){
            rootEntity = previous.aggregatedEntityClass;
            path.steps.add(new QueryStep(determineBuilder(rootEntity),  previous.relationRole, previous.queryDirection));
        }
        
        if(rootEntity == targetEntity){
            result.add(path);
        }
        
        for (EntityAggregation aggregation : findAggregatedEntitiesFor(rootEntity)) {
            result.addAll(findPathsToEntity(aggregation.aggregatedEntityClass, targetEntity, path.clone(), aggregation));
        }
        return result;
    }
    
    public  List<Class<?>> findRootEntities() {
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> entityClass : entityRegistry.values()) {
            Entity entity = entityClass.getAnnotation(Entity.class);
            if(entity != null && entity.root() == true){
                 result.add(entityClass);
            }
        }
        return result;
    }

    /**
     * TODO: the result could  contain more entities than one.
     * @param relationRole
     * @param builder
     * @return the entity that embeds a given nodetype with a given relationrole.
     */
    public Class<?> findEntityThatEmbeds(String relationRole, String builder) {
        for (Class<?> entityClass : entityRegistry.values()) {
            for (Field field : entityClass.getDeclaredFields()) {
                Embedded embedded = field.getAnnotation(Embedded.class);
                if (embedded != null) {
                    if (equalsNotBlank(relationRole, embedded.relationRole())
                            && equalsNotBlank(builder, embedded.builder())) {
                        return entityClass;
                    }
                }
            }
        }
        return null;
    }

    /**
     * finds all entities that embed a node of given type
     * @param builder the node type you want to find embedding entities for.
     * @return 
     */
    public List<EntityEmbedding> findEntitiesThatEmbed(String builder){
        List<EntityEmbedding> result = new ArrayList<EntityEmbedding>();
        for (Class<?> entityClass : entityRegistry.values()) {
            for (Field field : entityClass.getDeclaredFields()) {
                Embedded embedded = field.getAnnotation(Embedded.class);
                if (embedded != null) {
                    if ( equalsNotBlank(builder, embedded.builder())) {
                        Entity entity = entityClass.getAnnotation(Entity.class);
                        result.add(new EntityEmbedding(embedded.relationRole(), entity.builder(), builder, entityClass, entity.root(), embedded.queryDirection()));
                     }
                }
            }
        }
        return result;
    }

    public static String determineBuilder(final Class<?> clazz) {
        return clazz.getAnnotation(Entity.class) != null ? clazz.getAnnotation(Entity.class).builder() : null;
    }

    public static boolean entityIsRoot(Object o){
        Class<? extends Object> c = o.getClass();
        Entity e = (Entity) c.getAnnotation(Entity.class);
        return e != null && e.root() == true;
    }
    
    static Class<?> retrieveActualType(final Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static boolean equalsNotBlank(String s1, String s2) {
        return !StringUtils.isBlank(s1) && s1.equals(s2);
    }
}

package nl.vpro.mmbase.vob;

import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

import nl.vpro.mmbase.vob.annotations.*;
import nl.vpro.mmbase.vob.converters.FieldConverter;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.NodeMap;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class Populator{
    private static Logger log = Logging.getLoggerInstance(Populator.class.getName());

    
    private final QueryHelper queryHelper;
    private final EntityConfigLoader entityConfigLoader;

    /**
     * Instantiates a DefaultQueryHelper to retrieve associations.
     * 
     * @param pathToScan The base path the scan for entities
     */
    public Populator(EntityConfigLoader entityConfigLoader, QueryHelper queryHelper) {
        this.queryHelper = queryHelper;
        this.entityConfigLoader = entityConfigLoader;
    }

    public Populator(final EntityConfigLoader entityConfigLoader ){
        this(entityConfigLoader, new DefaultQueryHelper());
    }
    
    public EntityConfigLoader getEntityConfigLoader(){
        return this.entityConfigLoader;
    }

    /**
     * Call this method to convert a node to a graph of corresponding objects,
     * including all annotated associations etc. This method will be called
     * recursively and is stateless, as are the methods called from here.
     * 
     * 
     * TODO: do we really need to provide the builderName ourselves? currently
     * in here to simplify testing. Fix this, before it becomes a feature!!
     * 
     * @param node The concrete node which should be unmarshalled
     * @param builderName The name of the builder
     * @return The populated object graph
     */
    public Object unmarshallNode(final Node node, final String builderName) {
        Object target = null;
        if (entityConfigLoader.hasEntityFor(builderName)) {
            try {
                target = entityConfigLoader.getEntityRegistry().get(builderName).newInstance();
                copyBasicProperties(target, node);
                copyEmbeddeds(target, node);
                copyAssociations(target, node);

            } catch (final Exception e) {
                log.error("unable to unmarshall node " + node);
            }
        } else {
            log.debug("no entity mapping for builder " + builderName);
        }
        return target;
    }

    private void copyBasicProperties(final Object target, final Node node) {
        final NodeMap nodeMap = new NodeMap(node);
        final Set<Map.Entry<String, Object>> fieldNameSet = uncheckedCast(nodeMap.entrySet());
        for (final Map.Entry<String, Object> entry : fieldNameSet) {
            final String fieldName = entry.getKey();
            // final Object fieldValue = entry.getValue();
            populateField(target, fieldName, node);
        }
    }

    private void copyEmbeddeds(final Object target, final Node node) {
        for (final Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final Embedded embeddedAnnotation = field.getAnnotation(Embedded.class);
            if (embeddedAnnotation != null) {
                final String srcBuilder = EntityConfigLoader.determineBuilder(target.getClass());

                try {
                    final Object result = retrieveEmbedded(node, srcBuilder, embeddedAnnotation);
                    field.set(target, result);
                } catch (final Exception e) {
                    log.warn("unable to populate embedded field: "+e.getMessage());
                }
            }
        }
    }

    private Object retrieveEmbedded(final Node node, final String srcBuilder, final Embedded embeddedAnnotation) {
        final String targetBuilder = embeddedAnnotation.builder();
        final String relationRole = embeddedAnnotation.relationRole();
        final String path = String.format("%s,%s,%s", srcBuilder, relationRole, targetBuilder);

        final String targetField = String.format("%s.%s", targetBuilder, embeddedAnnotation.field());
        //TODO: parhaps the order field is a relation field?
        final String orderField = String.format("%s.%s", targetBuilder, embeddedAnnotation.orderField());

        final List<Node> virtualNodes = queryHelper.query(node.getNumber(), path, targetField, orderField,
                embeddedAnnotation.orderDirection(), embeddedAnnotation.queryDirection(), QueryLimit.single());

        if (virtualNodes.size() > 0) {
            try {
                final FieldConverter fieldConvertor = embeddedAnnotation.convertor().newInstance();
                return fieldConvertor.convert(virtualNodes.get(0), targetField);
            } catch (final Exception e) {
                log.warn(String.format("unable to convert returntype. reason: %s", e.getMessage()));
            }
        }
        return null;
    }

    private void copyAssociations(final Object target, final Node node) {
        for (final Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            final Rel relAnnotation = field.getAnnotation(Rel.class);
            final PosRel posrelAnnotation = field.getAnnotation(PosRel.class);

            if (posrelAnnotation != null) {
                final Class<?> relatedType = EntityConfigLoader.retrieveActualType(field);
                final String srcBuilder = EntityConfigLoader.determineBuilder(target.getClass());
                final String targetBuilder = EntityConfigLoader.determineBuilder(relatedType);
                final List<?> retrievedPosRel = retrievePosRel(node, relatedType, srcBuilder, targetBuilder,
                        posrelAnnotation);
                safeSet(target, field, retrievedPosRel);
            } else if (relAnnotation != null) {
                final Class<?> relatedType = EntityConfigLoader.retrieveActualType(field);
                final String srcBuilder = EntityConfigLoader.determineBuilder(target.getClass());
                final String targetBuilder = EntityConfigLoader.determineBuilder(relatedType);
                final List<?> retrievedRel = retrieveRel(node, relatedType, srcBuilder, targetBuilder, relAnnotation);
                safeSet(target, field, retrievedRel);
            }
        }
    }

    private static void safeSet(final Object target, final Field field, final Object retrievedPosRel) {
        field.setAccessible(true);
        try {
            field.set(target, retrievedPosRel);
        } catch (final Exception e) {
            log.error("unable to set target field " + field.getName() + " on object " + target);
        }
    }

    private <T> List<T> retrievePosRel(final Node node, final Class<T> relatedType, final String srcBuilder,
            final String targetBuilder, final PosRel posRel) {

        final String path = String.format("%s,posrel,%s", srcBuilder, targetBuilder);
        final String targetField = String.format("%s.number", targetBuilder);
        final String orderField = "posrel.pos";

        final List<Node> nodes = queryHelper.query(node.getNumber(), path, targetField, orderField,
                posRel.orderDirection(), posRel.queryDirection(), QueryLimit.unlimited());
        //final List<Node> nodes = loadNodes(virtualNodes, targetField);
        return unmarshallList(relatedType, nodes);
    }

    private <T> List<T> retrieveRel(final Node node, final Class<T> relatedType, final String srcBuilder,
            final String targetBuilder, final Rel rel) {

        final String path = String.format("%s,%s", srcBuilder, targetBuilder);
        final String targetField = String.format("%s.number", targetBuilder);
        final String orderField = String.format("%s.%s", targetBuilder, rel.orderField());

        final List<Node> nodes = queryHelper.query(node.getNumber(), path, targetField, orderField, rel
                .orderDirection(), rel.queryDirection(), QueryLimit.unlimited());
        return unmarshallList(relatedType, nodes);
    }


    @SuppressWarnings("unchecked")
    private <T> List<T> unmarshallList(final Class<T> relatedType, final List<Node> nodes) {
        final List<T> results = new ArrayList<T>(nodes.size());
        for (final Node n : nodes) {
            try {
                String builderName = EntityConfigLoader.determineBuilder(relatedType);
                final T relatedObject = (T) unmarshallNode(n, builderName);
                results.add(relatedObject);
            } catch (final Exception e) {
                log.warn("unable to instantiate related object of type " + relatedType
                        + " , no accessible contructor present?");
            }
        }
        return results;
    }

    

    public static Class<?> determineParametrizedType(final Type type) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType paramType = (ParameterizedType) type;
            final Type[] typeArguments = paramType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
    }


    /**
     * Set the value of an Entity property based on a node field value. this is
     * done when a property of matching name is found in the Entity object, or
     * when there is an entity property with a Field annotation that maps that
     * property to this node field. If a Convertor is set through a Field
     * annotation, it is applied before setting the property.
     * 
     * @param target is the Entity object the field value is set on.
     * @param fieldName name of node field that is being set.
     * @param node that the value is copied from
     */
    static void populateField(final Object target, final String fieldName, final Node node) {
        final Field[] fields = target.getClass().getDeclaredFields();
        try {
            for (final Field field : fields) {
                final nl.vpro.mmbase.vob.annotations.Field fieldAnnotation = field
                        .getAnnotation(nl.vpro.mmbase.vob.annotations.Field.class);
                if (fieldMatchesWithoutAnnotatedFieldMapping(fieldName, field, fieldAnnotation)
                        || AnnotatedFieldMappingMatchesNodeField(fieldName, fieldAnnotation)) {
                    setFieldValue(target, fieldName, node, field, fieldAnnotation);
                    return;
                }
            }
        } catch (final Exception e) {
            log.warn(String.format("unable to set property %s on object %s. reason: %s", fieldName, target, e.getMessage()));
        }
    }

    private static boolean AnnotatedFieldMappingMatchesNodeField(final String fieldName,
            final nl.vpro.mmbase.vob.annotations.Field fieldAnnotation) {
        return fieldAnnotation != null && fieldAnnotation.nodeField().equalsIgnoreCase(fieldName);
    }

    private static boolean fieldMatchesWithoutAnnotatedFieldMapping(final String fieldName, final Field field,
            final nl.vpro.mmbase.vob.annotations.Field fieldAnnotation) {
        return field.getName().equalsIgnoreCase(fieldName)
                && (fieldAnnotation == null || "".equals(fieldAnnotation.nodeField()));
    }

    private static void setFieldValue(final Object target, final String fieldName, final Node node, final Field field,
            final nl.vpro.mmbase.vob.annotations.Field fieldAnnotation) throws IllegalAccessException,
            InstantiationException {
        field.setAccessible(true);
        if (fieldAnnotation != null) {
            field.set(target, fieldAnnotation.convertor().newInstance().convert(node, fieldName));
        } else {
            field.set(target, node.getObjectValue(fieldName));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T uncheckedCast(final Object obj) {
        return (T) obj;
    }
}

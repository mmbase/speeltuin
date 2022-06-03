package nl.vpro.mmbase.vob;

import java.io.IOException;
import java.util.Map;

import nl.vpro.mmbase.vob.annotations.Entity;

import org.apache.log4j.Logger;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Classpath scanner, retrieves entities from the classpath. Uses Springs'
 * annotationscanner to do so.
 * 
 * @author peter.maas@finalist.com
 */
public final class EntityFilter implements TypeFilter {
  private static final Logger log = Logger.getLogger(EntityFilter.class);
  private final Map<String, Class<?>> entityRegistry;

  public EntityFilter(final Map<String, Class<?>> entityRegistry) {
    this.entityRegistry = entityRegistry;
  }

  public boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {
    final boolean matches = isEntity(metadataReader);
    if (matches) {
      final ClassMetadata classMetadata = metadataReader.getClassMetadata();
      if (classMetadata.isConcrete()) {
        try {
          Class<?> clazz = getClass().getClassLoader().loadClass(classMetadata.getClassName());
          final String builder = EntityConfigLoader.determineBuilder(clazz);
          entityRegistry.put(builder, clazz);
        } catch (final ClassNotFoundException e) {
          log.warn("unable to load class: " + classMetadata.getClassName(), e);
        }
      }
    }
    return matches;
  }

  private boolean isEntity(final MetadataReader metadataReader) {
    return metadataReader.getAnnotationMetadata().hasAnnotation(Entity.class.getName());
  }
}

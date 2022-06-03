package nl.vpro.mmbase.vob;

import static org.junit.Assert.*;

import java.util.List;

import nl.vpro.mmbase.vob.EntityConfigLoader.*;
import nl.vpro.mmbase.vob.domain.*;

import org.junit.Before;
import org.junit.Test;
public class EntityConfigLoaderTest {
    
    public static final String SCAN_CLASSPATH = "nl.vpro.mmbase.vob.domain";
    private EntityConfigLoader entityConfigLoader;
    
    @Before
    public void init(){
        entityConfigLoader = new EntityConfigLoader(SCAN_CLASSPATH);
    }
    
    @Test
    public void testEntityIsRoot(){
        
        assertTrue(EntityConfigLoader.entityIsRoot(new NewsItem()));
        assertFalse(EntityConfigLoader.entityIsRoot(new Image()));
    }

    @Test
    public void testFindEntitiesThatEmbed(){
        List<EntityEmbedding> embeddings = entityConfigLoader.findEntitiesThatEmbed("mmevents");
        
        assertEquals(2, embeddings.size());
        EntityEmbedding emb =embeddings.get(1); 
        assertEquals("news", emb.embeddingType);
        assertEquals("mmevents", emb.embeddedType);
        assertEquals("posrel", emb.relationRole);
        assertEquals(NewsItem.class, emb.EntityClass);
        assertEquals(true, emb.isRoot);
        assertEquals(QueryDirection.BOTH  , emb.queryDirection);
        
        emb = embeddings.get(0);
        assertEquals("dummy", emb.embeddingType);
        assertEquals(QueryDirection.DESTINATION, emb.queryDirection);
        assertEquals("related", emb.relationRole);
    }

    @Test
    public void testFindAggregatedEntitiesFor(){
        List<EntityAggregation> aggregates = entityConfigLoader.findAggregatedEntitiesFor(NewsItem.class);
        assertEquals(3, aggregates.size());
        
        EntityAggregation ea = aggregates.get(0);
        assertEquals(Image.class, ea.aggregatedEntityClass);
        assertEquals(NewsItem.class, ea.aggregatingEntityClass);
        assertEquals(QueryDirection.SOURCE, ea.queryDirection);
        assertEquals("posrel", ea.relationRole);
        assertEquals(Tag.class, aggregates.get(1).aggregatedEntityClass);
        assertEquals(Paragraph.class, aggregates.get(2).aggregatedEntityClass);
        
        aggregates = entityConfigLoader.findAggregatedEntitiesFor(Paragraph.class);
        assertEquals(2, aggregates.size());
        assertEquals(Image.class, aggregates.get(0).aggregatedEntityClass);
        assertEquals(Dummy.class, aggregates.get(1).aggregatedEntityClass);
    }

    @Test
    public void testFindPathsToAggregatingEntities(){
        List<Path> paths = entityConfigLoader.findPathsToAggregatingEntities(Image.class);
        assertEquals(3, paths.size());
        
        Path path = paths.get(0);
        assertEquals("news,posrel,images", path.pathAsString());
        assertEquals("source", path.queryDirectionsAsString());
        
        path = paths.get(1);
        assertEquals("news,posrel,paragraphs,posrel,images", path.pathAsString());
        assertEquals("source,destination", path.queryDirectionsAsString());
        
        path = paths.get(2);
        assertEquals("news,posrel,paragraphs,related,dummy,posrel,images", path.pathAsString());
        assertEquals("source,destination,destination", path.queryDirectionsAsString());
    }

    //@Test
    public void testFindEntityThatEmbeds() {
        Class<?> c = entityConfigLoader.findEntityThatEmbeds("posrel", "mmevents");
        assertEquals(NewsItem.class, c);
    
        c = entityConfigLoader.findEntityThatEmbeds("related", "mmevents");
        assertNull(c);
    }

    // @Test
    public void thePopulatorCanDetermineBuildersFromEntities() {
        assertEquals("images", entityConfigLoader.determineBuilder(Image.class));
        assertEquals("news", entityConfigLoader.determineBuilder(NewsItem.class));
    }

}

package nl.vpro.mmbase.vob;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.vpro.mmbase.vob.domain.Image;
import nl.vpro.mmbase.vob.domain.NewsItem;
import nl.vpro.mmbase.vob.util.Mapper;

import org.junit.Before;
import org.junit.Test;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.MapNode;

public class PopulatorTest {
    
    private EntityConfigLoader entityConfigLoader;
    
    
    @Before
    public void init(){
        entityConfigLoader = new EntityConfigLoader(EntityConfigLoaderTest.SCAN_CLASSPATH);
    }

    

    @Test
    public void testUnmarshallNode() throws IllegalArgumentException, IllegalAccessException, SecurityException,
            NoSuchFieldException {
        Populator populator = new Populator(entityConfigLoader,  new NoResultsQueryHelper());

        Node newsItemNode = createDefaultNodeForNewsitem();
        NewsItem item = (NewsItem) populator.unmarshallNode(newsItemNode, "news");

        assertEquals(Long.valueOf(1234), item.getNumber());
        assertEquals("title", item.getTitle());
    }

    /**
     * The field annotation can be used with just a converter
     */
    @Test
    public void testFieldConverterOnFieldAnnotation() throws IllegalArgumentException, IllegalAccessException,
            SecurityException, NoSuchFieldException {
        Populator populator = new Populator(entityConfigLoader,  new NoResultsQueryHelper());

        MapNode newsItemNode = new Mapper().put("number", Long.valueOf(1234)).put("subtitle", "Subtitle").getNode();

        NewsItem item = (NewsItem) populator.unmarshallNode(newsItemNode, "news");

        assertEquals(Long.valueOf(1234), item.getNumber());
        assertEquals("subtitle", item.getSubtitle());
    }

    @Test
    public void testUnmarshallNodeWithAssociations() throws IllegalArgumentException, IllegalAccessException,
            SecurityException, NoSuchFieldException {

        DummyQueryHelper queryHelper = new DummyQueryHelper("images", 
                new Mapper()
                    .put("number", Long.valueOf(12345))
                    .put("title", "image title").getNode());
        
        Populator populator = new Populator(entityConfigLoader,  queryHelper);
        MapNode newsItemNode = new Mapper()  
            .put("number", Long.valueOf(1234))
            .put("title", "title").getNode();

        NewsItem item = (NewsItem) populator.unmarshallNode(newsItemNode, "news");

        assertEquals(Long.valueOf(1234), item.getNumber());
        assertEquals("title", item.getTitle());
        assertEquals(1, item.getImages().size());
        Image image = item.getImages().get(0);
        assertEquals(new Long(12345), image.getNumber());
        assertEquals("image title", image.getTitle());
    }
    
    

    @Test
    public void testUnmashallNodeWithEmbedded() {
        
        Node returnNode = new Mapper()
            .put("mmevents.number", Long.valueOf(12345))
            .put("mmevents.start", Long.valueOf(createMyBirthday().getTimeInMillis() / 1000L))
            .getNode();
        DummyQueryHelper queryHelper = new DummyQueryHelper("mmevents", "posrel", returnNode);
        Populator populator = new Populator(entityConfigLoader,  queryHelper);

        Node newsNode = createDefaultNodeForNewsitem();
        NewsItem item = (NewsItem) populator.unmarshallNode(newsNode, "news");
        
        assertNotNull(item.getCreated());
        Calendar c = new GregorianCalendar();
        c.setTime(item.getCreated());
        assertEquals(1970, c.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, c.get(Calendar.MONTH));
        assertEquals(19, c.get(Calendar.DAY_OF_MONTH));
    }
    
    /**
     * 
     * @param cloud use mock cloud if this node is triggering a query
     * @return
     */
    private Node createDefaultNodeForNewsitem() {
        MapNode newsItemNode = new Mapper()  
            .put("number", Long.valueOf(1234))
            .put("title", "title").getNode();
        return newsItemNode;
    }
    
     private Calendar createMyBirthday() {
        Calendar c = new GregorianCalendar();
        c.set(1970, Calendar.FEBRUARY, 19);
        return c;
    }

}

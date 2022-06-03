

package nl.vpro.mmbase.republisher;

import static org.junit.Assert.*;

import java.util.List;

import nl.vpro.mmbase.republisher.domain.mynews.News;
import nl.vpro.mmbase.vob.*;

import org.junit.Before;
import org.junit.Test;
import org.mmbase.bridge.Node;

public class MyNewsDummyTest {
    
    private MyNewsDummy dummy;

    @Before
    public void setup(){
        dummy = new MyNewsDummy();
    }
    
    @Test
    public void testMyNewsDummy(){
        Node newsNode = dummy.getNews();
        
        assertEquals("test news", newsNode.getStringValue("title"));
        assertEquals(1234, newsNode.getIntValue("number"));
        List<Node> list;
        
        list = dummy.query(-1, "news,images", "", "", Direction.ASC, QueryDirection.BOTH, QueryLimit.unlimited());
        assertEquals(2, list.size());
        assertEquals("img1", list.get(0).getStringValue("title"));
        assertEquals(10, list.get(0).getIntValue("number"));
        assertEquals(10, list.get(0).getNumber());
        
        assertEquals("img2", list.get(1).getStringValue("title"));
        assertEquals(11, list.get(1).getIntValue("number"));
        
        list = dummy.query(-1, "news,people", "", "", Direction.ASC, QueryDirection.BOTH, QueryLimit.unlimited());
        assertEquals(2, list.size());
        assertEquals("Ernst", list.get(0).getStringValue("firstname"));
        assertEquals("Bunders", list.get(0).getStringValue("lastname"));
        
        assertEquals("Bill", list.get(1).getStringValue("firstname"));
        
        list = dummy.query(-1, "news,urls", "", "", Direction.ASC, QueryDirection.BOTH, QueryLimit.unlimited());
        assertEquals(2, list.size());
    }
    
    @Test
    public void testPopulatorWithMyNewsDummy(){
            MyNewsDummy queryHelper = new MyNewsDummy();
            EntityConfigLoader entityConfigLoader = new EntityConfigLoader("nl.vpro.mmbase.republisher.domain");
            Populator populator = new Populator(entityConfigLoader, queryHelper);
            Node newsNode = queryHelper.getNews();
            
            News news = (News) populator.unmarshallNode(newsNode, "news");
            assertEquals("test news", news.getTitle());
            assertEquals(1234, news.getNumber());
            
            assertEquals(2, news.getUrls().size());
            assertEquals("url1", news.getUrls().get(0).getDescription());
            assertEquals(20, news.getUrls().get(0).getNumber());
            assertEquals("url2", news.getUrls().get(1).getDescription());
            
            assertEquals(2, news.getPeople().size());
            assertEquals("Ernst", news.getPeople().get(0).getFirstname());
            assertEquals(30, news.getPeople().get(0).getNumber());
            
            assertEquals(2, news.getImages().size());
            assertEquals(10, news.getImages().get(0).getNumber());
            assertEquals("img1", news.getImages().get(0).getTitle());
            
            
    }

}

package nl.vpro.mmbase.republisher;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.mmbase.vob.*;
import nl.vpro.mmbase.vob.util.Mapper;

import org.mmbase.bridge.*;


/**
 * This queryhelper will produce a dummy mynews article.
 * @author ernst
 *
 */
public class MyNewsDummy implements QueryHelper {

    
    
    public Node getNews(){
        return new Mapper().put("title", "test news").put("number", 1234).getNode();
    }
    
    public List<Node> getPeople(){
        return new ListWrapper<Node>()
        .add(new Mapper().put("firstname","Ernst").put("lastname","Bunders").put("number", 30).getNode())
        .add(new Mapper().put("firstname","Bill").put("lastname","Gates").put("number", 31).getNode())
        .toList();
    }
    
    public List<Node> getUrls(){
        return new ListWrapper<Node>()
        .add(new Mapper().put("description","url1").put("number", 20).getNode())
        .add(new Mapper().put("description","url2").put("number", 21).getNode())
        .toList();
    }
    
    public List<Node> getImages(){
        return new ListWrapper<Node>()
        .add(new Mapper().put("title","img1").put("number", 10).getNode())
        .add(new Mapper().put("title","img2").put("number", 11).getNode())
        .toList();
    }

    public List<Node> query(int startNumber, String path, String fields, String sortField, Direction dir,
            QueryDirection queryDir, QueryLimit limit) {
        if(path.contains("people")){
            return getPeople();
        }else if(path.contains("images")){
            return getImages();
        }else if(path.contains("urls")){
            return getUrls();
        }
        return null;
    }
    
    public void setCloud(Cloud cloud) {}

    private static class ListWrapper <T> {
        List<T> wrapped = new ArrayList<T>();
        public ListWrapper<T>add(T entry){
            wrapped.add(entry);
            return this;
        }
        public List<T> toList(){return wrapped;}
    }

}

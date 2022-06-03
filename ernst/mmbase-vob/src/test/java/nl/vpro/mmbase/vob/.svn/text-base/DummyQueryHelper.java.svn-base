package nl.vpro.mmbase.vob;

import java.util.Collections;
import java.util.List;

import nl.vpro.mmbase.vob.util.Mapper;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

public class DummyQueryHelper implements QueryHelper{
    private String path;
    Node node;

    public DummyQueryHelper(String nodeType, Long nodenrToReturn) {
        path = nodeType;
        node = new Mapper().put("number", nodenrToReturn).getNode();
    }

    public DummyQueryHelper(String nodeType, Long nodenrToReturn, String relationRole) {
        path = relationRole + "," + nodeType;
        node = new Mapper().put("number", nodenrToReturn).getNode();
    }
    
    public DummyQueryHelper(String nodeType, String relationRole, Node returnNode) {
        this.path = relationRole + "," + nodeType;
        node = returnNode;
    }
    
    public DummyQueryHelper(String nodeType, Node returnNode) {
        this.path =  nodeType;
        node = returnNode;
    }

    public List<Node> query(int startNumber, String path, String fields, String sortField, Direction dir,
            QueryDirection queryDir, QueryLimit limit) {

        if (path.contains(this.path)) {
            return Collections.singletonList(node);
        } else {
            return Collections.emptyList();
        }
    }

    public void setCloud(Cloud cloud) {
    }

}

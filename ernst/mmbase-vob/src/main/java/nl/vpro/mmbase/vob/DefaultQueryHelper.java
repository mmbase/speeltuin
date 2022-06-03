package nl.vpro.mmbase.vob;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;

/**
 * Default implementation, almost one-to-one patch trough to mmbase with some
 * minor enhancements, like explicit typing for sorting and query directions.
 * 
 * @author peter.maas@finalist.com
 */
public class DefaultQueryHelper implements QueryHelper {
    private static final EnumMap<Direction, String> remapSortDirection = new EnumMap<Direction, String>(Direction.class);
    static {
        remapSortDirection.put(Direction.ASC, "up");
        remapSortDirection.put(Direction.DESC, "down");
    }

    private Cloud cloud;

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public List<Node> query(int startNumber, String path, String fields, String sortField, Direction dir, QueryDirection queryDir, QueryLimit limit) {
        String queryDirectionAsString = queryDir.toString().toLowerCase();
        Query query = Queries.createQuery(cloud, String.valueOf(startNumber), path, fields, null, sortField, remapSortDirection.get(dir), queryDirectionAsString, true);

        if (limit.isLimited()) {
            query.setMaxNumber(limit.getMax());
        }

        return executeQuery(cloud, query);
    }

    private List<Node> executeQuery(Cloud cloud, Query query) {
        List<Node> nodes = new ArrayList<Node>();
        NodeList list = cloud.getList(query);
        NodeIterator nodeIterator = list.nodeIterator();
        while (nodeIterator.hasNext()) {
            nodes.add(nodeIterator.nextNode());
        }
        return nodes;
    }
}

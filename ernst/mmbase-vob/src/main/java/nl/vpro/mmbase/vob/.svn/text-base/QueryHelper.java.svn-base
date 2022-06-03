package nl.vpro.mmbase.vob;

import java.util.List;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

/**
 * Wrapper interface for the Query utility in mmbase, to allow for mocking and
 * alternative implementations.
 * 
 * @author peter.maas@finalist.com
 */
public interface QueryHelper {


  /**
   * @param cloud
   *          The cloud to query
   * @param startNumber
   *          The startnode of the path
   * @param path
   *          The actual path
   * @param fields
   *          The fields to retrieve
   * @param sortField
   *          The field to sort by
   * @param dir
   *          The direction to sort in
   * @param queryDir
   *          The direction to query in
   * @param limit
   *          The maximum number of nodes to retrieve
   * @return A list of resulting nodes. This must be a real node
   */
  public List<Node> query(int startNumber, String path, String fields, String sortField, Direction dir, QueryDirection queryDir, QueryLimit limit);
  public void setCloud(Cloud cloud);
}

package org.mmbase.module.corebuilders;

import junit.framework.*;
import org.mmbase.bridge.*;
import java.util.*;

/**
 * JUnit tests for TypeRel
 *
 * @author  Michiel Meeuwissen 
 * @version $Id: TypeRelTest.java 35493 2009-05-28 22:44:29Z michiel $
 */
public class TypeRelTest extends TestCase {

    static protected String UNIDIR_ROLE = "unidirectionalrelation";
    static protected String  BIDIR_ROLE = "bidirectionalrelation";
    static protected String  INH_ROLE   = "inheritancerelation";
    static protected String  OTHER_ROLE   = "this_role_does_not_exist";
    static protected String  RELATED_ROLE   = "related";

    static protected String  SOURCE        = "source";
    static protected String  DESTINATION   = "destination";
    static protected String  BOTH          = "both";


    static protected Cloud cloud = null;
    static protected NodeManager relDefManager;
    static protected NodeManager typeRelManager;
    static protected NodeManager insRelManager; 
    static protected NodeManager newsManager; 
    static protected NodeManager urlsManager; 
    static protected NodeManager objectManager; 
    static protected NodeList    createdNodes;

    static protected Node        news;
    static protected Node        url;
    static protected Node        object;
    static protected Node        typerel;

    public TypeRelTest(String testName) {
        super(testName);
    }


    public void testListRelations() {       
        RelationManagerList rml = cloud.getRelationManagers();
        assertTrue(rml != null);
        assertTrue(rml.size() > 0);
        if (rml.size() == 0) {
            fail("cannot test");
        }
        
    }
    protected Node createRelDefNode(String role, int dir) {
        // create a new relation-definition
        Node reldef = relDefManager.createNode();
        reldef.setValue("sname", role);
        reldef.setValue("dname", role);
        reldef.setValue("sguiname", role);
        reldef.setValue("dguiname", role);
        reldef.setIntValue("dir", dir);
        reldef.setNodeValue("builder", insRelManager);
        reldef.commit();
        createdNodes.add(reldef);
        return reldef;
    }

    /**
     * Create bidirection relation type, and check if relationmanager in both directions can be found.
     */
    public void testBidirectionalCloud1() {
        Node reldef = createRelDefNode(BIDIR_ROLE, 2);
        typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);

        // now this relation must exist.
        

        // check if it can be found by cloud
        RelationManagerList rml = cloud.getRelationManagers(newsManager, urlsManager, BIDIR_ROLE);
        assertTrue(rml.size() > 0);          
        assertTrue(rml.contains(typerel));
    }

    public void testBidirectionalCloud2() {
        RelationManagerList rml = cloud.getRelationManagers(urlsManager, newsManager, BIDIR_ROLE);
        assertTrue(rml.size() > 0);          
        assertTrue(rml.contains(typerel));
    }


    public void testBidirectionalNodeManagerAllowedRelations1() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations((NodeManager) null, null, null);
        assertTrue(rml.contains(typerel));
    }
    public void testBidirectionalNodeManagerAllowedRelations2() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, null, null);
        assertTrue(rml.contains(typerel));
    }
    public void testBidirectionalNodeManagerAllowedRelations3() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, null);
        assertTrue(rml.contains(typerel));
    }


    public void testBidirectionalNodeManagerAllowedRelations4() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, DESTINATION);
        assertTrue(rml.contains(typerel));
    }
    public void testBidirectionalNodeManagerAllowedRelations5() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, SOURCE);
        assertFalse(rml.contains(typerel));
    }
    public void testBidirectionalNodeManagerAllowedRelations6() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, BOTH);
        assertTrue(rml.contains(typerel));
    }
    public void testBidirectionalNodeManagerAllowedRelations7() {
        // by source-manager
        try {
            RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, OTHER_ROLE, null);
            fail("Should have thrown exception for non-existing relations");
        } catch (NotFoundException e) {
        };
    }
    public void testBidirectionalNodeManagerAllowedRelations8() {
        // by source-manager        
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, RELATED_ROLE, null);
        assertFalse(rml.contains(typerel));
    }

    public void testBidirectionalNodeManagerAllowedRelations9() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations();
        assertTrue(rml.contains(typerel));
    }


    public void testBidirectionalNodeManagerAllowedRelations10() {
        // by destination-manager
        RelationManagerList rml = urlsManager.getAllowedRelations();
        assertTrue(rml.contains(typerel));
    }


        

    public void testBidirectionalNode1() {
        RelationManager rm = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, DESTINATION).getRelationManager(0);
        Relation r = rm.createRelation(news, url);
        r.commit();
        createdNodes.add(r);
        // no exception should have occured.
    }

    public void testBidirectionalNode2() {
        RelationManager rm = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, DESTINATION).getRelationManager(0);
        Relation r = rm.createRelation(url, news);
        r.commit();
        createdNodes.add(r);
    }

    public void testBidirectionalNode3() {
        RelationManager rm = newsManager.getAllowedRelations(urlsManager, BIDIR_ROLE, DESTINATION).getRelationManager(0);
        try {
            Relation r = rm.createRelation(news, object);
            r.commit();
            createdNodes.add(r);
            fail("Should not have been allowed");
        } catch (BridgeException e) {
        }
        // no exception should have occured.
    }


    public void testBidirectionalNode4() {
        // make sure it is the right direction now.
        NodeList nl = news.getRelatedNodes(urlsManager, BIDIR_ROLE, null);
        assertTrue("" + nl, nl.contains(url));
    }        
    public void testBidirectionalNode5() {
        NodeList nl = url.getRelatedNodes(newsManager);
        assertTrue(nl.contains(news));
    }                   


    
    /*
     * Create unidirection relation type, and check if relationmanager in only one direction can be found.
     */

    public void testUnidirectionalCloud1() {
        Node reldef = createRelDefNode(UNIDIR_ROLE, 1);

        typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", newsManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);


        // now this relation must exist.
        RelationManagerList rml = cloud.getRelationManagers(newsManager, urlsManager, UNIDIR_ROLE);
        assertTrue(rml.size() > 0);
        assertTrue(rml.contains(typerel));
    }

    public void testUnidirectionalCloud2() {
        RelationManagerList rml = cloud.getRelationManagers(urlsManager, newsManager, UNIDIR_ROLE);
        assertTrue(rml.size() == 0);
        assertFalse(rml.contains(typerel));
    }


    public void testUnidirectionalNodeManagerAllowedRelations1() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations((NodeManager) null, null, null);
        assertTrue(rml.contains(typerel));
    }
    public void testUnidirectionalNodeManagerAllowedRelations2() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, null, null);
        assertTrue(rml.contains(typerel));
    }
    public void testUnidirectionalNodeManagerAllowedRelations3() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, null);
        assertTrue(rml.contains(typerel));
    }


    public void testUnidirectionalNodeManagerAllowedRelations4() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, DESTINATION);
        assertTrue(rml.contains(typerel));
    }
    public void testUnidirectionalNodeManagerAllowedRelations5() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, SOURCE);
        assertFalse(rml.contains(typerel));
    }
    public void testUnidirectionalNodeManagerAllowedRelations6() {
        // by source-manager
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, BOTH);
        assertTrue(rml.contains(typerel));
    }
    public void testUnidirectionalNodeManagerAllowedRelations7() {
        try {
            RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, OTHER_ROLE, null);
            fail("Should have thrown exception for non-existing relations");
        } catch (NotFoundException e) {
        };
    }
    public void testUnidirectionalNodeManagerAllowedRelations8() {
        RelationManagerList rml = newsManager.getAllowedRelations(urlsManager, RELATED_ROLE, null);
        assertFalse(rml.contains(typerel));
    }

    public void testUnidirectionalNodeManagerAllowedRelations9() {
        RelationManagerList rml = newsManager.getAllowedRelations();
        assertTrue(rml.contains(typerel));
    }

    public void testUnidirectionalNodeManagerAllowedRelations10() {
        RelationManagerList rml = urlsManager.getAllowedRelations();
        assertFalse(rml.contains(typerel));
    }

        

    public void testUnidirectionalNode1() {
        RelationManager rm = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, DESTINATION).getRelationManager(0);
        Relation r = rm.createRelation(news, url);
        r.commit();
        createdNodes.add(r);
        // no exception should have occured.
    }

    public void testUnidirectionalNode2() {
        RelationManager rm = newsManager.getAllowedRelations(urlsManager, UNIDIR_ROLE, DESTINATION).getRelationManager(0);
        Relation r = rm.createRelation(url, news);
        r.commit();
        createdNodes.add(r);
                           
    }

    public void testUnidirectionalNode3() {
        // make sure it is the right direction now.
        NodeList nl = news.getRelatedNodes(urlsManager, UNIDIR_ROLE, null);
        assertTrue(nl.contains(url));
    }        
    public void testUnidirectionalNode4() {
        NodeList nl = url.getRelatedNodes(newsManager, UNIDIR_ROLE, null);
        assertFalse(nl.contains(news));
    }                   



    public void testInheritanceRelations() {
        Node reldef = createRelDefNode(INH_ROLE, 2);

        Node typerel = typeRelManager.createNode();
        typerel.setNodeValue("snumber", objectManager);
        typerel.setNodeValue("dnumber", urlsManager);
        typerel.setNodeValue("rnumber", reldef);
        typerel.commit();
        createdNodes.add(typerel);

        // now this relation must exist.
        RelationManagerList rm1 = cloud.getRelationManagers(objectManager, urlsManager, INH_ROLE);
        assertTrue(rm1.size() > 0);

        RelationManagerList rm2 = cloud.getRelationManagers(newsManager, urlsManager, INH_ROLE);
        assertTrue(rm2.size() > 0);       
    }



    public void testClearUpMess() {
        //        System.out.println("Clearing up the mess");
        NodeIterator i = createdNodes.nodeIterator();
        while (i.hasNext()) i.next(); // fast forward.

        while (i.hasPrevious()) {
            Node node = (Node) i.previous();
            System.out.print("D"); //eleting " + node);
            node.delete();
        }
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        if (cloud == null) {
            CloudContext cloudContext= ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext");
            HashMap user = new HashMap();
            user.put("username", "admin");
            user.put("password", "admin5k");
            cloud = cloudContext.getCloud("mmbase","name/password",user);
            
            // needed builders for this test.
            try {
                relDefManager  = cloud.getNodeManager("reldef");
                typeRelManager = cloud.getNodeManager("typerel");
                insRelManager  = cloud.getNodeManager("insrel");
                newsManager    = cloud.getNodeManager("news");
                urlsManager    = cloud.getNodeManager("urls");
                objectManager  = cloud.getNodeManager("object");
            } catch (NotFoundException e) {
                throw new Exception("Test cases cannot be performed because " + e.getMessage() + " Please arrange this in your cloud before running this TestCase.");
             }

            createdNodes = cloudContext.createNodeList();
            assertNotNull("Could not create remotely a nodelist" , createdNodes);

            news = newsManager.createNode();
            news.setValue("title", "test node");
            news.commit();
            createdNodes.add(news);
            
            url = urlsManager.createNode();
            url.setValue("url", "http://url");
            url.commit();
            createdNodes.add(url);

            object = objectManager.createNode();
            object.commit();
            createdNodes.add(object);

        }
    }
    
}
    

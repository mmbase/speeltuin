package nl.kennisnet.cpb.webservice.lucene;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.apache.lucene.index.Term;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.TermQuery;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.core.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;

public class Indexer extends IndexHelper implements NodeEventListener {

	ExecutorService service = Executors.newSingleThreadExecutor();

	private static Logger log = Logging.getLoggerInstance(Indexer.class.getName());

	public static boolean runningFullIndex = false;

	//	private static List newItemQueue = new ArrayList();

    /**
     * Name of the MMBase Lucene module.
     */
    public static final String LUCENE_MODULE_NAME = "cpblucenemodule";

    /**
     * Document node field.
     */
    public static final String NODE_FIELD = "node";

    /**
     * Document builder field.
     */
    public static final String BUILDER_FIELD = "builder";

    /**
     * Source field.
     */
    public static final String SOURCE_FIELD = "source";
    
    /** The mmbase instance */
    private MMBase mmbase = null;

    /**
     * 
     */
    
    public Indexer() {
        mmbase = (MMBase) Module.getModule("mmbaseroot", true);
    }

	/**
	 * Internal resolution used for dates in Lucene
	 */
	//public static final DateTools.Resolution INTERNAL_RESOLUTION = DateTools.Resolution.MILLISECOND;
	
	public void notify(NodeEvent event) {
		if ( !runningFullIndex) {
			String number = "" + event.getNodeNumber();
			if (log.isDebugEnabled()) {
			   log.debug("got notify:" + event.getNodeNumber());
			}
			switch (event.getType()) {
			case NodeEvent.TYPE_NEW:
			   if (log.isDebugEnabled()) {
			      log.debug("NodeEvent.TYPE_NEW");		
			   }
				service.execute(new IndexNodeTask(IndexAction.ADD, number));
				break;
			case NodeEvent.TYPE_DELETE:
			   if (log.isDebugEnabled()) {
			      log.debug("NodeEvent.TYPE_DELETE");
			   }
				service.execute(new IndexNodeTask(IndexAction.DELETE, number));
				break;
			case NodeEvent.TYPE_RELATION_CHANGE:
			   if (log.isDebugEnabled()) {
			      log.debug(" NodeEvent.TYPE_RELATION_CHANGE");
			   }
				service.execute(new IndexNodeTask(IndexAction.DELETE, number));
				service.execute(new IndexNodeTask(IndexAction.ADD, number));				
				break;
			case NodeEvent.TYPE_CHANGE:
			   if (log.isDebugEnabled()) {
			      log.debug(" NodeEvent.TYPE_CHANGE");				
			   }
				service.execute(new IndexNodeTask(IndexAction.DELETE, number));
				service.execute(new IndexNodeTask(IndexAction.ADD, number));
				break;
			}
		} else {
			log.warn("New item not indexed, full index task is running now");
		}
	}

	protected List < Integer > getNodesList() {
		log.debug("Start get node List for CE table");
		List < Integer > l = new ArrayList();
		Cloud cloud = nl.kennisnet.cpb.cloud.CloudFactory.getFullAccessCloud();
		NodeManager nm = cloud.getNodeManager("contentelementen");
	    NodeQuery q = nm.createQuery();
		HugeNodeListIterator iterator = new HugeNodeListIterator(q);
		while (iterator.hasNext()) {
			Node currentNode = iterator.nextNode();
			l.add(new Integer(currentNode.getNumber()));		
		}
		log.debug("End get node List for CE table returning "+ l.size() + " ids");
		return l;
	}

	public void registerObserver() {
        log.info("adding MMBase observer for TABLE: contentelementen");
        mmbase.addNodeRelatedEventsListener("contentelementen", this);
        log.info("adding MMBase observer for TABLE: images");
        mmbase.addNodeRelatedEventsListener("images", this);
	}

	protected synchronized int indexAll(boolean createIndex){
		int count = 0;
		Cloud cloud;
		IndexSearcher s;
		
		if (exists() && !createIndex) {
			forceOpen();
		} else {
        	create();
		}
		try {
		   if (log.isDebugEnabled()) {
            log.debug("Start indexing contentelementen");
         }
			cloud = nl.kennisnet.cpb.cloud.CloudFactory.getFullAccessCloud();
			NodeManager nm = cloud.getNodeManager("contentelementen");
		   NodeQuery q = nm.createQuery();
			HugeNodeListIterator iterator = new HugeNodeListIterator(q);
			s = getSearcher();
			while (iterator.hasNext()) {
				Node currentNode = iterator.nextNode();
				if (! isIndexed(s,"" + currentNode.getNumber())) {
					if (currentNode != null) {
						Document doc = getDocument(currentNode);
						write(doc);
						currentNode = null;
					} else {
						log.warn("No cloud.");
					}
					count ++;
					if (count % 500 == 0) {
					    flush();
					    close();
					    open();
					    if (log.isDebugEnabled()) {
					       log.debug("doccount=" + count);
					    }
				    }
				}
			}
			if (log.isDebugEnabled()) {
			   log.debug("End indexing contentelementen, " + count + " ce's inspected");
            log.debug("Start indexing images");
         }
			count = 0;
         nm = cloud.getNodeManager("images");
         q = nm.createQuery();
         iterator = new HugeNodeListIterator(q);
         s = getSearcher();
         while (iterator.hasNext()) {
            Node currentNode = iterator.nextNode();
            if (! isIndexed(s,"" + currentNode.getNumber())) {
               if (currentNode != null) {
                  Document doc = getImageDocument(currentNode);
                  write(doc);
                  currentNode = null;
               } else {
                  log.warn("No cloud.");
               }
               count ++;
               if (count % 500 == 0) {
                   flush();
                   close();
                   open();
                   if (log.isDebugEnabled()) {
                      log.debug("doccount=" + count);
                   }
                }
            }
         }
         if (log.isDebugEnabled()) {
            log.debug("End indexing images, " + count + " ce's inspected");
         }
			try {
				s.close();
			} catch (Exception e) {
				// ignore
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't open index for writing", e);
	    }finally {
			s = null;
			close();
			cloud =null;
		}
		return count;
	}

   private boolean isIndexed(IndexSearcher s, String id){	
		if (s != null) {
			TermQuery t = new TermQuery(new Term(Constants.NODE_FIELD, id));
			try {
				Hits hits = s.search(t);
				if (hits != null && hits.length() > 0) {
					return true;
				} 
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return false;
	}
	
	private void indexNode(String number) {
		Cloud c = nl.kennisnet.cpb.cloud.CloudFactory.getFullAccessCloud();
		if (c!= null) {
			Node node = c.getNode(number);
			indexNode(node);
		} else {
			log.warn("No cloud");
		}
	}
	
	protected void indexSingleNode(String number) {
		open();
		indexNode(number);
		close();
	}
	
	private void indexNode(Node node) {
		open();
		if (node != null) {
			//String builder = node.getNodeManager().getName();
		   Document doc = null;
		   if ("contentelementen".equals(node.getNodeManager().getName())) {
		      if (log.isDebugEnabled()) {
		         log.debug("Indexing content element");
		      }
		      doc = getDocument(node);
		   } else if ("images".equals(node.getNodeManager().getName())) {
		      if (log.isDebugEnabled()) {
               log.debug("Indexing image");
            }
		      doc = getImageDocument(node);		      
		   } 
		   if (doc != null) {
		      write(doc);		      
		   } else {
		      log.warn("Unknown node type, cannot be indexed:" + node);
		   }
         node = null;
		} else {
			log.warn("No cloud.");
		}		
	   flush();
		close();
	}	
	
	private void deleteNode(String number) {
		log.debug("Delete document from index");
		Term term = new Term(Constants.NODE_FIELD, number);
		delete(term);
	}
	
	/** Create an image document for use in the index
	 * @param currentNode
	 * @return
	 */
	private Document getImageDocument(Node node) {
	   Document doc = new Document();
      int number = node.getNumber();
            
      // always index the builder name and the node number for later
      doc.add(new Field(Constants.SOURCE_FIELD, "mmbase", Field.Store.YES, Field.Index.UN_TOKENIZED));
      doc.add(new Field(Constants.BUILDER_FIELD, "images", Field.Store.YES, Field.Index.UN_TOKENIZED));
      doc.add(new Field(Constants.NODE_FIELD, String.valueOf(number), Field.Store.YES, Field.Index.UN_TOKENIZED));
         
      // titel + alt tekst + redactienotitie, searchable
      doc.add(new Field(Constants.IMAGE_TITLE_FIELD, node.getStringValue("title"), Field.Store.YES, Field.Index.TOKENIZED));
      doc.add(new Field(Constants.IMAGE_DESCRIPTION_FIELD, node.getStringValue("description"), Field.Store.YES, Field.Index.TOKENIZED));
      doc.add(new Field(Constants.IMAGE_NOTITIE_FIELD, node.getStringValue("notitie"), Field.Store.YES, Field.Index.TOKENIZED));
      
      //image characteristics, read only
      doc.add(new Field(Constants.IMAGE_WIDTH_FIELD, node.getStringValue("width"), Field.Store.YES, Field.Index.NO));
      doc.add(new Field(Constants.IMAGE_HEIGHT_FIELD, node.getStringValue("height"), Field.Store.YES, Field.Index.NO));
      int filesize = 0;
      byte [] imagedata = node.getByteValue("handle");
      if (imagedata != null) {
         filesize = imagedata.length;
      }
      doc.add(new Field(Constants.IMAGE_FILESIZE_FIELD, String.valueOf(filesize), Field.Store.YES, Field.Index.NO));
      
      return doc;
   }

	private Document getDocument(Node node) {
		//only support CE
		Document doc = new Document();
		int number = node.getNumber();
		String type = node.getStringValue("elementtype");
		
		// always index the builder name and the node number for later
		doc.add(new Field(Constants.SOURCE_FIELD, "mmbase", Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(Constants.BUILDER_FIELD, "contentelementen", Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(Constants.NODE_FIELD, String.valueOf(number), Field.Store.YES, Field.Index.UN_TOKENIZED));
			
		// titel
		doc.add(new Field(Constants.TITEL_FIELD, node.getStringValue("titel"), Field.Store.YES, Field.Index.TOKENIZED));
		
		// intro & body
		StringBuffer buff = new StringBuffer();
		buff.append(node.getStringValue("body")); 
		buff.append(node.getStringValue("intro"));
		doc.add(new Field(Constants.TEXT_FIELD, buff.toString(), Field.Store.NO, Field.Index.TOKENIZED));
		
		// notitie
		doc.add(new Field(Constants.NOTITIE_FIELD, node.getStringValue("notitie"), Field.Store.NO, Field.Index.TOKENIZED));
		
		// elemenettype
		doc.add(new Field(Constants.ELEMENTTYPE_FIELD, type, Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		//boolean2: necessary to check if a kanaal is an agenda or not
		doc.add(new Field(Constants.BOOLEAN2_FIELD, node.getStringValue("boolean2"), Field.Store.NO, Field.Index.UN_TOKENIZED));
	
		// if this ce is in a channel add date
		// add the most recent modification date to index
		buff= new StringBuffer();
		Date d = new Date(0);
		if (!"Kanaal".equals(type)) {
			RelationList rl = node.getRelations("itemrel");
			if (rl != null && rl.size() > 0) {
				for (Iterator i = rl.iterator(); i.hasNext();) {
					Relation r = (Relation)i.next();
					
					// related kanaal id
					Node kn = r.getSource();
					buff.append(String.valueOf(kn.getNumber()) + " ");
					
					if (r != null) {
						Date n = r.getDateValue("startdate");
						if (d.getTime() == 0) {
							d = n;
						} else {
							if (d.before(n)) {
								d = n;
							}
						}
					}
				}
			}
			rl = null;
		}
		
		doc.add(new Field(Constants.KANAALMOD_FIELD, "" + d.getTime(), Field.Store.YES, Field.Index.UN_TOKENIZED));	
		doc.add(new Field(Constants.IN_KANAALEN_FIELD, "" + buff.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		// collect parent channel ids in a field
		buff = new StringBuffer();
		if ("Kanaal".equals(type)) {
			String afgeleid = node.getStringValue("boolean1");
			if (!"1".equals(afgeleid) && !"true".equals(afgeleid)) {
				RelationList rl = node.getRelations("parentrel");		
				if (rl != null && rl.size() > 0) {
					for (Iterator i = rl.iterator(); i.hasNext();) {
						Relation r = (Relation) i.next();
						Node k = r.getDestination();
						if (k != null) {
							buff.append(" " + k.getNumber());
						}
					}
				}
			}
		} 
		String s = buff.toString();
		if ("".equals(s)) {
			s="0";
		}
		doc.add(new Field(Constants.PARENTIDS_FIELD, s, Field.Store.YES, Field.Index.UN_TOKENIZED));	
			
		// add redactie groups to index
		buff = new StringBuffer();
		NodeList rl = node.getRelatedNodes("redactiegroepen");
		if (rl != null && rl.size() > 0) {
			for (Iterator i = rl.iterator(); i.hasNext();) {
				Node k = (Node)i.next();
				if (k != null) {
					buff.append(" " + k.getNumber());
				}
			}
		}
		rl = null;
		s = buff.toString();
		if ("".equals(s)) {
			s="0";
		}
		doc.add(new Field(Constants.REDACTIE_FIELD, s, Field.Store.YES, Field.Index.TOKENIZED));
	
		// add page links
		buff = new StringBuffer();
		rl = node.getRelatedNodes("categorieen");
		if (rl != null && rl.size() > 0) {
			for (Iterator i = rl.iterator(); i.hasNext();) {
				Node k = (Node)i.next();
				if (k != null) {
					buff.append(" " + k.getNumber());
				}
			}
		}
		rl = null;
		s = buff.toString();
		if ("".equals(s)) {
			s="0";
		}
		doc.add(new Field(Constants.PAGES_FIELD, s, Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		return doc;
	}
	
	private enum IndexAction {
		ADD,DELETE;
	}
	
	private class IndexNodeTask implements Runnable {
		/**
		 * The thread in which the external links will be checked
		 */

		private String number = "";
		private IndexAction action = null;
		
		IndexNodeTask(IndexAction a, String n){
			this.action = a;
			this.number = n;
		}
		
		public void run() {
			log.debug("Start index node task for id =" +number);
			if (IndexAction.ADD.equals(this.action)) {
				indexNode(this.number);
			} else if (IndexAction.DELETE.equals(this.action)) {
				deleteNode(this.number);
			}
			log.debug("End index node task for id =" +number);
		}
	}
}

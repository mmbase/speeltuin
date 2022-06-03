package nl.kennisnet.cpb.webservice.lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;

/**
 * This object handles access to Lucene indexes
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1.2.1 $
 */
public class IndexHelper {
	
	private static Log log = LogFactory.getLog(IndexHelper.class);

	private IndexWriter writer;
	
	private Analyzer analyzer = new StandardAnalyzer();

	private String internal_indexName;

	private String helperIndexPath;
	
	public void init() {
		internal_indexName = getPath() + getName();
		log.info("Using index path: "+ internal_indexName);
	}

	/**
	 * Sets the analyzer attribute of the IndexHelper object
	 * 
	 * @param a The new analyzer value
	 */
	public void setAnalyzer(Analyzer a) {
		this.analyzer = a;
	}

	/**
	 * Getter for the Analyzer in use in the Index
	 * 
	 * @return an instance of the analyzer
	 */
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Getter for the path used to get to the Index
	 * 
	 * @return Returns the path.
	 */
    public String getPath() {
        if (helperIndexPath == null || "".equals(helperIndexPath)) {   
            helperIndexPath = CPBLuceneModule.indexPath;
        }
        
		if (helperIndexPath == null || "".equals(helperIndexPath)) {   
			String systmp = System.getProperty("java.io.tmpdir", "tmp");
			if (systmp != null) {
                helperIndexPath = systmp + System.getProperty("file.separator");
			} else {
				log.error("java.io.tmpdir is not set!");
			}
		}
		return helperIndexPath;
	}

	/**
	 * Set the path to the Index
	 * 
	 * @param path The path to set.
	 */
	public void setPath(String path) {
		helperIndexPath = path;
	}

	/**
	 * Getter for the name of the Index
	 * 
	 * @return The name of this Index
	 */
	public String getName() {
		return "ce";
	}


	/**
	 * Check if a Index exists
	 * 
	 * @return true if Index exists
	 */
	public boolean exists() {
		boolean result = false;
		try {
			IndexReader reader = IndexReader.open(internal_indexName);
			result = true;
			reader.close();
			
		} catch (IOException e) {
		}
		return result;
	}

	
	/**
	 * Create the index (writer)
	 * 
	 * @param indexName
	 * @param analyzer
	 */
	public boolean create() {
		return open(true);
	}

	/**
	 * Open the writer
	 */
	public boolean open() {
		return open(false);
	}

	/**
	 * Close the writer
	 */
	public void close() {
		log.debug("Closing Lucene storage with index name '" + internal_indexName + "'");
		if (writer != null) {
			try {
				writer.optimize();
				writer.close();
				writer = null;
			} catch (IOException e) {
				log.error("IOException occured when closing Lucene Index with index name '" + internal_indexName + "'");
			}
		} else {
				log.debug("writer null in close '" + internal_indexName + "'");
		}
	}
	
	public void flush() {
		try {
			if (writer != null) {
				writer.flush();
			} else {
 				log.debug("writer null in flush '" + internal_indexName + "'");
  		    }
		} catch (IOException e) {
			log.error("IOException occured when flushing writer");
		}
	}

	/**
	 * Write a Lucene Document to the index
	 * 
	 * @param document Lucene Document
	 */
	public void write(Document document) {
		try {
			if (writer != null) {
				writer.addDocument(document);
			} else {
 				log.debug("writer null in write '" + internal_indexName + "'");
  		    }
		} catch (IOException e) {
			log.error("IOException occured when writing document to Lucene Index with index name '" + internal_indexName + "'");
		}
	}

	/**
	 * Return the amount of documents in the index
	 * 
	 * @return -1 if no size or the amount of documents in the index
	 */
	public int size() {
		int result = -1;
		if (writer != null) {
			result = writer.docCount();
		} else {
			IndexReader reader;
			try {
				reader = IndexReader.open(internal_indexName);
				if (reader != null) {
					result = reader.numDocs();
					reader.close();
				} else {
	 				log.debug("reader null in size '" + internal_indexName + "'");
	  		    }
			} catch (IOException e) {
				// ignore
			}
		}
		return result;
	}

	/**
	 * Returns the IndexSearcher for this index
	 * 
	 * @return IndexSearcher
	 */
	public synchronized IndexSearcher getSearcher() {
		IndexSearcher s = null;
    	try {
			s = new IndexSearcher(internal_indexName);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("IOException occured when opening Lucene Index with index name '" + internal_indexName + "' for searching");
		}
		return s;
	}

	/**
	 * Returns the IndexReader for this index
	 * 
	 * @return IndexReader
	 */
	private IndexReader getReader() {
		IndexReader result = null;
		try {
			result = IndexReader.open(internal_indexName);
		} catch (IOException e) {
			log.warn("IOException occured when opening Lucene Index with index name '" + internal_indexName + "' for reading");
		}
		return result;
	}

	/**
	 * Delete a document from the index
	 * 
	 * @param term A Lucene Term describing the document to delete
	 * @return Amount of deleted documents
	 */
	public int delete(Term term) {
		int result = -1;
		try {
			IndexReader reader = getReader();
			if (reader != null) {
				int count = reader.docFreq(term);
				if (count > 0) {
					result = reader.deleteDocuments(term);
				}
				reader.close();
				reader = null;
			} else {
 				log.debug("reader null for '" + internal_indexName + "'");
  		    }
		} catch (IOException e) {
			log.error("IOException occured when deleting a document from Lucene Index with index name '" + internal_indexName
					+ "'");
		}
		return result;
	}
	
	protected boolean forceOpen() {
		return open(false,true);
	}
	

	private boolean open(boolean create) {
		return open(create,false);
	}
	
	/**
	 * Open the index for writing
	 * 
	 * @param create Open for update (false) or a clean new index (true)
	 */
	private boolean open(boolean create, boolean force) {
		if (create) {
			log.debug("Creating Lucene storage with index name '" + internal_indexName + "'");
		} else {
			log.debug("Opening Lucene storage with index name '" + internal_indexName + "'");
		}
		try {
		   if (force && IndexReader.isLocked(internal_indexName)) {
		       log.info("locked, unlocking lucene index");
     		   IndexReader r = getReader();
     		   if (r != null) {
     			   IndexReader.unlock(r.directory());
     		   } else {
     				log.debug("null in getreader for '" + internal_indexName + "'");
     		   }
		    }
		    writer = new IndexWriter( internal_indexName, analyzer, create);
		} catch (IOException e) {
			log.warn("IOException occured when opening Lucene Index with index name '" + internal_indexName + "' for writing");
		//	e.printStackTrace();
		}
		if (writer != null) {
			log.debug("Lucene storage opened successfully");
			return true;
		} else {
			log.debug("in open reader null for '" + internal_indexName + "'");
		}
		return false;
	}

	/**
	 * 
	 */
	public Map stat() {
		Map stats = new HashMap();
		try {
			IndexReader reader = getReader();
			if (reader != null) {
				stats.put("numdocs", String.valueOf(reader.numDocs()));
				reader.close();
				reader = null;
			} else {
				log.debug("in stat reader null for '" + internal_indexName + "'");
			}
		} catch (IOException e) {
			log.error("IOException occured when requesting size from Lucene Index with index name '" + internal_indexName + "'");
		}
		return stats;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
	//	log.debug("Finalize on index name '" + internal_indexName + "'");
		try {
			if (writer != null)
				writer.close();
			writer = null;
			analyzer = null;
	    	helperIndexPath = null;
			internal_indexName = null;
		} finally {
			super.finalize();
		}
	}
}

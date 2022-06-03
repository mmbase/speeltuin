package nl.kennisnet.cpb.webservice.lucene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Hit;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class Searcher extends IndexHelper {
	
	private List ceTypes = null;
	
	private static Logger log = Logging.getLoggerInstance(Searcher.class.getName());
	
	public Results getResults(CEQueryBuilder query, int first, int count ) throws Exception{
		IndexSearcher search = null;
		Results results = null;
		try {
			BooleanQuery q = query.buildQuery();
			search = getSearcher();
			if (search!=null) {
				Hits hits = search.search(q);
				log.debug("getResults: hit count="+ hits.length() + q.toString());
				results = new Results();
				results.setCeType(query.getElementType());
				results.setHitCount(hits.length());
				int counter = first;
				List rlist = new ArrayList();
				while( counter < hits.length() && counter < (first + count)) {
					CEResult hitResult = new CEResult(hits.doc(counter));
					rlist.add(hitResult);
					log.debug("added Hit id =" + hitResult.getNumber());
					counter ++;
				}
				results.setHits(rlist);
				results.setHitCount(hits.length());
			}
		} finally {
			if (search != null) {
				search.close();
				search.getIndexReader().close();
			}
		}
		return results;
	}
	
	public ImageResults getImageResults(ImageQueryBuilder query, int first, int count ) throws Exception{
      IndexSearcher search = null;
      ImageResults results = null;
      try {
         BooleanQuery q = query.buildQuery();
         search = getSearcher();
         if (search!=null) {
            Hits hits = search.search(q);
            if (log.isDebugEnabled()) {
               log.debug("getResults: hit count="+ hits.length() + q.toString());
            }
            results = new ImageResults();            
            results.setHitCount(hits.length());
            int counter = first;
            List rlist = new ArrayList();
            while( counter < hits.length() && counter < (first + count)) {
               ImageResult hitResult = new ImageResult(hits.doc(counter));
               rlist.add(hitResult);
               if (log.isDebugEnabled()) {
                  log.debug("added Hit id =" + hitResult.getNumber());
               }
               
               counter ++;
            }
            results.setHits(rlist);
            results.setHitCount(hits.length());
         }
      } finally {
         if (search != null) {
            search.close();
            search.getIndexReader().close();
         }
      }
      return results;
   }
	
	
	public Clusters getClusterResults(CEQueryBuilder q, ClusterType clusterType) throws Exception { 
		List l = new ArrayList();
		Clusters clusters = new Clusters();
		int totalHits= 0;
		if (clusterType == null || ClusterType.CETYPE.equals(clusterType)) {
			// cluster by cetype
			List ces = getCeTypes();
			for (Iterator i = ces.iterator(); i.hasNext();) {
				String s = (String)i.next();
				q.setElementType(s);
				Results r = getResults(q, 0, 3);
				if (r != null) {
					r.setCeType(s);
					if (r.getHitCount() > 0) {
						l.add(r);
						totalHits += r.getHitCount();
					}
				}
			}
		} else if (ClusterType.KANAAL.equals(clusterType)) {
			// cluster by kanaal
			Map kanaals = getKanaals();
			for (Iterator i = kanaals.keySet().iterator(); i.hasNext();) {
				Object o = (String)i.next();
				Map m = (Map)kanaals.get(o);
				
				String kanaalIds = (String)m.get("number");
				kanaalIds = kanaalIds + " " + m.get("parents");
				
				q.setKanaalId(kanaalIds);
				Results r = getResults(q, 0, 3);
				if (r != null) {
					r.setKanaalId(kanaalIds);
					r.setNodeId((String)m.get("number"));
					r.setTitle((String)m.get("titel"));
					log.debug("Searching in kanaal: " + kanaalIds);
					if (r.getHitCount() > 0) {
						l.add(r);
						totalHits += r.getHitCount();
					}
				}
			}
		}
		clusters.setClusters(l);
		clusters.setHitCount(totalHits);
		return clusters;
	}

	private Map getKanaals() throws Exception {
		Map map = new HashMap();
		CEQueryBuilder query = new CEQueryBuilder();
		query.setElementType("Kanaal");
		Results r = getResults(query, 0, 10000);
		List l = r.getHits();
		log.debug("Getting list of kanaalen");
		for (Iterator i = l.iterator(); i.hasNext();) {
			CEResult cer = (CEResult)i.next();
			Map info = new HashMap();
			info.put("number",String.valueOf(cer.getNumber()));
			info.put("titel", cer.getTitle());
			info.put("parents", cer.getParentIds()); 
			map.put(String.valueOf(cer.getNumber()), info);
		}
		return map;
	}
	
	private List getCeTypes() { 
		 if (ceTypes == null) { 
			 	ceTypes = new ArrayList <String> ();
		    //	ceTypes.add(Constants.CETYPE_ABC_ITEM);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_ARTIKEL);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_ATTENDERING);
		    //	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_CN_ARTIKEL);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_EVENEMENT);
		    // 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_FOOTER_LINKS);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_FORMULIER);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_GALERIJ);
		    //	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_HTML_BLOK);
			 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_JUMP_TO_LINKS);
		    	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_LANG_ARTIKEL);
		    	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_LINKSLIJST);
		    // 	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_NB_AANMELDING);
		    	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_PARAGRAAF);
		    	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_PLAATJE);
		    	ceTypes.add(nl.kennisnet.cpb.Constants.CETYPE_POLL);
		}
		return ceTypes;
	 }
	
	public void close() {
	}
}

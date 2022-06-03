package nl.kennisnet.cpb.webservice.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ImageQueryBuilder {

	private static Logger log = Logging.getLoggerInstance(ImageQueryBuilder.class.getName());	  
     
   
   private String searchText;
   private String redactieGroup; 

   
   

   public String getRedactieGroup() {
      return redactieGroup;
   }

   public void setRedactieGroup(String redactieGroup) {
      this.redactieGroup = redactieGroup;
   }

   public String getSearchText() {
      return searchText;
   }

   public void setSearchText(String searchText) {
      this.searchText = searchText;
   }
   
   public BooleanQuery buildQuery() throws Exception{
      BooleanQuery boolQuery = new BooleanQuery();
      QueryParser parser = null;
      try {
         
         if (searchText != null && !"".equals(searchText)) {
            
            parser = new MultiFieldQueryParser(
                  new String[] { 
                        Constants.IMAGE_TITLE_FIELD,
                        Constants.IMAGE_DESCRIPTION_FIELD,
                        Constants.IMAGE_NOTITIE_FIELD }, new StandardAnalyzer());
            
            org.apache.lucene.search.Query q = parser.parse(searchText);
            boolQuery.add(q, BooleanClause.Occur.MUST);
          }
          if (redactieGroup != null && !"".equals(redactieGroup)) {
            
            parser = new QueryParser(Constants.IMAGE_NOTITIE_FIELD, new StandardAnalyzer());
            
            org.apache.lucene.search.Query q = parser.parse(Constants.IMAGE_REDACTIEGROEP_MARKER_PREFIX + redactieGroup);
            boolQuery.add(q, BooleanClause.Occur.MUST);
          }
         
      } catch (ParseException e) {
         // ignore
      }
      //Filter out only the images     
      boolQuery.add(new TermQuery(new Term(Constants.BUILDER_FIELD, "images")), BooleanClause.Occur.MUST);      
      return boolQuery;
   }
   
	
}

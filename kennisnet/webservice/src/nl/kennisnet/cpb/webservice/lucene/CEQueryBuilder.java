package nl.kennisnet.cpb.webservice.lucene;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.index.Term;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class CEQueryBuilder {

	private static Logger log = Logging.getLoggerInstance(CEQueryBuilder.class.getName());	  
	
	private String searchText; 
	private String redactie;
	private boolean isOnPage = false;
	private KanaalStatus kanaalState = null;
	private String elementType; //This is a list of elementtypes, separated by whitespace (spaces)
	private boolean exact = false;
	private SearchField searchField; 
	private String kanaalId;
	private String[] redactieGroups;
	private String isAgenda; //optional, 1 or 0

	
	public String[] getRedactieGroups() {
		return redactieGroups;
	}

	public void setRedactieGroups(String[] redactieGroups) {
		this.redactieGroups = redactieGroups;
	}

	public String getRedactie() {
		return redactie;
	}

	public void setRedactie(String redactie) {
		this.redactie = redactie;
	}

	public String getKanaalId() {
		return kanaalId;
	}

	public void setKanaalId(String kanaalId) {
		this.kanaalId = kanaalId;
	}

	public SearchField getSearchField() {
		return searchField;
	}

	public void setSearchField(SearchField searchField) {
		this.searchField = searchField;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public KanaalStatus getKanaalState() {
		return kanaalState;
	}

	public void setKanaalState(KanaalStatus kanaalState) {
		this.kanaalState = kanaalState;
	}

	public boolean isOnPage() {
		return isOnPage;
	}

	public void setOnPage(boolean isOnPage) {
		this.isOnPage = isOnPage;
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
			
				if (searchField == null || searchField.equals(SearchField.ALL)) {
						parser = new MultiFieldQueryParser(
								new String[] { 
										Constants.TEXT_FIELD,
										Constants.NOTITIE_FIELD,
										Constants.TITEL_FIELD }, new StandardAnalyzer());
					
				} else if (searchField.equals(SearchField.TITEL)) {
					parser = 
							new QueryParser(
									Constants.TITEL_FIELD, new StandardAnalyzer());
				} else if (searchField.equals(SearchField.BODY)) {
					parser = 
					 		new QueryParser(
								Constants.TEXT_FIELD, new StandardAnalyzer());
				} else if (searchField.equals(SearchField.NOTITIE)) {
					parser = 
					 		new QueryParser(
								Constants.NOTITIE_FIELD, new StandardAnalyzer());
				}
		        org.apache.lucene.search.Query q = parser.parse(searchText);
		        boolQuery.add(q, BooleanClause.Occur.MUST);
		    }
			
		} catch (ParseException e) {
			// ignore
		}
		if (elementType != null && !"".equals(elementType)) {
			//TermQuery tq1 = new TermQuery(new Term(Constants.ELEMENTTYPE_FIELD, elementType));
			//boolQuery.add(tq1, BooleanClause.Occur.MUST);			
			
			//New due to webservice:
			//Elementtype is either a single term or a list of terms, separated by whitespace (spaces).
			//The web search uses only single elementtypes, the webservice needs more fine grained access
			parser = new QueryParser(Constants.ELEMENTTYPE_FIELD, new WhitespaceAnalyzer());
         org.apache.lucene.search.Query tq1 = parser.parse(elementType);
         boolQuery.add(tq1,  BooleanClause.Occur.MUST);
      }
		
		Calendar cal = Calendar.getInstance();
		String now = "" + new Date().getTime();
		
		if (kanaalState != null) {
			if (KanaalStatus.NOT_IN_KANAAL.equals(kanaalState)) {
				TermQuery tq1 = new TermQuery(new Term(Constants.KANAALMOD_FIELD, " 0"));
				boolQuery.add(tq1, BooleanClause.Occur.MUST);
			} else if (KanaalStatus.ADDED_LAST_YEAR.equals(kanaalState)) {
				cal.add(Calendar.MONTH, -12);
				String s = "" + cal.getTime().getTime();
				ConstantScoreRangeQuery crq = new ConstantScoreRangeQuery(Constants.KANAALMOD_FIELD, s, now, true, true);
				boolQuery.add(crq, BooleanClause.Occur.MUST);
			} else if (KanaalStatus.ADDED_LAST_3_MONTHS.equals(kanaalState)) {
				cal.add(Calendar.MONTH, -3);
				String s = "" + cal.getTime().getTime();
				ConstantScoreRangeQuery crq = new ConstantScoreRangeQuery(Constants.KANAALMOD_FIELD, s, now, true, true);
				boolQuery.add(crq, BooleanClause.Occur.MUST);
			} else if (KanaalStatus.ADDED_LAST_MONTH.equals(kanaalState)) {
				cal.add(Calendar.MONTH, -1);
				String s = "" + cal.getTime().getTime();
				ConstantScoreRangeQuery crq = new ConstantScoreRangeQuery(Constants.KANAALMOD_FIELD, s, now, true, true);
				boolQuery.add(crq, BooleanClause.Occur.MUST);
			} else if (KanaalStatus.ADDED_LAST_WEEK.equals(kanaalState)) {
				cal.add(Calendar.DAY_OF_YEAR, -7);
				String s = "" + cal.getTime().getTime();
				ConstantScoreRangeQuery crq = new ConstantScoreRangeQuery(Constants.KANAALMOD_FIELD, s, now, true, true);
				boolQuery.add(crq, BooleanClause.Occur.MUST);
			} else if (KanaalStatus.IN_KANAAL.equals(kanaalState)) {
				TermQuery tq1 = new TermQuery(new Term(Constants.KANAALMOD_FIELD, "0"));
				boolQuery.add(tq1, BooleanClause.Occur.MUST_NOT);
			}
		}
		
		if (kanaalId != null && !"".equals(kanaalId)) {
			String[] parts = kanaalId.split(" ");
			StringBuffer pBuff = new StringBuffer();

			for (int i = 0; i < parts.length; i++) {
				if (!"".equals(parts[i]) && !" ".equals(parts[i]) && !"0".equals(parts[i])){
					log.debug("adding query part=" + parts[i]);
					pBuff.append(" ");
					pBuff.append(parts[i]);
					pBuff.append("*");
				}
			}
			log.debug("whole query is=" + pBuff);
			parser = new QueryParser(Constants.IN_KANAALEN_FIELD, new StandardAnalyzer());
			org.apache.lucene.search.Query tq1 = parser.parse(pBuff.toString());
			boolQuery.add(tq1, BooleanClause.Occur.MUST);
		}
		
		if (isOnPage) {
			TermQuery tq1 = new TermQuery(new Term(Constants.PAGES_FIELD, "0"));
			boolQuery.add(tq1, BooleanClause.Occur.MUST_NOT);
		}
		
		if (redactieGroups != null && redactieGroups.length > 0) {
			boolean foundRed = false;
			StringBuffer redGroups = new StringBuffer();
			for (int i = 0; i < redactieGroups.length; i++) {
				if (!"".equals(redactieGroups[i]) && !" ".equals(redactieGroups[i])) {
					redGroups.append(" ");
					redGroups.append(redactieGroups[i]);
					redGroups.append("*");
					foundRed = true;
				}
			}
			if (foundRed) {
				parser = new QueryParser(Constants.REDACTIE_FIELD, new StandardAnalyzer());
				org.apache.lucene.search.Query tq1 = parser.parse(redGroups.toString());
				boolQuery.add(tq1,  BooleanClause.Occur.MUST);
			}
		}
		
		if (isAgenda != null) {		   
         TermQuery tq1 = new TermQuery(new Term(Constants.BOOLEAN2_FIELD, "" + isAgenda));
         boolQuery.add(tq1, BooleanClause.Occur.MUST);
      }
		
		/*
		if (redactie != null && !"".equals(redactie)) {
			TermQuery tq1 = new TermQuery(new Term(Constants.REDACTIE_FIELD," " + redactie + "*"));
			boolQuery.add(tq1, BooleanClause.Occur.MUST);
		}
		*/
			
		return boolQuery;
	}
	
	 protected String getStringError(Exception e) {
	      String buff = "";
	      try { 
	         ByteArrayOutputStream bstr = new ByteArrayOutputStream();
	         OutputStreamWriter out = new OutputStreamWriter(bstr,"UTF-8");
	         PrintWriter writer = new PrintWriter(out);
	         e.printStackTrace(new PrintWriter(writer));
	         buff= bstr.toString();
	      } catch (Exception err) {
	         err.printStackTrace();
	      }
	      return buff;
	  }

   public String getIsAgenda() {
      return isAgenda;
   }

   public void setIsAgenda(String isAgenda) {
      this.isAgenda = isAgenda;
   }
	
	
}

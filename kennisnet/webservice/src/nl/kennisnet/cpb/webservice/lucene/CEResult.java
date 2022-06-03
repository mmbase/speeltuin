package nl.kennisnet.cpb.webservice.lucene;

import org.apache.lucene.document.Document;

public class CEResult extends Result{

	private String kanaalMod;
	private String parentIds; 
	private String redactieGroupen;
	private String pages;
	private String title;
	private String ceType;
	
	CEResult(Document doc) {
		this.title =  doc.getField(Constants.TITEL_FIELD).stringValue();
		this.number =  new Long(Long.parseLong(doc.getField(Constants.NODE_FIELD).stringValue()));
		this.kanaalMod = doc.getField(Constants.KANAALMOD_FIELD).stringValue();
		this.parentIds = doc.getField(Constants.PARENTIDS_FIELD).stringValue();
		this.redactieGroupen = doc.getField(Constants.REDACTIE_FIELD).stringValue();
		this.pages = doc.getField(Constants.PAGES_FIELD).stringValue();
		this.ceType = doc.getField(Constants.ELEMENTTYPE_FIELD).stringValue();
	}
	
	public String getCeType() {
		return ceType;
	}
	
	public void setCeType(String ceType) {
		this.ceType = ceType;
	}
	
	public String getCeTypeDisplayName() {
		return nl.kennisnet.cpb.Constants.giveNiceCeName(ceType);
	}
	
	public String getCeWizard() {
		return nl.kennisnet.cpb.Constants.giveWizardName(ceType);
	}
	
	private long number;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	} 
	
	
	public String getKanaalMod() {
		return kanaalMod;
	}


	public void setKanaalMod(String kanaalMod) {
		this.kanaalMod = kanaalMod;
	}


	public String getPages() {
		return pages;
	}


	public void setPages(String pages) {
		this.pages = pages;
	}


	public String getParentIds() {
		return parentIds;
	}


	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}


	public String getRedactieGroupen() {
		return redactieGroupen;
	}


	public void setRedactieGroupen(String redactieGroupen) {
		this.redactieGroupen = redactieGroupen;
	}
	
	
	
}

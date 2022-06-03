package nl.kennisnet.cpb.webservice.lucene;

import java.util.List;

public class Results {
 
	private int hitCount;
	private List < CEResult > hits;
	private String ceType; 
	private String ceTypeDisplayName; 
	private String ceWizard;
	private String title;
	private String nodeId;
	private String kanaalId;
	
	
	
	public String getKanaalId() {
		return kanaalId;
	}
	public void setKanaalId(String kanaalId) {
		this.kanaalId = kanaalId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	public List < CEResult > getHits() {
		return hits;
	}
	public void setHits(List < CEResult > hits) {
		this.hits = hits;
	}
}

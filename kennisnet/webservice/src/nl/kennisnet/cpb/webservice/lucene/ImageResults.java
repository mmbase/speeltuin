package nl.kennisnet.cpb.webservice.lucene;

import java.util.List;

public class ImageResults {
 
	private int hitCount;
	private List < ImageResult > hits;
	
		
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
   public List<ImageResult> getHits() {
      return hits;
   }
   public void setHits(List<ImageResult> hits) {
      this.hits = hits;
   }
	
}

/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;


/**
 * Model for the fields of a page published in the webservice
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class PageWrapper extends OrderedWrapper {

   private Integer categorieId;
   private String url;  
   
   public PageWrapper(int pos) {
      super(pos);   
   }   
  
   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public Integer getCategorieId() {
      return categorieId;
   }

   public void setCategorieId(Integer categorieId) {
      this.categorieId = categorieId;
   }

}

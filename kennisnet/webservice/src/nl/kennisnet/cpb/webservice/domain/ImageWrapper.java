/**
 * 
 */
package nl.kennisnet.cpb.webservice.domain;

/**
 * A container for image data  
 * 
 * Log: not needed
 * 
 * @author dekok01
 * @version $Revision: 1.0
 */
public class ImageWrapper extends OrderedWrapper{
   
   private Integer number;
   private String owner;
   private String title;
   private String description;   
   private String itype;
   private Integer popup;
   private String notitie;   
   private Integer thumbnail;   
   private Integer width;   
   private Integer height;
   
   public ImageWrapper (int pos) {
      super(pos);
   }   
   
   public Integer getNumber() {
      return number;
   }
   public void setNumber(Integer number) {
      this.number = number;
   }
   public String getOwner() {
      return owner;
   }
   public void setOwner(String owner) {
      this.owner = owner;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public String getDescription() {
      return description;
   }
   public void setDescription(String description) {
      this.description = description;
   }
   public String getItype() {
      return itype;
   }
   public void setItype(String itype) {
      this.itype = itype;
   }
   public Integer getPopup() {
      return popup;
   }
   public void setPopup(Integer popup) {
      this.popup = popup;
   }
   public String getNotitie() {
      return notitie;
   }
   public void setNotitie(String notitie) {
      this.notitie = notitie;
   }
   public Integer getThumbnail() {
      return thumbnail;
   }
   public void setThumbnail(Integer thumbnail) {
      this.thumbnail = thumbnail;
   }
   public Integer getWidth() {
      return width;
   }
   public void setWidth(Integer width) {
      this.width = width;
   }
   public Integer getHeight() {
      return height;
   }
   public void setHeight(Integer height) {
      this.height = height;
   }   
}

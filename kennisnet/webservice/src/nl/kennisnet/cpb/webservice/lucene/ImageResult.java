package nl.kennisnet.cpb.webservice.lucene;

import org.apache.lucene.document.Document;

public class ImageResult extends Result{
	
	private String title;
	private long number;
   private String description;
   private String notitie;
   private String width;
   private String height;
   private String filesize;
	
	ImageResult(Document doc) {
	   this.number =  new Long(Long.parseLong(doc.getField(Constants.NODE_FIELD).stringValue()));
		this.title =  doc.getField(Constants.IMAGE_TITLE_FIELD).stringValue();		
		this.description =  doc.getField(Constants.IMAGE_DESCRIPTION_FIELD).stringValue();
		this.notitie =  doc.getField(Constants.IMAGE_NOTITIE_FIELD).stringValue();
		this.width =  doc.getField(Constants.IMAGE_WIDTH_FIELD).stringValue();
		this.height =  doc.getField(Constants.IMAGE_HEIGHT_FIELD).stringValue();
		this.filesize =  doc.getField(Constants.IMAGE_FILESIZE_FIELD).stringValue();
	}
	

	
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



   public String getDescription() {
      return description;
   }



   public void setDescription(String description) {
      this.description = description;
   }



   public String getNotitie() {
      return notitie;
   }



   public void setNotitie(String notitie) {
      this.notitie = notitie;
   }



   public String getWidth() {
      return width;
   }



   public void setWidth(String width) {
      this.width = width;
   }



   public String getHeight() {
      return height;
   }



   public void setHeight(String height) {
      this.height = height;
   }



   public String getFilesize() {
      return filesize;
   }



   public void setFilesize(String filesize) {
      this.filesize = filesize;
   } 
	
	
}

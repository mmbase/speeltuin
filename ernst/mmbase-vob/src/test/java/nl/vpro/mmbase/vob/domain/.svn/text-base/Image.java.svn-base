package nl.vpro.mmbase.vob.domain;

import nl.vpro.mmbase.vob.annotations.Entity;

@Entity(builder = "images")
public class Image {
	private Long number;
	private String title;

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return String.format("http://images.vpro.nl/%d", number);
	}
	
	@Override
	public String toString() {
		return String.format("Image[%d, %s]", number, title);
	}
}

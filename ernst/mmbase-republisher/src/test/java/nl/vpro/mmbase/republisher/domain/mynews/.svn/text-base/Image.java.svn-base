package nl.vpro.mmbase.republisher.domain.mynews;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import nl.vpro.mmbase.vob.annotations.Entity;
import nl.vpro.mmbase.vob.annotations.Field;

@Entity(builder = "images")
public class Image {
	private int number;
	private String title;
	private String description;

	@Field(nodeField = "itype")
	private String type;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
}

package nl.vpro.mmbase.vob.domain;

import nl.vpro.mmbase.vob.annotations.Entity;

@Entity(builder = "tags")
public class Tag {
	private Long number;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return String.format("Tag[%d, %s]", number, value);
	}
}

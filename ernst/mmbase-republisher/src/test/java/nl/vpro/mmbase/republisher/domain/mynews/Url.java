package nl.vpro.mmbase.republisher.domain.mynews;

import nl.vpro.mmbase.vob.annotations.Entity;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@Entity(builder = "urls")
public class Url {
    private int number;
	private String url;
	private String description;
	
	
	
	public int getNumber() {
        return number;
    }
	
    public void setNumber(int number) {
        this.number = number;
    }
    
    public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
	
	
}

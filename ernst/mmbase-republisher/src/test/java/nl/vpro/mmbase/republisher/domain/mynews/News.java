package nl.vpro.mmbase.republisher.domain.mynews;

import java.util.Date;
import java.util.List;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.annotations.*;
import nl.vpro.mmbase.vob.converters.EpochDateConverter;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@Entity(builder = "news", root=true)
public class News {
    private int number;
	private String title;
	private String subtitle;
	private String intro;
	private String text;

	@PosRel(orderDirection = Direction.DESC)
	private List<Url> urls;

	@Rel()
	private List<Person> people;

	@PosRel(orderDirection = Direction.DESC)
	private List<Image> images;

	@Field(convertor = EpochDateConverter.class)
	private Date date;
	
	

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

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Url> getUrls() {
		return urls;
	}

	public void setUrls(List<Url> urls) {
		this.urls = urls;
	}

	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
}

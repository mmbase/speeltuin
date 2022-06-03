package nl.vpro.mmbase.vob.domain;

import java.util.Date;
import java.util.List;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.QueryDirection;
import nl.vpro.mmbase.vob.annotations.*;
import nl.vpro.mmbase.vob.converters.EpochDateConverter;
import nl.vpro.mmbase.vob.converters.ToLowercaseConverter;

@Entity(builder = "news", root=true)
public class NewsItem {

	private Long number;
	private String title;
	
	@Field(convertor = ToLowercaseConverter.class)
	private String subtitle;
	
	private String credits;

	@Field(nodeField = "intro")
	private String description;

	private String body;

	@Embedded(builder = "mmevents", relationRole="posrel", queryDirection = QueryDirection.BOTH, field = "start", convertor = EpochDateConverter.class)
	private Date created;

	@PosRel(orderDirection = Direction.DESC, queryDirection = QueryDirection.SOURCE)
	private List<Image> images;

	@Rel(orderDirection = Direction.DESC, orderField = "value", queryDirection = QueryDirection.SOURCE)
	private List<Tag> tags;

	@PosRel(orderDirection = Direction.DESC, queryDirection = QueryDirection.SOURCE)
	private List<Paragraph> paragraphs;
	
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

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return String.format("News[%d, %s]", number, title);
	}
}

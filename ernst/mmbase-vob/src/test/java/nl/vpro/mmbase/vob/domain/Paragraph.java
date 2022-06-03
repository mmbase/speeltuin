package nl.vpro.mmbase.vob.domain;

import java.util.List;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.QueryDirection;
import nl.vpro.mmbase.vob.annotations.*;


/**
 * @author Ernst Bunders
 *
 */
@Entity(builder = "paragraphs")
public class Paragraph {
    private long number;
    private String title;
    private String text;
    
    @PosRel(orderDirection = Direction.DESC, queryDirection = QueryDirection.DESTINATION)
    private List<Image> images;
    
    @Rel(orderDirection = Direction.DESC, queryDirection = QueryDirection.DESTINATION)
    private List<Dummy> dummies;

    public long getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Dummy> getDummies() {
        return dummies;
    }

}

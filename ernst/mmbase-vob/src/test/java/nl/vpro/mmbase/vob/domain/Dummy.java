package nl.vpro.mmbase.vob.domain;

import java.util.Date;
import java.util.List;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.QueryDirection;
import nl.vpro.mmbase.vob.annotations.*;
import nl.vpro.mmbase.vob.converters.EpochDateConverter;

/**
 * @author Ernst Bunders
 *
 */
@Entity(builder = "dummy")
public class Dummy {
    @PosRel(orderDirection = Direction.DESC, queryDirection = QueryDirection.DESTINATION)
    private List<Image> images;
    
    @Embedded(builder = "mmevents", relationRole="related", queryDirection = QueryDirection.DESTINATION, field = "start", convertor = EpochDateConverter.class)
    private Date created;

    public List<Image> getImages() {
        return images;
    }

}

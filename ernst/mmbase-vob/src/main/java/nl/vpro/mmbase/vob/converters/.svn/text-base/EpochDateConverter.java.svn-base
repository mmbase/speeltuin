package nl.vpro.mmbase.vob.converters;

import java.util.Date;

import nl.vpro.mmbase.vob.converters.FieldConverter;

import org.mmbase.bridge.Node;

/**
 * Converts 'seconds from the epoch' to a real date object.
 * 
 * @author peter.maas@finalist.com
 */
public class EpochDateConverter implements FieldConverter {

	/* (non-Javadoc)
	 * @see nl.vpro.mmbase.vob.convertors.FieldConvertor#convert(org.mmbase.bridge.Node, java.lang.String)
	 */
	public Object convert(Node node, String field) {
		return new Date(node.getLongValue(field) * 1000L);
	}

}

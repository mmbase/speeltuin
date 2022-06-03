package nl.vpro.mmbase.vob.converters;

/**
 * Convertors are used to transform the mmbase fieldvalue to the wanted type.
 * 
 * @author peter.maas@finalist.com
 */
public interface FieldConverter {
	/**
	 * @param node The node containing the field
	 * @param field The name of the field to convert
	 * @return the resulting value
	 */
	Object convert(org.mmbase.bridge.Node node, String field);
}

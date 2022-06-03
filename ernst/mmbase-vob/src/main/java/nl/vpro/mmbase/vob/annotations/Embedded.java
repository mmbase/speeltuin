package nl.vpro.mmbase.vob.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.QueryDirection;
import nl.vpro.mmbase.vob.converters.FieldConverter;
import nl.vpro.mmbase.vob.converters.PassThroughFieldConverter;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Embedded {
	String builder();
	String field();
	String relationRole() default "related";
	String orderField() default "number";
	Direction orderDirection() default Direction.ASC;
	QueryDirection queryDirection() default QueryDirection.DESTINATION;
	Class<? extends FieldConverter> convertor() default PassThroughFieldConverter.class;
}

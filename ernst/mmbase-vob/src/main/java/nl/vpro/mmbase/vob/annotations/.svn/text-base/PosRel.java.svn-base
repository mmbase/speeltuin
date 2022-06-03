package nl.vpro.mmbase.vob.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.vpro.mmbase.vob.Direction;
import nl.vpro.mmbase.vob.QueryDirection;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Rel
public @interface PosRel  {
	Direction orderDirection() default Direction.ASC;

	QueryDirection queryDirection() default QueryDirection.DESTINATION;
}

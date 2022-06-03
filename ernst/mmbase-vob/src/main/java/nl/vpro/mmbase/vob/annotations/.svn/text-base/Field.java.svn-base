package nl.vpro.mmbase.vob.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.vpro.mmbase.vob.converters.FieldConverter;
import nl.vpro.mmbase.vob.converters.PassThroughFieldConverter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
	String nodeField() default "";
	Class<? extends FieldConverter> convertor() default PassThroughFieldConverter.class;
}

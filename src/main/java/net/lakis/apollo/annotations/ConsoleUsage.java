package net.lakis.apollo.annotations;

 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD}) //can use in method only.
public @interface ConsoleUsage {
 	public String value();
 
}
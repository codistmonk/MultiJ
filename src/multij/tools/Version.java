package multij.tools;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author codistmonk (creation 2012-09-03)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {
	
	public abstract String value();
	
}

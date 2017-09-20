package multij.rules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author codistmonk (creation 2015-12-07)
 *
 * @param <T>
 */
public abstract interface Predicate<T> extends Serializable, java.util.function.Predicate<T> {
	
	@Override
	public default boolean test(final T object) {
		return this.test(object, new HashMap<>());
	}
	
	public abstract boolean test(T object, Map<Variable, Object> mapping);
	
}

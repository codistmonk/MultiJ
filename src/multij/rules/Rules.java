package multij.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author codistmonk (creation 2015-12-07)
 *
 * @param <T>
 * @param <R>
 */
public final class Rules<T, R> implements Rule<T, R> {
	
	private final List<Rule<T, R>> rules = new ArrayList<>();
	
	public final List<Rule<T, R>> getRules() {
		return this.rules;
	}
	
	public final R applyTo(final T input) {
		return this.applyTo(input, new LinkedHashMap<>());
	}
	
	public final R applyTo(final T input, final R defaultResult) {
		return this.applyTo(input, new LinkedHashMap<>(), defaultResult);
	}
	
	public final R applyTo(final T input, final Map<Variable, Object> mapping) {
		return this.apply(input, mapping).get();
	}
	
	public final R applyTo(final T input, final Map<Variable, Object> mapping, final R defaultResult) {
		final Result<R> r = this.apply(input, mapping);
		
		return r != null ? r.get() : defaultResult;
	}
	
	@Override
	public final Result<R> apply(final T input, final Map<Variable, Object> mapping) {
		final Map<Variable, Object> backup = new LinkedHashMap<>(mapping);
		
		for (final Rule<T, R> rule : this.getRules()) {
			restore(backup, mapping);
			
			final Result<R> r = rule.apply(input, mapping);
			
			if (r != null) {
				return r;
			}
		}
		
		return null;
	}
	
	public final Rules<T, R> add(final Rule<T, R> rule) {
		this.getRules().add(rule);
		
		return this;
	}
	
	public final void add(final Predicate<T> predicate, final Application<T, R> application) {
		this.add(new CompositeRule<>(predicate, application));
	}
	
	private static final long serialVersionUID = 271358038357056552L;
	
	public static final <K, V> void restore(final Map<K, V> backup, final Map<K, V> mapping) {
		mapping.clear();
		mapping.putAll(backup);
	}
	
	/**
	 * @author codistmonk (creation 2016-09-04)
	 *
	 * @param <R>
	 */
	public static final class Result<R> implements Serializable {
		
		private final R value;
		
		public Result(final R value) {
			this.value = value;
		}
		
		public final R get() {
			return this.value;
		}
		
		private static final long serialVersionUID = -4189453873936338637L;
		
	}
	
}
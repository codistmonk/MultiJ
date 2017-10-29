package multij.rules;

import static java.util.stream.Collectors.toCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Rules}.
 * 
 * @author codistmonk (creation 2015-12-07)
 */
public final class RulesTest {
	
	@Test
	public final void test1() {
		final ExpressionContext context = new ExpressionContext();
		
		context.addType((e, m) -> number(e) != null, (e, m) -> "Scalar");
		context.addSimplification(isNumberBinaryOperation("+"), numberBinaryOperation(BigDecimal::add));
		context.addSimplification(isNumberBinaryOperation("*"), numberBinaryOperation(BigDecimal::multiply));
		context.addSimplification((e, m) -> isScalarBinaryOperation("+", e, context)
				&& left(e).equals(right(e)), (e, m) -> context.$(2, "*", left(e)));
		context.addSimplification((e, m) -> isScalarBinaryOperation("*", e, context)
				&& left(e).equals(right(e)), (e, m) -> context.$(left(e), "^", 2));
		
		assertEquals("Scalar", context.getTypeOf(42));
		assertEquals("Undefined", context.getTypeOf("toto"));
		assertEquals(number(3), context.$(1, "+", 2));
		
		assertEquals(Arrays.asList("a", "+", "a"), context.$("a", "+", "a"));
		
		context.declare("a", "Scalar");
		
		assertEquals(Arrays.asList(number(2), "*", "a"), context.$("a", "+", "a"));
		assertEquals(Arrays.asList("a", "^", number(2)), context.$("a", "*", "a"));
	}
	
	@Test
	public final void test2() {
		final Variable v = Variable.var();
		final Map<Variable, Object> m = new HashMap<>();
		
		assertTrue(new PatternPredicate(Arrays.asList(v, "42")).test(Arrays.asList("v", "42"), m));
		assertEquals("v", m.get(v));
	}
	
	public static final boolean isScalarBinaryOperation(final Object operator,
			final Object expression, final ExpressionContext context) {
		return isBinaryOperation(operator, expression)
				&& "Scalar".equals(context.getTypeOf(left(expression)))
				&& "Scalar".equals(context.getTypeOf(right(expression)));
	}
	
	public static final <T> T right(final Object expression) {
		return get(expression, 2);
	}
	
	public static final <T> T left(final Object expression) {
		return get(expression, 0);
	}
	
	public static final Application<Object, Object> numberBinaryOperation(
			final BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
		return (e, m) -> operation.apply(number(left(e)), number(right(e)));
	}
	
	public static final Predicate<Object> isNumberBinaryOperation(final Object operator) {
		return (e, m) -> isNumberBinaryOperation(operator, e);
	}
	
	public static final boolean isNumberBinaryOperation(final Object operator, final Object expression) {
		return isBinaryOperation(operator, expression) && isNumber(left(expression)) && isNumber(right(expression));
	}
	
	public static final boolean isBinaryOperation(final Object operator, final Object expression) {
		return isList(expression) && size(expression) == 3 && operator.equals(get(expression, 1));
	}
	
	public static final boolean isNumber(final Object object) {
		return number(object) != null;
	}
	
	public static final boolean isList(final Object object) {
		return object instanceof List;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> T get(final Object object, final int index) {
		return ((List<T>) object).get(index);
	}
	
	public static final int size(final Object object) {
		return ((List<?>) object).size();
	}
	
	public static final BigDecimal number(final Object object) {
		if (object instanceof BigDecimal) {
			return (BigDecimal) object;
		}
		
		if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long) {
			return BigDecimal.valueOf(((Number) object).longValue());
		}
		
		if (object instanceof Float || object instanceof Double) {
			return BigDecimal.valueOf(((Number) object).doubleValue());
		}
		
		if (object instanceof Number) {
			return new BigDecimal(object.toString());
		}
		
		return null;
	}

	/**
	 * @author codistmonk (creation 2015-12-07)
	 */
	public static final class ExpressionContext implements Serializable {
		
		private final Rules<Object, Object> typeRules = new Rules<>();
		
		private final Rules<Object, Object> simplificationRules = new Rules<>();
		
		public final Rules<Object, Object> getTypeRules() {
			return this.typeRules;
		}
		
		public final Rules<Object, Object> getSimplificationRules() {
			return this.simplificationRules;
		}
		
		public final Object simplify(final Object expression) {
			return this.getSimplificationRules().applyTo(expression, expression);
		}
		
		public final Object getTypeOf(final Object expression) {
			return this.getTypeRules().applyTo(expression, "Undefined");
		}
		
		public final Object $(final Object... objects) {
			return this.simplify(Arrays.stream(objects)
					.map(ExpressionContext::expression).collect(toCollection(ArrayList::new)));
		}
		
		public final void declare(final Object object, final Object type) {
			this.getTypeRules().add((e, m) -> Variable.match(object, e, m), (e, m) -> Variable.rewrite(type, m));
		}
		
		public final void addType(final Predicate<Object> predicate, final Application<Object, Object> application) {
			this.addType(new CompositeRule<>(predicate, application));
		}
		
		public final void addType(final CompositeRule<Object, Object> rule) {
			this.getTypeRules().add(rule);
		}
		
		public final void addSimplification(final Predicate<Object> predicate,
				final Application<Object, Object> application) {
			this.addSimplification(new CompositeRule<>(predicate, application));
		}
		
		public final void addSimplification(final CompositeRule<Object, Object> rule) {
			this.getSimplificationRules().add(rule);
		}
		
		private static final long serialVersionUID = 2516111889161000383L;
		
		public static final Object expression(final Object object) {
			final Object number = number(object);
			
			return number == null ? object : number;
		}
		
	}
	
}

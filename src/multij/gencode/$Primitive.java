package multij.gencode;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import multij.tools.IllegalInstantiationException;

/**
 * Placeholder for Java primitive types.
 *  
 * @author codistmonk (creation 2013-01-16)
 */
public abstract interface $Primitive {
	
	@Transcription(" == ")
	public abstract boolean $isEqualTo(Object that);
	
	@Transcription(" != ")
	public abstract boolean $isNotEqualTo(Object that);
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $Boolean extends $Primitive {
		
		public abstract $Primitive $and(Object that);
		
		public abstract $Primitive $or(Object that);
		
		public static final String[] NAMES = { "boolean" };
		
		/**
		 * @author codistmonk (creation 2013-01-16)
		 */
		public static final class UnaryOperations {
			
			private UnaryOperations() {
				throw new IllegalInstantiationException();
			}
			
			public static final $Boolean $not(final $Boolean x) {
				return null;
			}
			
			public static final boolean $boolean(final $Boolean x) {
				return false;
			}
			
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $Number extends $Primitive {
		
		@Transcription(" + ")
		public abstract $Primitive $plus(Object that);
		
		@Transcription(" - ")
		public abstract $Primitive $minus(Object that);
		
		@Transcription(" * ")
		public abstract $Primitive $times(Object that);
		
		@Transcription(" / ")
		public abstract $Primitive $dividedBy(Object that);
		
		@Transcription(" % ")
		public abstract $Primitive $mod(Object that);
		
		@Transcription(" < ")
		public abstract $Primitive $isLessThan(Object that);
		
		@Transcription(" <= ")
		public abstract $Primitive $isLessThanOrEqualTo(Object that);
		
		@Transcription(" > ")
		public abstract $Primitive $isGreaterThan(Object that);
		
		@Transcription(" >= ")
		public abstract $Primitive $isGreaterThanOrEqualTo(Object that);
		
		/**
		 * @author codistmonk (creation 2013-01-16)
		 */
		public static abstract class UnaryOperations {
			
			protected UnaryOperations() {
				throw new IllegalInstantiationException();
			}
			
			@Transcription("(byte) ")
			public static final byte $byte(final $Number x) {
				return 0;
			}
			
			@Transcription("(char) ")
			public static final char $char(final $Number x) {
				return 0;
			}
			
			@Transcription("(short) ")
			public static final short $short(final $Number x) {
				return 0;
			}
			
			@Transcription("(int) ")
			public static final int $int(final $Number x) {
				return 0;
			}
			
			@Transcription("(long) ")
			public static final long $long(final $Number x) {
				return 0;
			}
			
			@Transcription("(float) ")
			public static final float $float(final $Number x) {
				return 0;
			}
			
			@Transcription("(double) ")
			public static final double $double(final $Number x) {
				return 0;
			}
			
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $Linear extends $Number {
		
		@Transcription(" | ")
		public abstract $Primitive $bitwiseOr(Object that);
		
		@Transcription(" & ")
		public abstract $Primitive $bitwiseAnd(Object that);
		
		@Transcription(" ^ ")
		public abstract $Primitive $bitwiseXor(Object that);
		
		@Transcription(" << ")
		public abstract $Primitive $shiftLeft(Object that);
		
		@Transcription(" >> ")
		public abstract $Primitive $shiftRight(Object that);
		
		@Transcription(" >>> ")
		public abstract $Primitive $unsignedShiftRight(Object that);
		
		public static final String[] NAMES = { "byte", "char", "short", "int", "long" };
		
		/**
		 * @author codistmonk (creation 2013-01-16)
		 */
		public static abstract class UnaryOperations extends $Number.UnaryOperations {
			
			protected UnaryOperations() {
				throw new IllegalInstantiationException();
			}
			
			@Transcription("~")
			public static final $Linear $bitwiseComplement(final $Linear x) {
				return null;
			}
			
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $SignedLinear extends $Linear {
		
		public static final String[] NAMES = { "byte", "short", "int", "long" };
		
		/**
		 * @author codistmonk (creation 2013-01-16)
		 */
		public static abstract class UnaryOperations extends $Linear.UnaryOperations {
			
			protected UnaryOperations() {
				throw new IllegalInstantiationException();
			}
			
			@Transcription("-")
			public static final $Number $minus(final $Number x) {
				return null;
			}
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $UnsignedLinear extends $Linear {
		
		public static final String[] NAMES = { "char" };
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	public static abstract interface $Nonlinear extends $Number {
		
		public static final String[] NAMES = { "float", "double" };
		
	}
	
	/**
	 * @author codistmonk (creation 2013-01-16)
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static abstract @interface Transcription {
		
		public abstract String value();
		
	}
	
}

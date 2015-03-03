package net.sourceforge.aprog.tools;

import java.io.Serializable;

/**
 * @author codistmonk (creation 2014-04-29)
 */
public abstract interface IntComparator extends Serializable {
	
	public abstract int compare(int value1, int value2);
	
	/**
	 * @author codistmonk (creation 2014-05-23)
	 */
	public static final class Default implements IntComparator {
		
		@Override
		public final int compare(final int value1, final int value2) {
			return value1 < value2 ? -1 : value1 == value2 ? 0 : 1;
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 2338544738825140920L;
		
		public static final Default INSTANCE = new Default();
		
	}
	
}

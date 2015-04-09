/*
 *  The MIT License
 *
 *  Copyright 2015 Codist Monk.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

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
			return Integer.compare(value1, value2);
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 2338544738825140920L;
		
		public static final Default INSTANCE = new Default();
		
	}
	
}
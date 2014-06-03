/*
 *  The MIT License
 * 
 *  Copyright 2014 Codist Monk.
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

import static net.sourceforge.aprog.tools.Tools.notNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author codistmonk (creation 2014-06-03)
 */
public final class Tee extends OutputStream {
	
	private final OutputStream output1;
	
	private final OutputStream output2;
	
	/**
	 * @param output1
	 * <br>Must not be null
	 * <br>Will be stored as string reference
	 * @param output2
	 * <br>Must not be null
	 * <br>Will be stored as string reference
	 */
	public Tee(final OutputStream output1, final OutputStream output2) {
		this.output1 = notNull(output1);
		this.output2 = notNull(output2);
	}
	
	@Override
	public final void write(final int b) throws IOException {
		try {
			this.getOutput1().write(b);
		} finally {
			this.getOutput2().write(b);
		}
	}
	
	@Override
	public final void write(final byte[] bytes) throws IOException {
		try {
			this.getOutput1().write(bytes);
		} finally {
			this.getOutput2().write(bytes);
		}
	}
	
	@Override
	public final void write(final byte[] bytes, final int offset, final int length) throws IOException {
		try {
			this.getOutput1().write(bytes, offset, length);
		} finally {
			this.getOutput2().write(bytes, offset, length);
		}
	}
	
	@Override
	public final void flush() throws IOException {
		try {
			this.getOutput1().flush();
		} finally {
			this.getOutput2().flush();
		}
	}
	
	@Override
	public final void close() throws IOException {
		try {
			this.getOutput1().close();
		} finally {
			this.getOutput2().close();
		}
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference
	 */
	public final OutputStream getOutput1() {
		return this.output1;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference
	 */
	public final OutputStream getOutput2() {
		return this.output2;
	}
	
}

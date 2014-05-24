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

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.aprog.tools.ConsoleMonitor.MessageBuilder;

import org.junit.Test;

/**
 * @author codistmonk (creation 2014-05-24)
 */
public class ConsoleMonitorTest {
	
	@Test
	public final void test() {
		final AtomicInteger count = new AtomicInteger();
		final ConsoleMonitor monitor = new ConsoleMonitor(100L).setMessageBuilder(new MessageBuilder() {
			
			@Override
			public final String getMessage() {
				count.incrementAndGet();
				return MessageBuilder.Predefined.DOT.getMessage();
			}
			
			/**
			 * {@value}.
			 */
			private static final long serialVersionUID = 4881389430607213459L;
			
		});
		
		monitor.ping();
		monitor.ping();
		
		assertEquals(0L, count.get());
		
		Tools.gc(100L);
		
		monitor.ping();
		
		assertEquals(1L, count.get());
		
		Tools.gc(60L);
		
		monitor.ping();
		
		Tools.gc(60L);
		
		monitor.ping();
		
		assertEquals(2L, count.get());
		
		monitor.pause();
	}
	
}

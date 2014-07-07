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

import static net.sourceforge.aprog.tools.Tools.unchecked;
import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.Test;

/**
 * @author codistmonk (creation 2014-07-07)
 */
public final class TaskManagerTest {
	
	@Test(timeout=1000L)
	public final void test1() throws InterruptedException {
		assertEquals(0L, test(new TaskManager(0.0), 1));
	}
	
	@Test(timeout=1000L)
	public final void test2() throws InterruptedException {
		assertEquals(0L, test(new TaskManager(1.0), SystemProperties.getAvailableProcessorCount()));
	}
	
	public static final int test(final TaskManager taskManager, final int taskCount) throws InterruptedException {
		final Semaphore lockByAllTasks = new Semaphore(1 - taskCount);
		final Semaphore lockForAllTasks = new Semaphore(0);
		
		for (int i = 0; i < taskCount; ++i) {
			taskManager.submit(new Runnable() {
				
				@Override
				public final void run() {
					try {
						lockByAllTasks.release();
						lockForAllTasks.acquire();
					} catch (final InterruptedException exception) {
						throw unchecked(exception);
					}
				}
				
			});
		}
		
		lockByAllTasks.acquire();
		lockForAllTasks.release(taskCount);
		
		taskManager.join();
		
		return lockForAllTasks.availablePermits();
	}
	
}

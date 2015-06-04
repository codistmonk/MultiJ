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

package multij.tools;

import static java.lang.Math.max;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static multij.tools.Tools.unchecked;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple task manager that uses fixed-size thread pools to schedule tasks.
 * <br>A thread pool's size is determined by the maximum CPU load parameter specified during construction, but at least 1 thread is used regardless.
 * <br>A thread pool is created as needed for task submission, and shut down for joining.
 * 
 * @author codistmonk (creation 2014-05-08)
 */
public final class TaskManager implements Serializable {
	
	private final double maximumCPULoad;
	
	private ExecutorService executor;
	
	/**
	 * Calls <code>this(0.5)</code>.
	 */
	public TaskManager() {
		this(0.5);
	}
	
	/**
	 * @param maximumCPULoad
	 * <br>Range: <code>[0.0 .. 1.0]</code>
	 */
	public TaskManager(final double maximumCPULoad) {
		this.maximumCPULoad = maximumCPULoad;
	}
	
	/**
	 * @param task
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final synchronized TaskManager submit(final Runnable task) {
		this.getExecutor().submit(new Runnable() {
			
			@Override
			public final void run() {
				try {
					task.run();
				} catch (final Throwable throwable) {
					throwable.printStackTrace(Tools.getDebugErrorOutput());
				}
				
				throw new RuntimeException();
			}
			
		});
		
		return this;
	}
	
	/**
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final synchronized TaskManager join() {
		if (this.executor == null) {
			return this;
		}
		
		this.executor.shutdown();
		
		try {
			this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException exception) {
			throw unchecked(exception);
		} finally {
			this.executor = null;
		}
		
		return this;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Maybe new
	 * <br>Strong reference stored in <code>this</code>
	 */
	private final ExecutorService getExecutor() {
		if (this.executor == null) {
			this.executor = newFixedThreadPool(this.getWorkerCount());
		}
		
		return this.executor;
	}
	
	/**
	 * @return
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 */
	public final int getWorkerCount() {
		return max(1, (int) (SystemProperties.getAvailableProcessorCount() * this.maximumCPULoad));
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = 3623189981603208763L;
	
}

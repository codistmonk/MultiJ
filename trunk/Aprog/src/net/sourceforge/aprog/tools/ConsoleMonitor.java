package net.sourceforge.aprog.tools;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sourceforge.aprog.tools.TicToc;

/**
 * @author codistmonk (creation 2014-04-07)
 */
public final class ConsoleMonitor implements Serializable {
	
	private final TicToc timer;
	
	private final long periodMilliseconds;
	
	private final AtomicBoolean newLineNeeded;
	
	private MessageBuilder messageBuilder;
	
	/**
	 * @param periodMilliseconds
	 * <br>Range: any long
	 */
	public ConsoleMonitor(final long periodMilliseconds) {
		this.timer = new TicToc();
		this.periodMilliseconds = periodMilliseconds;
		this.newLineNeeded = new AtomicBoolean();
		this.messageBuilder = MessageBuilder.Predefined.DOT;
		
		this.timer.tic();
	}
	
	/**
	 * @return
	 * <br>Range: any long
	 */
	public final long getPeriodMilliseconds() {
		return this.periodMilliseconds;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference
	 */
	public final synchronized MessageBuilder getMessageBuilder() {
		return this.messageBuilder;
	}
	
	/**
	 * @param messageBuilder
	 * <br>Must not be null
	 * @return
	 * <br>Not null
	 * <br><code>this</code>
	 */
	public final synchronized ConsoleMonitor setMessageBuilder(final MessageBuilder messageBuilder) {
		this.messageBuilder = messageBuilder;
		
		return this;
	}
	
	/**
	 * Computes the elapsed time since the last call;
	 * if it is greater or equal to <code>this.getPeriodMilliseconds()</code>,
	 * then calls <code>this.getMessageBuilder().getMessage()</code>
	 * and prints the returned string to the standard output.
	 * @return <code>true</code> if more than {{@link #getPeriodMilliseconds()} have elapsed since the last call to {{@link #ping()} or {{@link #ping(String)}
	 * <br>Range: any boolean
	 */
	public final synchronized boolean ping() {
		if (this.getPeriodMilliseconds() <= this.timer.toc()) {
			System.out.print(this.getMessageBuilder().getMessage());
			this.newLineNeeded.set(true);
			this.timer.tic();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Behaves the same as {@link #ping()} but uses <code>text</code> as message
	 * (<code>this.getMessageBuilder()</code> is ignored).
	 * 
	 * @param text
	 * <br>Maybe null
	 */
	public final synchronized void ping(final String text) {
		if (this.getPeriodMilliseconds() <= this.timer.toc()) {
			System.out.print(text);
			this.newLineNeeded.set(true);
			this.timer.tic();
		}
	}
	
	/**
	 * Prints a newline character to the standard output if a previous call to {@link #ping()}
	 * or {@link ConsoleMonitor#ping(String)} printed a message.
	 */
	public final void pause() {
		if (this.newLineNeeded.getAndSet(false)) {
			System.out.println();
		}
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -3669736743010335592L;
	
	/**
	 * @author codistmonk (creation 2014-05-24)
	 */
	public static abstract interface MessageBuilder extends Serializable {
		
		/**
		 * @return
		 * <br>Maybe null
		 */
		public abstract String getMessage();
		
		/**
		 * @author codistmonk (creation 2014-05-24)
		 */
		public static enum Predefined implements MessageBuilder {
			
			DOT {
				
				@Override
				public final String getMessage() {
					return ".";
				}
				
			};
			
		}
		
	}
	
}

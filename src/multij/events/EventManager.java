/*
 *  The MIT License
 * 
 *  Copyright 2012 Codist Monk.
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

package multij.events;

import static multij.tools.Tools.append;
import static multij.tools.Tools.set;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import multij.events.EventManager.Event.Listener;
import multij.tools.Pair;

/**
 * This class provides an alternative to the {@link AbstractObservable} mechanism.
 * <br>Listeners can be attached to arbitrary objects and listener methods are annotated with {@link Listener}.
 * <br>Weak listeners are stored as weak references (see {@link WeakReference}).
 * 
 * @author codistmonk (creation 2012-06-16)
 */
public final class EventManager {
	
	private final Map<Object, Map<Object, List<Pair<Class<? extends Event<?>>, Method>>>> listeners;
	
	private final Map<Object, Object> references;
	
	private EventManager() {
		this.listeners = new WeakHashMap<Object, Map<Object, List<Pair<Class<? extends Event<?>>, Method>>>>();
		this.references = new IdentityHashMap<Object, Object>();
	}
	
	/**
	 * @param eventSource
	 * <br>Not null
	 * @param eventType
	 * <br>Not null
	 * @param listener
	 * <br>Not null
	 */
	public final synchronized void addListener(final Object eventSource, final Class<? extends Event<?>> eventType, final Object listener) {
		this.references.put(listener, null);
		this.addWeakListener(eventSource, eventType, listener);
	}
	
	/**
	 * @param eventSource
	 * <br>Not null
	 * @param eventType
	 * <br>Not null
	 * @param listener
	 * <br>Not null
	 */
	@SuppressWarnings("unchecked")
	public final synchronized void addWeakListener(final Object eventSource, final Class<? extends Event<?>> eventType, final Object listener) {
		final List<Pair<Class<? extends Event<?>>, Method>> newListenerMethods = new ArrayList<Pair<Class<? extends Event<?>>, Method>>();
		
        for (final Method method : set(append(listener.getClass().getMethods(), listener.getClass().getDeclaredMethods()))) {
        	if (method.isAnnotationPresent(Event.Listener.class)) {
        		final Class<?>[] parameterTypes = method.getParameterTypes();
        		
        		if (parameterTypes.length == 1 && eventType.isAssignableFrom(parameterTypes[0])) {
        			newListenerMethods.add(new Pair<Class<? extends Event<?>>, Method>((Class<? extends Event<?>>) parameterTypes[0], method));
        		}
        	}
        }
        
    	if (newListenerMethods.isEmpty()) {
        	return;
        }
        
    	Map<Object, List<Pair<Class<? extends Event<?>>, Method>>> sourceListeners = this.listeners.get(eventSource);
    	
    	if (sourceListeners == null) {
    		sourceListeners = new WeakHashMap<Object, List<Pair<Class<? extends Event<?>>, Method>>>();
    		
    		this.listeners.put(eventSource, sourceListeners);
    	}
    	
    	final List<Pair<Class<? extends Event<?>>, Method>> existingListenerMethods = sourceListeners.get(listener);
    	
    	if (existingListenerMethods != null) {
    		existingListenerMethods.addAll(newListenerMethods);
    	} else {
    		sourceListeners.put(listener, newListenerMethods);
    	}
	}
	
	/**
	 * @param eventSource
	 * <br>Not null
	 * @param eventType
     * <br>Not null
	 * @return
     * <br>Not null
	 */
	public final synchronized Object[] getListeners(final Object eventSource, final Class<? extends Event<?>> eventType) {
    	final Map<Object, List<Pair<Class<? extends Event<?>>, Method>>> sourceListeners = this.listeners.get(eventSource);
    	
    	if (sourceListeners == null) {
    		return EMPTY_LISTENERS;
    	}
    	
    	final Set<Object> result = new LinkedHashSet<Object>();
    	
    	for (final Map.Entry<Object, List<Pair<Class<? extends Event<?>>, Method>>> entry : sourceListeners.entrySet()) {
    		for (final Pair<Class<? extends Event<?>>, Method> listenerMethod : entry.getValue()) {
    			if (listenerMethod.getFirst().isAssignableFrom(eventType)) {
    				result.add(entry.getKey());
    			}
    		}
    	}
    	
    	return result.toArray();
	}
	
	/**
	 * @param eventSource
     * <br>Not null
	 * @param eventType
     * <br>Not null
	 * @param listener
     * <br>Not null
	 */
	public final synchronized void removeListener(final Object eventSource, final Class<? extends Event<?>> eventType, final Object listener) {
    	final Map<Object, List<Pair<Class<? extends Event<?>>, Method>>> sourceListeners = this.listeners.get(eventSource);
    	
    	if (sourceListeners == null) {
    		return;
    	}
    	
    	final List<Pair<Class<? extends Event<?>>, Method>> listenerMethods = sourceListeners.get(listener);
    	
    	if (listenerMethods == null) {
    		return;
    	}
    	
    	boolean listenerNeedsToStayStrong = false;
    	
    	for (final Iterator<Pair<Class<? extends Event<?>>, Method>> i = listenerMethods.iterator(); i.hasNext();) {
    		final Pair<Class<? extends Event<?>>, Method> listenerMethod = i.next();
    		
    		if (eventType.isAssignableFrom(listenerMethod.getFirst())) {
    			i.remove();
    		} else {
    			listenerNeedsToStayStrong = true;
    		}
    	}
    	
    	if (!listenerNeedsToStayStrong) {
    		this.references.remove(listener);
    	}
	}
	
	/**
	 * @param event
     * <br>Not null
	 */
	public final synchronized void dispatch(final Event<?> event) {
    	final Map<Object, List<Pair<Class<? extends Event<?>>, Method>>> sourceListeners = this.listeners.get(event.getSource());
    	
    	if (sourceListeners == null) {
    		return;
    	}
    	
    	for (final Map.Entry<Object, List<Pair<Class<? extends Event<?>>, Method>>> entry : sourceListeners.entrySet()) {
    		for (final Pair<Class<? extends Event<?>>, Method> listenerMethod : entry.getValue()) {
    			if (listenerMethod.getFirst().isAssignableFrom(event.getClass())) {
    				final Object listener = entry.getKey();
    				
					try {
						listenerMethod.getSecond().setAccessible(true);
						listenerMethod.getSecond().invoke(listener, event);
					} catch (final Exception exception) {
						Logger.getLogger(listener.getClass().getName() + "@" + System.identityHashCode(listener))
							.log(Level.WARNING, exception.getMessage(), exception);
					}
    			}
    		}
    	}
	}
	
	private static final Object[] EMPTY_LISTENERS = {};
	
	private static final EventManager instance = new EventManager();
	
	/**
	 * @return
     * <br>Not null
	 */
	public static final EventManager getInstance() {
		return instance;
	}
	
	/**
	 * @author codistmonk (creation 2012-06-17)
	 * @param <S> The event source type
	 */
	public static abstract interface Event<S> extends Serializable {
		
	    /**
	     * @return
	     * <br>Not null
	     */
		public abstract Object getSource();
		
		public abstract void fire();
		
		/**
		 * @author codistmonk (creation 2012-06-16)
		 */
		@Retention(RetentionPolicy.RUNTIME)
		public static @interface Listener {
			// Deliberately left empty
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2012-06-16)
	 * @param <S> The event source type
	 */
	public static abstract class AbstractEvent<S> implements Event<S> {
		
		private final EventManager eventManager;
		
		private final S source;
		
		private final long milliTime;
		
		/**
		 * @param eventManager
		 * <br>Not null
		 * @param source
		 * <br>Not null
		 * @param milliTime
		 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
		 */
		protected AbstractEvent(final EventManager eventManager, final S source, final long milliTime) {
			this.eventManager = eventManager;
			this.source = source;
			this.milliTime = milliTime;
		}
		
		/**
		 * @param source
		 * <br>Not null
		 */
		protected AbstractEvent(final S source) {
			this(EventManager.getInstance(), source, System.currentTimeMillis());
		}
		
		/**
		 * @return
         * <br>Not null
		 */
		public final EventManager getEventManager() {
			return this.eventManager;
		}
		
		@Override
		public final S getSource() {
			return this.source;
		}
		
		/**
		 * @return
         * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
		 */
		public final long getMilliTime() {
			return this.milliTime;
		}
		
		@Override
		public final void fire() {
			this.getEventManager().dispatch(this);
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 8762957052471158152L;
		
	}
	
}

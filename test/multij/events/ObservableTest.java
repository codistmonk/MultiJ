/*
 *  The MIT License
 * 
 *  Copyright 2010 Codist Monk.
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

import static multij.events.EventsTestingTools.*;
import static org.junit.Assert.*;

import java.io.Serializable;

import multij.events.AbstractObservable;
import multij.events.Observable;
import multij.events.Observable.Event;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Observable}.
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class ObservableTest {

	@Test
	public final <R extends EventRecorder<Event<?>> & DummyObservable.Listener> void testFireEvent() {
		final DummyObservable observable = new DummyObservable();
		@SuppressWarnings("unchecked")
		final R recorder1 = (R) newEventRecorder(DummyObservable.Listener.class);
		@SuppressWarnings("unchecked")
		final R recorder2 = (R) newEventRecorder(DummyObservable.Listener.class);

		observable.addListener(recorder1);
		observable.addListener(recorder2);
		observable.fireNewEvent();
		observable.removeListener(recorder2);
		observable.fireNewEvent();

		assertTrue(recorder1.getEvent(0) instanceof DummyObservable.EventFiredEvent);
		assertTrue(recorder1.getEvent(1) instanceof DummyObservable.EventFiredEvent);
		assertSame(recorder1.getEvent(0), recorder2.getEvent(0));
		assertEquals(2, recorder1.getEvents().size());
		assertEquals(1, recorder2.getEvents().size());
	}

	/**
	 *
	 * @author codistmonk (creation 2010-06-23)
	 */
	private static final class DummyObservable extends AbstractObservable<DummyObservable.Listener> {
		
		/**
		 * Package-private default constructor to suppress visibility warnings.
		 */
		DummyObservable() {
			// Do nothing
		}

		public final void fireNewEvent() {
			new EventFiredEvent().fire();
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 7489789886939477608L;

		/**
		 *
		 * @author codistmonk (creation 2010-06-23)
		 */
		public static interface Listener extends Serializable {

			/**
			 *
			 * @param event
			 * <br>Not null
			 */
			public abstract void eventFired(final EventFiredEvent event);

		}

		/**
		 *
		 * @author codistmonk (creation 2010-06-23)
		 */
		public final class EventFiredEvent extends AbstractEvent<DummyObservable, Listener> {

			@Override
			protected final void notifyListener(final Listener listener) {
				listener.eventFired(this);
			}
			
			/**
			 * {@value}.
			 */
			private static final long serialVersionUID = -6467945430815473127L;

		}

	}

}
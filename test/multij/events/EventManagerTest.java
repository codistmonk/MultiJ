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

import static multij.tools.Tools.ignore;
import multij.events.EventManager;
import multij.events.EventManager.AbstractEvent;
import multij.events.EventManager.Event.Listener;

import org.junit.Assert;
import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link EventManager}.
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class EventManagerTest {
    
    @Test
    public final void test1() {
        final class SomeEvent extends AbstractEvent<Object> {
        	
			SomeEvent(final Object source) {
                super(source);
            }
			
			/**
			 * {@value}.
			 */
			private static final long serialVersionUID = -3494951364147110136L;
            
        }
        
        final int[] eventCount = new int[1];
        
        final class SomeListener {
            
            @Listener
            final void someEventHappened(final SomeEvent event) {
            	ignore(event);
                ++eventCount[0];
            }
            
        }
        
        final Object source = new Object();
        Object weakListener = new SomeListener();
        
        EventManager.getInstance().addWeakListener(source, SomeEvent.class, weakListener);
        EventManager.getInstance().addListener(source, SomeEvent.class, new SomeListener());
        
        gc();
        
        new SomeEvent(source).fire();
        
        Assert.assertEquals(2, eventCount[0]);
        
        weakListener = null;
        gc();
        
        new SomeEvent(source).fire();
        
        Assert.assertEquals(3, eventCount[0]);
    }
    
    public static final void gc() {
        System.gc();
        
        try {
            Thread.sleep(+200L);
        } catch (final InterruptedException exception) {
            exception.printStackTrace();
        }
    }
    
}

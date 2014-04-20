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

package net.sourceforge.aprog.context;

import static java.util.Collections.unmodifiableCollection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.aprog.events.AbstractObservable;
import net.sourceforge.aprog.events.AtomicVariable;
import net.sourceforge.aprog.events.Variable;

/**
 *
 * @author codistmonk (creation 2010-06-20)
 */
public final class Context extends AbstractObservable<Context.Listener> implements Iterable<Variable<?>> {
	
	private final Map<String, Variable<?>> variables;

	public Context() {
		this.variables = new HashMap<String, Variable<?>>();
	}

	/**
	 *
	 * @param <T> The type of the variable value
	 * @param variableName
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * <br>Shared
	 */
	public final <T> T get(final String variableName) {
		final Variable<T> variable = this.getVariable(variableName);

		return variable == null ? null : variable.getValue();
	}

	/**
	 *
	 * @param <T> The type of the variable value
	 * @param variableName
	 * <br>Must not be null
	 * @param value
	 * <br>Maybe null
	 * <br>Shared
	 * @param variableType 
	 * <br>Must not be null
	 * @return The old value
	 * <br>Maybe null
	 */
	public final <T> T set(final String variableName, final T value, final Class<T> variableType) {
		final Variable<T> variable = this.getVariable(variableName);

		if (variable == null) {
			this.putVariable(new AtomicVariable<T>(variableType, variableName, value));

			return null;
		}

		final T oldValue = variable.getValue();

		variable.setValue(value);

		return oldValue;
	}

	/**
	 *
	 * @param <T> The type of the variable value
	 * @param variableName
	 * <br>Not null
	 * @param value
	 * <br>Maybe null
	 * <br>Shared
	 * @return The old value
	 * <br>Maybe null
	 */
	@SuppressWarnings("unchecked")
	public final <T> T set(final String variableName, final T value) {
		return this.set(variableName, value, (Class<T>) (value == null ? Object.class : value.getClass()));
	}

	/**
	 *
	 * @param <T> The type of the variable value
	 * @param variableName
	 * <br>Maybe null
	 * @return The removed variable
	 * <br>Maybe null
	 */
	public final <T> Variable<T> remove(final String variableName) {
		@SuppressWarnings("unchecked")
		final Variable<T> removedVariable = (Variable<T>) this.variables.remove(variableName);

		if (removedVariable != null) {
			new VariableRemovedEvent<T>(removedVariable).fire();
		}

		return removedVariable;
	}

	/**
	 *
	 * @param <T> The type of the variable value
	 * @param variableName
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * <br>Shared
	 */
	@SuppressWarnings("unchecked")
	public final <T> Variable<T> getVariable(final String variableName) {
		return (Variable<T>) this.variables.get(variableName);
	}

	/**
	 *
	 * @param <T> The type of the new variable value
	 * @param <U> The type of the old variable value
	 * @param variable
	 * <br>Not null
	 * <br>Shared
	 * @return the old variable
	 * <br>Maybe null
	 */
	public final <T, U> Variable<U> putVariable(final Variable<T> variable) {
		final Variable<U> oldVariable = this.remove(variable.getName());

		this.variables.put(variable.getName(), variable);

		new VariableAddedEvent<T>(variable).fire();

		return oldVariable;
	}

	@Override
	public final Iterator<Variable<?>> iterator() {
		return unmodifiableCollection(this.variables.values()).iterator();
	}

	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = 6781146243444157285L;

	/**
	 * 
	 * @author codistmonk (creation 2010-06-20)
	 */
	public static interface Listener {

		/**
		 *
		 * @param event
		 * <br>Not null
		 */
		public abstract void variableAdded(VariableAddedEvent<?> event);

		/**
		 *
		 * @param event
		 * <br>Not null
		 */
		public abstract void variableRemoved(VariableRemovedEvent<?> event);

	}

	/**
	 *
	 * @param <T> the type of the variable value
	 * @author codistmonk (creation 2010-06-20)
	 */
	public abstract class AbstractEvent<T> extends AbstractObservable<Listener>.AbstractEvent<Context, Listener> {
		
		private final Variable<T> variable;
		
		/**
		 *
		 * @param variable
		 * <br>Not null
		 * <br>Shared
		 */
		public AbstractEvent(final Variable<T> variable) {
			this.variable = variable;
		}
		
		/**
		 *
		 * @return
		 * <br>Not null
		 * <br>Shared
		 */
		public final Variable<T> getVariable() {
			return this.variable;
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 7658695848947908440L;
		
	}
	
	/**
	 *
	 * @param <T> The type of the variable value
	 * @author codistmonk (creation 2010-06-20)
	 */
	public final class VariableAddedEvent<T> extends AbstractEvent<T> {
		
		/**
		 *
		 * @param variable
		 * <br>Not null
		 * <br>Shared
		 */
		public VariableAddedEvent(final Variable<T> variable) {
			super(variable);
		}
		
		@Override
		protected final void notifyListener(final Listener listener) {
			listener.variableAdded(this);
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 9102305623107226542L;
		
	}
	
	/**
	 *
	 * @param <T> The type of the variable value
	 * @author codistmonk (creation 2010-06-20)
	 */
	public final class VariableRemovedEvent<T> extends AbstractEvent<T> {
		
		/**
		 *
		 * @param removedVariable
		 * <br>Not null
		 * <br>Shared
		 */
		public VariableRemovedEvent(final Variable<T> removedVariable) {
			super(removedVariable);
		}
		
		@Override
		protected final void notifyListener(final Listener listener) {
			listener.variableRemoved(this);
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 3279041274964843457L;

	}

}

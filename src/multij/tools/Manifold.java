package multij.tools;

import static multij.tools.Tools.*;

import java.io.Serializable;
import java.util.BitSet;

import multij.primitivelists.IntList;
import multij.tools.Manifold.Traversor.Limit;

/**
 * @author codistmonk (creation 2013-04-06)
 */
public final class Manifold implements Serializable {
	
	private final IntList nexts;
	
	public Manifold() {
		this(new IntList());
	}
	
	private Manifold(final IntList nexts) {
		this.nexts = nexts;
	}
	
	public final Manifold copy() {
		return new Manifold(new IntList(this.nexts.toArray()));
	}
	
	public final void clear() {
		this.nexts.clear();
	}
	
	public final int newEdge() {
		final int result = this.getDartCount();
		
		this.nexts.add(-1);
		this.nexts.add(-1);
		
		return result;
	}
	
	public final int getPrevious(final int dart, final int iterations) {
		int result = dart;
		
		for (int i = 0; i < iterations; ++i) {
			result = this.getPrevious(result);
		}
		
		return result;
	}
	
	public final int getPrevious(final int dart) {
		final Limit limit = new Limit(this);
		int result;
		int next = dart;
		
		do {
			result = next;
			next = this.getNext(result);
			
			if (next < 0) {
				return next;
			}
			
			limit.check();
		} while (next != dart);
		
		return result;
	}
	
	public final int getNext(final int dart) {
		return this.nexts.get(dart);
	}
	
	public final int getNext(final int dart, final int iterations) {
		int result = dart;
		
		for (int i = 0; i < iterations; ++i) {
			result = this.getNext(result);
		}
		
		return result;
	}
	
	public final void initializeNext(final int dart, final int next) {
		final int existingNext = this.getNext(dart);
		
		if (existingNext != -1) {
			throw new IllegalStateException(dart + " -> " + existingNext);
		}
		
		this.setNext(dart, next);
	}
	
	public final void setNext(final int dart, final int next) {
		if (DEBUG) {
			debugPrint(dart, "->", next);
		}
		
		this.nexts.set(dart, next);
	}
	
	public final int getOpposite(final int dart) {
		return opposite(dart);
	}
	
	public final int getDartCount() {
		return this.nexts.size();
	}
	
	public final int getEdgeCount() {
		return this.getDartCount() / 2;
	}
	
	/**
	 * <pre>
	 * *---dartPrev-->**---dart--->**---dartNext-->*
	 * *<--dart'Next--**<--dart'---**<--dart'Prev--*
	 * 
	 *                       |
	 *                       V
	 * 
	 * *---dartPrev-->**---dart--->**---newDart-->**---dartNext-->*
	 * *<--dart'Next--**<--dart'---**<--newDart'--**<--dart'Prev--*
	 * </pre>
	 * 
	 * @param dart
	 * <br>Range: <code>[0 .. this.getDartCount() - 1]</code>
	 * @return
	 * <br><code>oldDartCount + 1</code>
	 */
	public final int cutEdge(final int dart) {
		final int dartNext = this.getNext(dart);
		final int dartOpposite = this.getOpposite(dart);
		final int previousOfDartOpposite = this.getPrevious(dartOpposite);
		final int newDart = this.newEdge();
		final int newDartOpposite = this.getOpposite(newDart);
		
		this.setNext(dart, newDart);
		this.setNext(newDart, dartNext);
		this.setNext(previousOfDartOpposite, newDartOpposite);
		this.setNext(newDartOpposite, dartOpposite);
		
		assert this.isValid();
		
		return newDart;
	}
	
	/**
	 * <pre>
	 * *---#########-->**---#########-->*
	 * *<--#########---**<--previous2---*
	 * 
	 * *---previous1-->**---#########-->*
	 * *<--#########---**<--#########---*
	 * 
	 *                 |
	 *                 V
	 * 
	 * *<--#########---**<--#########---*
	 * *<--#########---**<--previous2---*
	 *                 ^|
	 *                 ||
	 *                 n#
	 *                 e#
	 *                 w#
	 *                 D#
	 *                 a#
	 *                 r#
	 *                 t#
	 *                 ||
	 *                 |v
	 * *---previous1-->**---#########-->*
	 * *---#########-->**---#########-->*
	 * </pre>
	 * @param previous1
	 * <br>Range: <code>[0 .. this.getDartCount() - 1]</code>
	 * @param previous2
	 * <br>Range: <code>[0 .. this.getDartCount() - 1]</code>
	 * @return
	 * <br><code>oldDartCount + 1</code>
	 */
	public final int cutFace(final int previous1, final int previous2) {
		final int nextOfResultOpposite = this.getNext(previous1);
		final int resultNext = this.getNext(previous2);
		final int result = this.newEdge();
		final int resultOpposite = this.getOpposite(result);
		
		this.setNext(previous1, result);
		this.setNext(result, resultNext);
		this.setNext(previous2, resultOpposite);
		this.setNext(resultOpposite, nextOfResultOpposite);
		
		assert this.isValid();
		
		return result;
	}
	
	public final void forEach(final Traversor traversor, final DartProcessor processor) {
		traversor.traverse(this, processor);
	}
	
	public final void forEachDartIn(final Traversor traversor, final int dart, final DartProcessor processor) {
		traversor.traverse(this, dart, processor);
	}
	
	public final boolean isValid() {
		final int n = this.getDartCount();
		
		for (int dart = 0; dart < n; ++dart) {
			final int next = this.nexts.get(dart);
			
			if (next < 0 || n <= next) {
				debugError("dart:", dart, "n:", n, "next:", next);
				return false;
			}
			
			final int previousOfNext = this.getPrevious(next);
			
			if (dart != previousOfNext) {
				debugError("dart:", dart, "n:", n, "next:", next, "next.previous:", previousOfNext);
				return false;
			}
		}
		
		return true;
	}
	
	public final void initializeCycle(final int... darts) {
		final int n = darts.length;
		
		for (int i = 0; i < n; ++i) {
			this.initializeNext(darts[i], darts[(i + 1) % n]);
		}
	}
	
	public final void setCycle(final int... darts) {
		final int n = darts.length;
		
		for (int i = 0; i < n; ++i) {
			this.setNext(darts[i], darts[(i + 1) % n]);
		}
	}
	
	@Override
	public final String toString() {
		return this.nexts.toString();
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -2538040622973580305L;
	
	private static final boolean DEBUG = false;
	
	public static final int opposite(final int dart) {
		return dart ^ 1;
	}
	
	/**
	 * @author codistmonk (creation 2013-04-06)
	 */
	public static abstract interface DartProcessor extends Serializable {
		
		public abstract void process(int dart, int index);
		
	}
	
	/**
	 * @author codistmonk (creation 2013-04-06)
	 */
	public static enum Traversor {
		
		DART {
			
			@Override
			public final void traverse(final Manifold topology, final DartProcessor processor) {
				final int dartCount = topology.getDartCount();
				
				for (int dart = 0; dart < dartCount; ++dart) {
					processor.process(dart, dart);
				}
			}
			
			@Override
			public final void traverse(final Manifold topology, final int dart, final DartProcessor processor) {
				processor.process(dart, 0);
			}
			
			@Override
			public final void mark(final Manifold topology, final int dart, final BitSet marks) {
				marks.set(dart);
			}
			
			@Override
			public final int getNextDart(final Manifold topology, final int dart) {
				return dart;
			}
			
			@Override
			public final int getPreviousDart(final Manifold topology, final int dart) {
				return dart;
			}
			
		}, EDGE {
			
			@Override
			public final void traverse(final Manifold topology, final DartProcessor processor) {
				final int dartCount = topology.getDartCount();
				
				for (int dart = 0; dart < dartCount; dart += 2) {
					processor.process(dart, dart / 2);
				}
			}
			
			@Override
			public final void traverse(final Manifold topology, final int dart, final DartProcessor processor) {
				processor.process(dart, 0);
				processor.process(opposite(dart), 1);
			}
			
			@Override
			public final void mark(final Manifold topology, final int dart, final BitSet marks) {
				marks.set(dart);
				marks.set(opposite(dart));
			}
			
			@Override
			public final int getNextDart(final Manifold topology, final int dart) {
				return opposite(dart);
			}
			
			@Override
			public final int getPreviousDart(final Manifold topology, final int dart) {
				return opposite(dart);
			}
			
		}, FACE {
			
			@Override
			public final void traverse(final Manifold topology, final DartProcessor processor) {
				this.defaultTraverse(topology, processor);
			}
			
			@Override
			public final void traverse(final Manifold topology, final int dart, final DartProcessor processor) {
				this.defaultTraverse(topology, dart, processor);
			}
			
			@Override
			public final void mark(final Manifold topology, final int dart, final BitSet marks) {
				this.defaultMark(topology, dart, marks);
			}
			
			@Override
			public final int getNextDart(final Manifold topology, final int dart) {
				return topology.getNext(dart);
			}
			
			@Override
			public final int getPreviousDart(final Manifold topology, final int dart) {
				return this.defaultGetPreviousDart(topology, dart);
			}
			
		}, VERTEX {
			
			@Override
			public final void traverse(final Manifold topology, final DartProcessor processor) {
				this.defaultTraverse(topology, processor);
			}
			
			@Override
			public final void traverse(final Manifold topology, final int dart, final DartProcessor processor) {
				this.defaultTraverse(topology, dart, processor);
			}
			
			@Override
			public final void mark(final Manifold topology, final int dart, final BitSet marks) {
				this.defaultMark(topology, dart, marks);
			}
			
			@Override
			public final int getNextDart(final Manifold topology, final int dart) {
				return topology.getNext(opposite(dart));
			}
			
			@Override
			public final int getPreviousDart(final Manifold topology, final int dart) {
				return this.defaultGetPreviousDart(topology, dart);
			}
			
		};
		
		public final int countDarts(final Manifold topology, final int dart) {
			final int[] result = { 0 };
			
			topology.forEachDartIn(this, dart, new DartProcessor() {
				
				@Override
				public final void process(final int dart, final int index) {
					++result[0];
				}
				
				private static final long serialVersionUID = 4377255397062988188L;
				
			});
			
			return result[0];
		}
		
		public final int count(final Manifold topology) {
			final int[] result = { 0 };
			
			this.traverse(topology, new DartProcessor() {
				
				@Override
				public final void process(final int dart, final int index) {
					++result[0];
				}
				
				private static final long serialVersionUID = 4377255397062988188L;
				
			});
			
			return result[0];
		}
		
		public abstract void traverse(Manifold topology, DartProcessor processor);
		
		public abstract void traverse(Manifold topology, int dart, DartProcessor processor);
		
		public abstract void mark(Manifold topology, int dart, BitSet marks);
		
		public abstract int getNextDart(Manifold topology, int dart);
		
		public abstract int getPreviousDart(Manifold topology, int dart);
		
		public final int getNextDart(Manifold topology, int dart, int stepCount) {
			int result = dart;
			
			for (int i = 0; i < stepCount; ++i) {
				result = this.getNextDart(topology, result);
			}
			
			return result;
		}
		
		public final int getPreviousDart(Manifold topology, int dart, int stepCount) {
			int result = dart;
			
			for (int i = 0; i < stepCount; ++i) {
				result = this.getPreviousDart(topology, dart);
			}
			
			return result;
		}
		
		protected final void defaultTraverse(final Manifold topology, final DartProcessor processor) {
			final int dartCount = topology.getDartCount();
			final BitSet marks = new BitSet(dartCount);
			
			for (int dart = 0, index = 0; dart < dartCount; ++dart) {
				if (!marks.get(dart)) {
					this.mark(topology, dart, marks);
					
					processor.process(dart, index);
					
					++index;
				}
			}
		}
		
		protected final void defaultTraverse(final Manifold topology, final int dart, final DartProcessor processor) {
			final Limit limit = new Limit(topology);
			int d = dart;
			int index = 0;
			
			while (true) {
				processor.process(d, index);
				
				d = this.getNextDart(topology, d);
				++index;
				
				if (d == dart) {
					break;
				}
				
				limit.check();
			}
		}
		
		protected final void defaultMark(final Manifold topology, final int dart, final BitSet marks) {
			final Limit limit = new Limit(topology);
			
			marks.set(dart);
			
			for (int d = this.getNextDart(topology, dart); d != dart; d = this.getNextDart(topology, d)) {
				marks.set(d);
				limit.check();
			}
		}
		
		protected final int defaultGetPreviousDart(final Manifold topology, final int dart) {
			final Limit limit = new Limit(topology);
			int result = dart;
			
			for (int d = this.getNextDart(topology, dart); d != dart; d = this.getNextDart(topology, d)) {
				result = d;
				
				limit.check();
			}
			
			return result;
		}
		
		/**
		 * @author codistmonk (creation 2015-07-24)
		 */
		public static final class Limit implements Serializable {
			
			private int remaining;
			
			public Limit(final Manifold topology) {
				this.remaining = topology.getDartCount();
			}
			
			public final void check() {
				if (--this.remaining < 0) {
					throw new IllegalStateException("Infinite loop detected");
				}
			}
			
			private static final long serialVersionUID = -2252537298926386937L;
			
		}
		
	}
	
}

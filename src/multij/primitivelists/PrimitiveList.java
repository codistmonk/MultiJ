package multij.primitivelists;

import java.io.Serializable;

/**
 * @author codistmonk (creation 2014-04-27)
 */
public interface PrimitiveList extends Serializable {
	
	public abstract PrimitiveList clear();
	
	public abstract int size();
	
	public abstract boolean isBeingTraversed();
	
	public abstract PrimitiveList resize(int newSize);
	
	public abstract PrimitiveList pack();
	
	public abstract boolean isEmpty();
	
	public abstract PrimitiveList sort();
	
	public abstract void checkIndex(int index);
	
}

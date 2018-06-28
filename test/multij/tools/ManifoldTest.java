package multij.tools;

import static multij.tools.Manifold.opposite;
import static multij.tools.Manifold.Traversor.DART;
import static multij.tools.Manifold.Traversor.EDGE;
import static multij.tools.Manifold.Traversor.FACE;
import static multij.tools.Manifold.Traversor.VERTEX;
import static multij.tools.Tools.ints;
import static org.junit.Assert.*;

import org.junit.Test;

import multij.primitivelists.IntList;

/**
 * Automated tests using JUnit 4 for {@link Manifold}.
 * 
 * @author codistmonk (creation 2018-06-28)
 */
public final class ManifoldTest {
	
	@Test
	public final void test1() {
		final Manifold m = new Manifold();
		
		testValid(m, 0, 0);
		
		int ab = m.newEdge();
		final int bc = m.newEdge();
		final int ca = m.newEdge();
		int ba = opposite(ab);
		final int cb = opposite(bc);
		final int ac = opposite(ca);
		
		assertEquals(ab, opposite(ba));
		assertEquals(bc, opposite(cb));
		assertEquals(ca, opposite(ac));
		assertEquals(ab, m.getOpposite(ba));
		assertEquals(ba, m.getOpposite(ab));
		
		m.setNext(ab, bc);
		
		assertFalse(m.isValid());
		assertEquals(bc, m.getNext(ab));
		assertEquals(-1, m.getNext(ba));
		
		m.setNext(bc, ca);
		m.setNext(ca, ab);
		
		assertEquals(ca, m.getNext(ab, 2));
		
		m.setCycle(ba, ac, cb);
		
		assertEquals(ac, m.getNext(ba));
		assertEquals(new IntList(ints(bc, ac, ca, ba, ab, cb)).toString(), m.toString());
		
		testValid(m, 6, 3);
		assertEquals(ab, m.getPrevious(bc));
		assertEquals(ab, m.getPrevious(ca, 2));
		
		{
			final int[] tmp = { 0 };
			
			m.forEach(FACE, (f, i) -> {
				assertEquals(3, FACE.countDarts(m, f));
				assertEquals(tmp[0], i);
				++tmp[0];
			});
		}
		
		final Manifold m2 = m.copy();
		
		testValid(m2, 6, 3);
		
		final int db = m.cutEdge(ab);
		final int ad = ab;
		final int da = opposite(ad);
		ba = ab = -1;
		
		assertEquals(db, m.getNext(ad));
		
		testValid(m, 8, 4);
		testValid(m2, 6, 3);
		
		m.forEachDartIn(FACE, ad, (d, i) -> {
			assertEquals(4, FACE.countDarts(m, d));
		});
		
		m2.clear();
		
		testValid(m2, 0, 0);
		testValid(m, 8, 4);
		
		assertEquals(m.getDartCount(), DART.count(m));
		assertEquals(m.getEdgeCount(), EDGE.count(m));
		assertEquals(4, VERTEX.count(m));
		assertEquals(2, FACE.count(m));
		
		final int dc = m.cutFace(ad, bc);
		
		assertEquals(dc, m.getNext(ad));
		assertEquals(3, FACE.countDarts(m, ad));
		assertEquals(3, FACE.countDarts(m, db));
		assertEquals(4, FACE.countDarts(m, da));
		
		testValid(m, 10, 5);
		assertEquals(4, VERTEX.count(m));
		assertEquals(3, FACE.count(m));
	}
	
	@Test(expected=IllegalStateException.class)
	public final void test2() {
		final Manifold m = new Manifold();
		
		testValid(m, 0, 0);
		
		final int ab = m.newEdge();
		final int ba = opposite(ab);
		
		m.initializeCycle(ab, ba);
		
		testValid(m, 2, 1);
		
		m.initializeCycle(ab, ba);
	}
	
	@Test(expected=IllegalStateException.class)
	public final void test3() {
		final Manifold m = new Manifold();
		
		testValid(m, 0, 0);
		
		final int ab = m.newEdge();
		final int ba = opposite(ab);
		
		m.initializeNext(ab, ba);
		m.initializeNext(ba, ab);
		
		testValid(m, 2, 1);
		
		m.initializeNext(ab, ba);
	}
	
	private static final void testValid(final Manifold m, final int expectedDartCount, final int expectedEdgeCount) {
		assertTrue(m.isValid());
		assertEquals(expectedDartCount, m.getDartCount());
		assertEquals(expectedEdgeCount, m.getEdgeCount());
	}
	
}

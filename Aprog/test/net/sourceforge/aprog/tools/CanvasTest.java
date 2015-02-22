package net.sourceforge.aprog.tools;

import static org.junit.Assert.*;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

/**
 * @author codistmonk (creation 2015-02-22)
 */
public final class CanvasTest {
	
	@Test
	public final void testFormat() {
		final Canvas canvas = new Canvas();
		
		assertNull(canvas.getImage());
		assertEquals(0L, canvas.getWidth());
		assertEquals(0L, canvas.getHeight());
		
		canvas.setFormat(1, 2, BufferedImage.TYPE_3BYTE_BGR);
		
		BufferedImage image = canvas.getImage();
		
		assertNotNull(image);
		assertEquals(1L, image.getWidth());
		assertEquals(2L, image.getHeight());
		assertEquals(BufferedImage.TYPE_3BYTE_BGR, image.getType());
		
		Graphics2D g = canvas.getGraphics();
		
		assertNotNull(g);
		assertSame(g, canvas.getGraphics());
		
		canvas.setFormat(1, 2, BufferedImage.TYPE_3BYTE_BGR);
		
		assertSame(image, canvas.getImage());
		assertSame(g, canvas.getGraphics());
		
		canvas.setFormat(1, 2, BufferedImage.TYPE_INT_ARGB);
		
		assertNotSame(image, canvas.getImage());
		assertNotSame(g, canvas.getGraphics());
		
		image = canvas.getImage();
		g = canvas.getGraphics();
		
		canvas.setFormat(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		assertNotSame(image, canvas.getImage());
		assertNotSame(g, canvas.getGraphics());
	}
	
	@Test
	public final void testClear() {
		final Canvas canvas = new Canvas().setFormat(1, 1);
		
		assertEquals(0L, canvas.getImage().getRGB(0, 0));
		
		final int rgb = 0x01234567;
		
		canvas.clear(new Color(rgb, true));
		
		assertEquals(AlphaComposite.Src, canvas.getGraphics().getComposite());
		assertEquals(new Color(rgb, true), canvas.getGraphics().getColor());
		assertEquals(rgb, canvas.getImage().getRGB(0, 0));
	}
	
}

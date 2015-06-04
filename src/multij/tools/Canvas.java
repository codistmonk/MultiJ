package multij.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author codistmonk (creation 2014-02-15)
 */
public final class Canvas implements Serializable {
	
	private transient BufferedImage image;
	
	private transient Graphics2D graphics;
	
	/**
	 * @return image width or 0 if image is not yet defined
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 */
	public final int getWidth() {
		return this.getImage() == null ? 0 : this.getImage().getWidth();
	}
	
	/**
	 * @return image height or 0 if image is not yet defined
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 */
	public final int getHeight() {
		return this.getImage() == null ? 0 : this.getImage().getHeight();
	}
	
	/**
	 * @return
	 * <br>Maybe null
	 */
	public final BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * @return
	 * <br>Maybe null
	 */
	public final Graphics2D getGraphics() {
		return this.graphics;
	}
	
	/**
	 * Calls {@link #setFormat(int, int, int)} with <code>bufferedImageType = BufferedImage.TYPE_INT_ARGB</code>.
	 * 
	 * @param width
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param height
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Canvas setFormat(final int width, final int height) {
		return this.setFormat(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Updates image and graphics objects if image is not yet defined or any of the parameters is different from the current values. 
	 * @param width
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param height
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param bufferedImageType
	 * <br> Range: BufferedImage types
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Canvas setFormat(final int width, final int height, final int bufferedImageType) {
		if (this.getImage() == null || this.getImage().getWidth() != width || this.getImage().getHeight() != height ||
				this.getImage().getType() != bufferedImageType) {
			if (this.getGraphics() != null) {
				this.getGraphics().dispose();
			}
			
			this.image = new BufferedImage(width, height, bufferedImageType);
			this.graphics = this.getImage().createGraphics();
		}
		
		return this;
	}
	
	/**
	 * Fills image with <code>color</code>.
	 * <br>Image must already be defined.
	 * <br>Graphics's composite is set to {@link AlphaComposite#Src}.
	 * <br>Graphics's color is set to <code>color</code>.
	 * 
	 * @param color
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Canvas clear(final Color color) {
		this.getGraphics().setComposite(AlphaComposite.Src);
		this.getGraphics().setColor(color);
		this.getGraphics().fillRect(0, 0, this.getWidth(), this.getHeight());
		
		return this;
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -299324620065690574L;
	
}

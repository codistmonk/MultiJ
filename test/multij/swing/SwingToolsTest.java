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

package multij.swing;

import static multij.tools.Tools.*;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import multij.swing.SwingTools;
import multij.tools.SystemProperties;
import multij.tools.Tools;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link SwingTools}.
 * <br>Some methods that affect the visual appearance of components are not tested.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SwingToolsTest {
	
	@Test
	public final void testCenter() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(() -> {
			final JLabel component = new JLabel();
			final JPanel view = SwingTools.center(component);
			
			assertTrue(SwingUtilities.isDescendingFrom(component, view));
		});
	}
	
	@Test
	public final void testView() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(() -> {
			final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
			final JLabel view = SwingTools.view(image);
			
			assertSame(image, ((ImageIcon) view.getIcon()).getImage());
		});
	}
	
	@Test(timeout=TEST_TIMEOUT)
	public final void testJoinEventThread() throws InterruptedException {
		final Window window = SwingTools.show(new JLabel(), Tools.getThisMethodName(), false);
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public final void run() {
				Tools.gc(200L);
				window.dispose();
			}
			
		});
		
		SwingTools.getAWTEventDispatchingThread().join();
	}
	
	@Test
	public final void testFind() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public final void run() {
				final JFrame frame = new JFrame();
				final JPanel panel = new JPanel();
				final JTextArea component1 = new JTextArea();
				final JTextArea component2 = new JTextArea();
				
				frame.setName("frame");
				panel.setName("panel");
				component1.setName("component1");
				component2.setName("component2");
				
				frame.add(panel);
				panel.add(component1);
				panel.add(component2);
				
				assertSame(frame, SwingTools.find("frame", frame));
				assertSame(panel, SwingTools.find("panel", frame));
				assertSame(component1, SwingTools.find("component1", frame));
				assertSame(component2, SwingTools.find("component2", frame));
				assertSame(component1, SwingTools.find(JTextArea.class, frame));
				assertSame(component1, SwingTools.find(JTextComponent.class, frame));
			}
			
		});
	}
	
	@Test
	public final void testGetIcon() {
		SwingTools.setImagesBase(getThisPackagePath());
		
		assertNotNull(SwingTools.getIcon("start.png"));
	}
	
	@Test
	public final void testGetIconOrNull() {
		assertNull(SwingTools.getIconOrNull("inexisting_icon"));

		SwingTools.setImagesBase(getThisPackagePath());

		assertNotNull(SwingTools.getIconOrNull("start.png"));
	}

	@Test
	public final void testAdd() throws Exception {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JPanel panel = new JPanel(new FlowLayout());
			final JLabel label = new JLabel();

			assertTrue(panel.getLayout() instanceof FlowLayout);

			SwingTools.add(panel, label, new GridBagConstraints());

			assertTrue(panel.getLayout() instanceof GridBagLayout);
			assertSame(label, panel.getComponents()[0]);
			assertEquals(1, panel.getComponentCount());
		}
	}

	@Test
	public final void testRollover() throws Exception {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			SwingTools.setImagesBase(getThisPackagePath());

			final JButton button = SwingTools.rollover(new JButton(), "start", false);

			assertNotNull(button.getIcon());
			assertNotNull(button.getRolloverIcon());
			assertFalse(button.isBorderPainted());
		}
	}

	@Test
	public final void testScrollable() throws Exception {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final Component component = new JLabel();
			final JScrollPane scrollPane = SwingTools.scrollable(component);

			assertSame(component, scrollPane.getViewport().getView());
		}
	}

	@Test
	public final void testMenuBar() throws Exception {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JMenuBar menuBar = SwingTools.menuBar(
					null,
					SwingTools.menu("menu1",
							new JMenuItem("item1"),
							new JMenuItem("item2")),
					SwingTools.menu("menu2",
							new JMenuItem("item3"),
							new JMenuItem("item4"),
							new JMenuItem("item5")));

			assertEquals("menu1", menuBar.getMenu(0).getText());
			assertEquals(2, menuBar.getMenu(0).getItemCount());
			assertEquals("menu2", menuBar.getMenu(1).getText());
			assertEquals(3, menuBar.getMenu(1).getItemCount());
		}
	}

	@Test
	public final void testMenu() throws Exception {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JMenu menu = SwingTools.menu("menu",
					new JMenuItem("item1"),
					null,
					null,
					new JMenuItem("item2"));

			assertEquals("menu", menu.getText());
			assertEquals(3, menu.getItemCount());
			assertEquals("item1", menu.getItem(0).getText());
			assertNull(menu.getItem(1));
			assertEquals("item2", menu.getItem(2).getText());
		}
	}
	
	@Test
	public final void testHorizontalBox() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JLabel component1 = new JLabel("1");
			final JLabel component2 = new JLabel("2");
			final Box box = SwingTools.horizontalBox(component1, component2);

			assertNotNull(box);
			assertArrayEquals(array(component1, component2), box.getComponents());
		}
	}
	
	@Test
	public final void testVerticalBox() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JLabel component1 = new JLabel("1");
			final JLabel component2 = new JLabel("2");
			final Box box = SwingTools.verticalBox(component1, component2);

			assertNotNull(box);
			assertArrayEquals(array(component1, component2), box.getComponents());
		}
	}

	@Test
	public final void testHorizontalSplit() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JLabel component1 = new JLabel("1");
			final JLabel component2 = new JLabel("2");
			final JSplitPane splitPane = SwingTools.horizontalSplit(component1, component2);

			assertNotNull(splitPane);
			assertEquals(JSplitPane.HORIZONTAL_SPLIT, splitPane.getOrientation());
			assertEquals(splitPane, component1.getParent());
			assertEquals(splitPane, component2.getParent());
		}
	}

	@Test
	public final void testVerticalSplit() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JLabel component1 = new JLabel("1");
			final JLabel component2 = new JLabel("2");
			final JSplitPane splitPane = SwingTools.verticalSplit(component1, component2);

			assertNotNull(splitPane);
			assertEquals(JSplitPane.VERTICAL_SPLIT, splitPane.getOrientation());
			assertEquals(splitPane, component1.getParent());
			assertEquals(splitPane, component2.getParent());
		}
	}
	
	@Test
	public final void testGetFiles() {
		final DropTarget dropTarget = new DropTarget();
		final DropTargetContext dtc = dropTarget.getDropTargetContext();
		final DropTargetDropEvent event = new DropTargetDropEvent(dtc, new Point(), DnDConstants.ACTION_MOVE, DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY_OR_MOVE) {
			
			@Override
			public final Transferable getTransferable() {
				return new StringSelection("file1" + SystemProperties.getLineSeparator() + "file2");
			}
			
			@Override
			public final boolean isDataFlavorSupported(final DataFlavor df) {
				return SwingTools.getURIListAsStringFlavor().equals(df);
			}
			
			private static final long serialVersionUID = 7290136898788585439L;
			
		};
		
		assertEquals(Arrays.asList("file1", "file2").toString(), SwingTools.getFiles(event).toString());
	}

	@Test
	public final void testCanInvokeThisMethodInAWT() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			SwingTools.checkAWT();
		}
	}

	@Test(timeout=TEST_TIMEOUT)
	public final void testCanInvokeLaterThisMethodInAWT() throws InterruptedException {
		final Semaphore semaphore = new Semaphore(0);

		releaseInAWT(semaphore);

		semaphore.acquire();
	}

	@Test(timeout=TEST_TIMEOUT)
	public final void testAction() throws InterruptedException {
		final Semaphore semaphore = new Semaphore(0);

		final ActionListener actionListener = SwingTools.action(this.getClass(), "releaseInAWT", semaphore);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				actionListener.actionPerformed(new ActionEvent(this, 0, ""));
			}

		});

		semaphore.acquire();
	}

	@Test(expected=IllegalStateException.class)
	public final void testCheckAWT1() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public final void run() {
				// Doesn't throw
				SwingTools.checkAWT();
			}

		});

		// Throws
		SwingTools.checkAWT();
	}
	
	@Test
	public final void testCheckAWT2() {
		SwingTools.setCheckAWT(false);
		SwingTools.checkAWT();
		SwingTools.setCheckAWT(true);
	}
	
	@Test
	public final void testCheckNotAWT() throws Exception {
		// Doesn't throw
		SwingTools.checkNotAWT();

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public final void run() {
				try {
					// Throws
					SwingTools.checkNotAWT();

					fail("This section wasn't supposed to be reached");
				} catch (final IllegalStateException expectedException) {
					ignore(expectedException);
				}
			}

		});
	}

	/**
	 * {@value} milliseconds.
	 */
	public static final long TEST_TIMEOUT = 2000L;
	
	/**
	 * @param semaphore
	 * <br>Not null
	 * <br>Input-output
	 * <br>Shared
	 */
	private static final void releaseInAWT(final Semaphore semaphore) {
		if (SwingTools.canInvokeLaterThisMethodInAWT(null, semaphore)) {
			SwingTools.checkAWT();

			semaphore.release();
		}
	}
	
}
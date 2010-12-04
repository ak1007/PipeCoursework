package pipe.gui;

import java.awt.*;

import javax.swing.*;

import junit.extensions.abbot.*;
import abbot.finder.*;
import abbot.tester.*;

public class GuiFrameTest extends ComponentTestFixture {

	private ComponentTester tester;
	private GuiFrame frame;
	
	public GuiFrameTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		tester = new ComponentTester();
		CreateGui.init();
		frame = CreateGui.getApp();
		frame.setVisible(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		tester = null;
		frame.dispose();
		int lastIndex = CreateGui.getFreeSpace();
		while(lastIndex >= 0){
			CreateGui.removeTab(0); //deletes static ref to net objects
			lastIndex--;			//kept by CreateGui
		}
	}
	
	public void testModeButtonsChangeMode() throws Exception {
		Component button = getFinder().find(new Matcher(){
				public boolean matches(Component c){
					return JToggleButton.class.isInstance(c)
					&& ((JToggleButton)c).getAction().getValue(Action.NAME)
					.equals("Place");
				}
		});
		tester.actionClick(button);
		assertEquals("Mode not set correctly by Place button",
				Constants.PLACE,frame.getMode());
	}
	
	public void testSelectModeActivatedAtStartUp() throws Exception {
		assertTrue("Select mode not active at startup",
				frame.getMode() == Constants.SELECT);
	}

}

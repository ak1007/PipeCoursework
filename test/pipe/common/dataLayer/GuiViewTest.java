package pipe.common.dataLayer;

import java.awt.Component;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import junit.extensions.abbot.ComponentTestFixture;
import pipe.common.dataLayer.PetriNetObject;
import pipe.common.dataLayer.PlaceTransitionObject;
import pipe.gui.CreateGui;
import pipe.gui.GuiFrame;
import pipe.gui.GuiView;
import pipe.gui.ZoomController;
import abbot.finder.Matcher;
import abbot.tester.ComponentLocation;
import abbot.tester.ComponentTester;

public class GuiViewTest extends ComponentTestFixture {

	private ComponentTester tester;
	private GuiView view;
	private String testFileString = "test/resources/ClassicGSPN.xml"; 
	
	public GuiViewTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		tester = new ComponentTester();
		CreateGui.init();
		GuiFrame frame = CreateGui.getApp();
		frame.createNewTab(testFileString);
		view = CreateGui.getView();
		frame.setVisible(true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		tester = null;
		CreateGui.getApp().dispose();
		int lastIndex = CreateGui.getFreeSpace();
		while(lastIndex >= 0){
			CreateGui.removeTab(0); //deletes static ref to net objects
			lastIndex--;			//kept by CreateGui
		}
	}

	public void testOperationOfClickAndDragSelection() throws Exception {
		Component button1 = getFinder().find(new Matcher(){
			public boolean matches(Component c){
				return JToggleButton.class.isInstance(c)
				&& ((JToggleButton)c).getAction().getValue(Action.NAME)
				.equals("Place");
			}
		});
		Component button2 = getFinder().find(new Matcher(){
			public boolean matches(Component c){
				return JToggleButton.class.isInstance(c)
				&& ((JToggleButton)c).getAction().getValue(Action.NAME)
				.equals("Select");
			}
		});
		tester.actionClick(button1);
		tester.actionClick(button2);
		tester.actionDrag(view, new ComponentLocation(new Point(105,105)));
		tester.actionMouseMove(view,new ComponentLocation(new Point(425,175)));
		tester.actionMouseRelease();
		assertEquals("Expected number of objects not selected",3,
				view.getSelectionObject().getSelectionCount());
	}

	public void testSizeOfComponentsAfterZoom() throws Exception {
		double drawnWidth;
		Component[] components = view.getComponents();
		int[] expectedSizes = getComponentSizes(components);
		for (int i = 0; i < components.length; i++){
			drawnWidth = expectedSizes[i] 
			      - 2 * PetriNetObject.COMPONENT_DRAW_OFFSET;
			if (drawnWidth > 0) drawnWidth = drawnWidth * 1.2;
			expectedSizes[i] = (int)(drawnWidth 
					+ 2 * PetriNetObject.COMPONENT_DRAW_OFFSET);
		}
		Component button = getFinder().find(new Matcher(){
			public boolean matches(Component c){
				return JButton.class.isInstance(c)
				&& ((JButton)c).getAction().getValue(Action.NAME)
				.equals("Zoom in");
			}
		});	
		tester.actionClick(button);
		tester.actionClick(button);
		components = view.getComponents();
		int[] actualSizes = getComponentSizes(components);
		assertTrue(Arrays.equals(actualSizes,expectedSizes));
	}
	
	public void testPositionOfComponentsAfterZoom() throws Exception {
		Component[] components = view.getComponents();
		int[][] expectedLocations = getComponentLocations(components);
		for (int i = 0; i < components.length; i++){
			expectedLocations[i][0] = (int)(expectedLocations[i][0] * 70 / 100.0);
			expectedLocations[i][1] = (int)(expectedLocations[i][1] * 70 / 100.0);
		}
		Component button = getFinder().find(new Matcher(){
			public boolean matches(Component c){
				return JButton.class.isInstance(c)
				&& ((JButton)c).getAction().getValue(Action.NAME)
				.equals("Zoom out");
			}
		});
		tester.actionClick(button);
		tester.actionClick(button);
		tester.actionClick(button);
		components = view.getComponents();
		int[][] actualLocations = getComponentLocations(components);
		assertTrue(locationsMatch(actualLocations, expectedLocations));
	}
	
	private boolean locationsMatch(int[][] first, int[][] second) {
		boolean match = true;
		for (int i = 0; i < first.length; i++){
			if (!Arrays.equals(first[i],second[i])){
				match = false;
			}
		}
		return match;
	}

	private int[][] getComponentLocations(Component[] components) {
		int[][] locations = new int[components.length][2];
		for (int i = 0; i < components.length; i++){
			if (components[i] instanceof PlaceTransitionObject){
				locations[i][0] = (int)(
						(PlaceTransitionObject)components[i]).getPositionX();
				locations[i][1] = (int)(
						(PlaceTransitionObject)components[i]).getPositionY();				
			}
		}
		return locations;	}

	private int[] getComponentSizes(Component[] components){
		int[] sizes = new int[components.length];
		for (int i = 0; i < components.length; i++){
			if (components[i] instanceof PlaceTransitionObject){
				sizes[i] = components[i].getWidth();
			}
		}
		return sizes;
	}
	
}

package pipe.gui;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import junit.extensions.abbot.*;
import junit.framework.*;
import pipe.common.dataLayer.*;
import abbot.tester.*;

/**
 * This class creates a functional test suite by searching a nominated directory
 * for xml scripts. It also provides custom methods for testing certain components.
 * @author Tim Kimber
 */
public class FunctionalScriptsTest extends ScriptFixture{
	
	private static int[][] TOKEN_COORDS = {{-5,3,-11,-10,2},{-5,-10,-11,3,2}};
	
	private static String scriptsDir = "test/functional";
	
	public FunctionalScriptsTest(String filename) {
		super(filename);
	}
	
	/* Provide a default test suite for this test case.*/
	public static Test suite() { 
		return new ScriptTestSuite(FunctionalScriptsTest.class,
				scriptsDir) {
			public boolean accept(File file) {
				return super.accept(file)
				&& file.getName().endsWith(".xml");
			}
		};
	}
	

	/** 
	 * @see junit.extensions.abbot.ScriptFixture#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		CreateGui.getApp().dispose();
		int lastIndex = CreateGui.getFreeSpace();
		while(lastIndex >= 0){
			CreateGui.removeTab(0); //deletes static ref to net objects
			lastIndex--;			//kept by CreateGui
		}
	}
	
	/**
	 * Checks whether the PetriNetObject component contains the 
	 * specified colour. This assertion fails if the colour is not found.
	 * @param stepDescription a String describing where in the script this step
	 * occurs. This is output in the test failure message.
	 * @param component The PetriNetObject being tested
	 * @param color The color to search for
	 * @throws Exception
	 */
	public static void assertComponentIsPaintedCorrectColor(
			String stepDescription, PetriNetObject component, String color)
	throws Exception {
		
		Color expectedColor = parseColor(color);
		BufferedImage bi = createWhiteImage(component.getWidth(),
				component.getHeight());
		
		component.paintComponent(bi.createGraphics());
		assertTrue("Expected colour not found within component: "
				+ stepDescription, imageContainsColor(bi,expectedColor));
	}
	
	/**
	 * Checks whether the PetriNetObject component contains the 
	 * specified colour. This assertion fails if the colour is found.
	 * @param stepDescription a String describing where in the script this step
	 * occurs. This is output in the test failure message.
	 * @param component The PetriNetObject being tested
	 * @param color The color to search for
	 * @throws Exception
	 */
	public static void assertNotComponentContainsColor(
			String stepDescription, PetriNetObject component, String color)
	throws Exception {
		
		Color unwantedColor = parseColor(color);
		BufferedImage bi = createWhiteImage(component.getWidth(),
				component.getHeight());
		
		component.paintComponent(bi.createGraphics());
		assertTrue("Unwanted colour found within component: "
				+ stepDescription, !imageContainsColor(bi,unwantedColor));
	}

	/**
	 * Checks whether "place" contains the specified number of tokens. The
	 * assertion fails if the Place contains a different number of tokens.
	 * @param message A String descrbing where in the script this test occurs.
	 * This is output in the test failure message.
	 * @param place The pipe.gui.Place being tested.
	 * @param expected The number of tokens expected.
	 * @throws Exception
	 */
	public static void assertNumberOfTokensInPlace(String message,
			Place place, int expected) throws Exception{

		int tokens = 0;
		BufferedImage bi = createWhiteImage(place.getWidth(),
				place.getHeight());
		place.paintComponent(bi.createGraphics());
		BufferedImage fragment;

		for(int count = 0; count < 5; count++){
			fragment = bi.getSubimage(
					(bi.getWidth() / 2) + TOKEN_COORDS[0][count],
					(bi.getHeight() / 2) + TOKEN_COORDS[1][count],
					10,10);
			if (imageContainsColor(fragment,Color.BLACK)){
				tokens++;
			}
		}
		
		assertEquals(message + ": wrong number of tokens in this place",
				tokens,expected);
	}
	
	/**
	 * Returns the java.awt.Color object corresponding to the String namedColor
	 * by reference to the pipe.gui.Constants class.
	 * @param namedColor 
	 * @return the Color object corresponding to namedColor
	 * @throws Exception
	 */
	protected static Color parseColor(String namedColor) throws Exception {
		Color color = (Color)(Class.forName(
		"pipe.gui.Constants").getField(namedColor).get(null));
		return color;
	}
	
	/**
	 * Creates a BufferedImage of size width x height and paints it completely white.
	 * @param width
	 * @param height
	 * @return a white BufferedImage of size width x height
	 */
	protected static BufferedImage createWhiteImage(int width, int height)
	{
		BufferedImage bi =
			new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		return bi;
	}
	
	/**
	 * Checks whether the BufferedImage bi contains the 
	 * specified color. Returns true if the color is found, or false if it is not.
	 * @param bi The BufferedImage being tested
	 * @param color The color to search for
	 * @return true if the Color is found, false if not
	 */
	protected static boolean imageContainsColor(BufferedImage bi, Color color){
		int rgbToFind = color.getRGB();
		for (int x = 0; x < bi.getWidth(); x++ )
		{
			for (int y = 0; y < bi.getHeight(); y++ )
			{
				if (bi.getRGB(x, y) == rgbToFind)
					return true;
			}
		}
		return false;
	}

	
/*	
 *  Used to generate test images 
 *
   public static void makeImage(PetriNetObject component) throws Exception{

		BufferedImage bi = createWhiteImage(
				component.getWidth(),component.getHeight());
		component.paintComponent(bi.createGraphics());
		BufferedImage part = bi.getSubimage(
				(bi.getWidth() / 2) + 2,
				(bi.getHeight() / 2) + 2,
				10,10);
		ImageIO.write(part,"png", new File("test/functional/bitOfPlace.png"));

	}
	
	public static void main (String[] args){

		Place p = new Place(5,5);
		p.setCurrentMarking(5);
		try{
			makeImage(p);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	*/
}

package abbot.tester.extensions;

import java.awt.Component;

import pipe.common.dataLayer.Arc;
import abbot.tester.ComponentTester;

/**
 * @author Tim Kimber
 * Custom Abbot tester class for the pipe.dataLayer.Arc component
 */
public class ArcTester extends ComponentTester {

	/**
	 * Generates a string identifier to help the Abbot robot find the 
	 * correct Arc.
	 * @param comp the component being referenced
	 * @return a java.lang.String uniquely identifying this Arc component
	 * @see abbot.tester.ComponentTester#deriveTag(java.awt.Component)
	 */
	public String deriveTag(Component comp) {
		Arc a  = (Arc)comp;
		StringBuffer tag = new StringBuffer();
		String[][] details = a.getArcPath().getArcPathDetails();
		for (int row = 0; row < details.length; row ++){
			for (int col = 0; col < details[0].length; col++){
				tag.append(details[row][col]);
			}
		}
		return tag.toString();
	}
	
}

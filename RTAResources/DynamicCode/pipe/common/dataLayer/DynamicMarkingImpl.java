/*IMPORTANT NOTE: Do not remove any comments starting //#$# These are markings needed
 * when adding the logical expressions for deciding start and target states at runtime
 */
package pipe.common.dataLayer;

import pipe.io.NewStateRecord;
/**
 * Template class that is modified at run-time and saved as DynamicMarkingImpl.java, before
 * being compiled, loaded and used. User inputted logical if statements are added to the
 * code.
 * 
 * @author Oliver Haggarty - August 2007
 *
 */
public class DynamicMarkingImpl implements DynamicMarking {
	//IMPORTANT NOTE: Do not remove any comments starting //#$#
	//These are required by the DynamicMarkingCompiler class to identify where to insert
	//additional code at runtime
	
	/**
	 * Identifies whether the marking is classified as a target state according to 
	 * the logical expression written at runtime
	 * @param marking Marking to be tested
	 * @return true if matches expression	 
	 */
	public boolean isTargetMarking(NewStateRecord marking) {
		int [] p = marking.getState();
		int id = marking.getID();
	if( p[1] > 0) 
			return true;
		else
			return false;
	}
	
	/**
	 * Identifies whether the marking is classified as a start state according to the 
	 * logical expression written at runtime.
	 * @param marking Marking to be tested
	 * @return true if matches expression
	 */
	public boolean isStartMarking(NewStateRecord marking) {
		int [] p = marking.getState();
		int id = marking.getID();
	if( p[0] > 0) 
			return true;
		else
			return false;
	}
}

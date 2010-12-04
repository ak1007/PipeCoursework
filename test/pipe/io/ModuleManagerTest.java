package pipe.io;

import java.awt.List;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import junit.framework.*;
import pipe.gui.ModuleManager;

/** 
 * This JUnit TestCase tests dot file integrity
 * @author Matthew Worthington March 2007
 * 
 *
 */
public class ModuleManagerTest extends TestCase{
	private ModuleManager manager = new ModuleManager();
	private List modules = new List();
	protected void setUp() throws Exception {
		super.setUp();
		listModules(modules);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		modules=null;
		manager=null;
	}

	public void testModuleManager()throws Exception{

		File dir = manager.getModuleDir();
		// get the names of all the classes that are confirmed to be modules
		Vector names = manager.getModuleClasses(dir);
		Enumeration classes = names.elements();
		
		int itemNumber=0;
		while (classes.hasMoreElements()) {
			Class moduleClass = (Class)classes.nextElement();
			String moduleName=moduleClass.getName();
			System.out.println(moduleName);	
			
			//make sure all the applications modules are correctly loaded
			assertTrue(moduleName==modules.getItem(itemNumber));
			itemNumber++;
		}
	}
	
	private void listModules(List moduleList){
		moduleList.add("pipe.modules.stateSpace.StateSpace");
		moduleList.add("pipe.modules.simulation.Simulation");
		moduleList.add("pipe.modules.reachability.ReachabilityGraphGenerator");
		moduleList.add("pipe.modules.matrixes.Matrixes");
		moduleList.add("pipe.modules.invariantAnalysis.InvariantAnalysis");
		moduleList.add("pipe.modules.gspn.GSPNNew");
		moduleList.add("pipe.modules.dnamaca.Dnamaca");
		moduleList.add("pipe.modules.comparison.Comparison");
		moduleList.add("pipe.modules.classification.Classification");
	}

}
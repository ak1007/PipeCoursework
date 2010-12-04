package pipe;

import junit.framework.Test;
import junit.framework.TestSuite;
import pipe.common.dataLayer.GuiViewTest;
import pipe.gui.FunctionalScriptsTest;
import pipe.gui.GuiFrameTest;
import pipe.modules.gspn.GSPNAnalysisTest;
import pipe.modules.invariantAnalysis.InvariantAnalysisTest;
import pipe.modules.reachabilityGraphGenerator.ReachabilityGraphGeneratorTest;
import pipe.modules.reachabilityGraphGenerator.ReachabilityGraphTest;
import pipe.io.*;
import pipe.dataLayer.calculations.StateSpaceGeneratorTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for pipe.gui");
		//$JUnit-BEGIN$
		suite.addTestSuite(GSPNAnalysisTest.class);
		suite.addTestSuite(StateRecordTest.class);
		suite.addTestSuite(TransitionRecordTest.class);
		suite.addTestSuite(StateSpaceGeneratorTest.class);
		suite.addTestSuite(ReachabilityGraphGeneratorTest.class);
		suite.addTestSuite(ReachabilityGraphTest.class);
		suite.addTest(FunctionalScriptsTest.suite());
		suite.addTestSuite(GuiViewTest.class);
		suite.addTestSuite(GuiFrameTest.class);
		suite.addTestSuite(InvariantAnalysisTest.class);
		//$JUnit-END$
		return suite;
	}

}

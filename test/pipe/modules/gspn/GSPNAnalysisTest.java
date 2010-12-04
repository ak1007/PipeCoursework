package pipe.modules.gspn;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.text.DecimalFormat;

import junit.framework.*;

import pipe.common.dataLayer.*;
import pipe.dataLayer.calculations.*;
import pipe.gui.widgets.ResultsHTMLPane;

/** 
 * This JUnit TestCase tests several functions used in GSPNAnalysis
 * @author Edwin Chung 16 Feb 2007
 * 
 * 
 *
 */

public class GSPNAnalysisTest extends TestCase {

	private GSPNNew gspn;
	private ArrayList testNets;
	private ArrayList currentMarkings;
	private File [] nets;
	private double [] pi1;
	private double [] pi2;
	private File file1;
	private File file2;
	private DecimalFormat df;
	private StateList sl1; 
	private	StateList sl2;
	
	protected void setUp() throws Exception {
		super.setUp();
		gspn = new GSPNNew();
		nets = new File(new URI(this.getClass().getClassLoader().
				getResource("Example nets").toString())).listFiles();
		currentMarkings = new ArrayList();
		testNets = new ArrayList();
		addNets("gspn1");
		addNets("classicgspn");
		addNets("readers & writers");
		file1 = new File ("file1");
		file2 = new File ("file2");
  		df = new DecimalFormat();
		df.setMaximumFractionDigits(5);
		ResultsHTMLPane resultspane = new ResultsHTMLPane ();
 		StateSpaceGenerator.generate((DataLayer)testNets.get(1), file1, resultspane);
 		pi1 = SteadyStateSolver.solve(file1);
 		sl1 = new StateList(file1, false);
  		StateSpaceGenerator.generate((DataLayer)testNets.get(0), file2, resultspane);
  		pi2 = SteadyStateSolver.solve(file2);
  		sl2 = new StateList(file2, false);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		gspn = null;
		nets = null;
		testNets = null;
		currentMarkings = null;
		pi1 = pi2 = null;
		sl1 = sl2 = null;
		file1.delete();
		file2.delete();
		
	}

	// Start of super class GSPN tests
	
  	public void testGetTransitionsEnabledStatusArrayTest() throws Exception {

		int [] currentMarking = (int [])currentMarkings.get(0);
		DataLayer testNet = (DataLayer)testNets.get(0);
		boolean [] expected = {true,true,false,true,true};
		boolean [] actual = gspn.getTransitionEnabledStatusArray(testNet, currentMarking);
		boolean result = java.util.Arrays.equals(actual, expected);
		
		assertTrue(gspn.getTransitionEnabledStatus(testNet, currentMarking, 0));
		assertTrue(gspn.getTransitionEnabledStatus(testNet, currentMarking, 4));
		assertFalse(gspn.getTransitionEnabledStatus(testNet, currentMarking, 2));
		assertTrue("Error in transitionsEnabledStatusArray", result);
				
	}
  	
  	public void testIsTangibleState() throws Exception {
  		
   		assertFalse(gspn.isTangibleState((DataLayer)testNets.get(0), (int [])currentMarkings.get(0)));
   		assertTrue(gspn.isTangibleState((DataLayer)testNets.get(1), (int [])currentMarkings.get(1)));
   		assertFalse(gspn.isTangibleState((DataLayer)testNets.get(2), (int [])currentMarkings.get(2)));
  		
  	}
  	
  	public void testHasTimedTransitions () throws Exception {
  		
  		assertTrue(gspn.hasTimedTransitions((DataLayer)testNets.get(0)));
  		assertTrue(gspn.hasTimedTransitions((DataLayer)testNets.get(1)));
  		assertFalse(gspn.hasTimedTransitions((DataLayer)testNets.get(2)));
  	}
  	
  	public void testHasImmediateTransitions() throws Exception {
  		
  		assertTrue(gspn.hasImmediateTransitions((DataLayer)testNets.get(0)));
  		assertFalse(gspn.hasImmediateTransitions((DataLayer)testNets.get(1)));
  		assertTrue(gspn.hasImmediateTransitions((DataLayer)testNets.get(2)));
  		
  	}

  	// End of super class GSPN tests
  	
  	// Start of GSPNNew tests
  	
  	public void testSteadyStateSolution () throws Exception {
  		
  		final double [] result1 = {1.0/6, 1.0/6, 1.0/6, 1.0/6, 1.0/6, 1.0/6};
  		final double [] result2 = {0.05316, 0.19934, 0.74751};
  	  		
  		assertTrue("Error in the steady state solution for classicgspn", 
  					equal(result1,pi1));
  		
  		assertTrue("Error in the steady state solution for gspn1", 
					equal(result2,pi2));
  		  		
  	}
  	
  	public void testStateList () throws Exception {
  		
  		int [][] stateList1 = {{2,0,0},{1,1,0},{1,0,1},{0,2,0},{0,1,1},{0,0,2}};
  		int [][] stateList2 = {{0,0,2,0,1},{1,0,1,0,2},{2,0,0,0,3}};
  		  		
  		for (int m = 0; m < sl1.size(); m++) {
  			
  			int [] actualState = sl1.get(m);
  			
  			for(int n = 0; n < actualState.length; n++) {
  				
  				assertTrue ("Error in the calculations of states for classicgspn", 
  						stateList1[m][n] == actualState [n]);
  				
  			}
  		}
  		
  		for (int m = 0; m < sl2.size(); m++) {
  			
  			int [] actualState = sl2.get(m);
  			
  			for(int n = 0; n < actualState.length; n++) {
  				
  				assertTrue ("Error in the calculations of states for gspn1", 
  						stateList2[m][n] == actualState [n]);
  				
  			}
  		}
  	}
  	
  	public void testAverageNoOfTokens () throws Exception {
  		
  		final double [] result1 = {2.0/3, 2.0/3, 2.0/3};
  		final double [] result2 = {1.69435, 0 , 0.30565, 0, 2.69435};
  		
  		double [] actual1 = gspn.averageTokens(pi1, sl1);
  		double [] actual2 = gspn.averageTokens(pi2, sl2);
  		
   		assertTrue("Error in the calculations of average number of tokens for classicgspn", 
  				equal(result1, actual1));
   		
   		assertTrue("Error in the calculations of average number of tokens for gspn1", 
  				equal(result2, actual2));
  		
  	}
  	
  	public void testTransitionsThroughput () throws Exception {
  		
  		final double [] result1 = {0.5, 0.5, 0.5};
  		final double [] result2 = {0.0, 0.0, 0.0, 4.0, 1.26246};
  		
  		double [] actual1 = gspn.getFastTransitionThroughput((DataLayer)testNets.get(1), sl1, pi1);
  		double [] actual2 = gspn.getFastTransitionThroughput((DataLayer)testNets.get(0), sl2, pi2);
  		
  		assertTrue("Error in the calculations of transitionsThroughput for classicgspn", 
  				equal(result1, actual1));
  		assertTrue("Error in the calculations of transitionsThroughput for gspn1", 
  				equal(result2, actual2));
  	  	  		
  	}
  	
  	public void testTokenDistribution () throws Exception {
  		
  		final double [][] result1 = {{0.5, 1.0/3, 1.0/6}, {0.5, 1.0/3, 1.0/6}, {0.5, 1.0/3, 1.0/6}};
  		final double [][] result2 = {{0.05316, 0.19934, 0.74751, 0.0}, {1.0, 0.0, 0.0, 0.0}, {0.74751, 0.19934, 0.05316, 0.0}, {1.0, 0.0, 0.0, 0.0}, {0.0, 0.05316, 0.19934, 0.74751}};
  		
  		double [][] actual1 = gspn.tokenDistribution(pi1, sl1);
  		double [][] actual2 = gspn.tokenDistribution(pi2, sl2);
  		
  		assertTrue("Error in the calculation of token distribution for classicgspn", 
  				equal(result1,actual1));
  		assertTrue("Error in the calculation of token distribution for classicgspn", 
  				equal(result2,actual2));
  		
  	}
  	
  	// End of GSPNNew tests
  	  	
  	// Helper functions created to aid the testing
  	
  	private void addNets (String a) {
  		
  		int i = 0;
  		
  		try {
					
			for (i = 0; i < nets.length && !nets[i].getName().toLowerCase().endsWith(a + ".xml"); i++);
			
			DataLayer testNet = new DataLayer(nets[i]);
			
			testNets.add(testNet);
			currentMarkings.add(testNet.getCurrentMarkingVector());

		}
		catch (Exception e){
			System.out.println("Unable to start test - error opening example files for testing");
		}
  		
  	}
  	
  	private boolean equal (double [] first, double [] second){
  	  	
  		for (int i = 0; i < first.length; i++) {
  			
  			if (!df.format(first [i]).equals(df.format(second[i]))) return false;
  			
  		}
  		
  		return true;
  		
  	}
  	
  	private boolean equal (double[][] first, double[][] second){
  		
  		for (int m = 0; m < first.length; m++) {
  			
  			for (int n = 0; n < first[m].length; n++){
  				
  				if (!df.format(first[m][n]).equals(df.format(second [m][n]))) return false;
  				
  			}
  			
  		}
  		
  		return true;	
  	}
}

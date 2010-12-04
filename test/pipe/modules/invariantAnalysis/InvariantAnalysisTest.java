package pipe.modules.invariantAnalysis;

import junit.framework.*;
import pipe.common.dataLayer.*;

public class InvariantAnalysisTest extends TestCase {
	
	final static int[][] INPUT = 
	   {{-1,1,0,0,0,0},
		{0,-1,1,0,-1,1},
		{0,0,-1,1,0,0},
		{1,0,0,-1,1,-1}};
	
	final static int[][] EXPECTED = 
		{{1,1,0,0},
		 {1,1,0,0},
		 {1,0,1,0},
		 {1,0,1,0},
		 {0,0,1,1},
		 {0,1,0,1}};
	
	private InvariantAnalysis analyser;

	public InvariantAnalysisTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		analyser = new InvariantAnalysis();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		analyser = null;
	}

	public void testFindInvariantVectors() throws Exception{
		boolean matches = true;
		PNMatrix c = new PNMatrix(INPUT);
		PNMatrix expected = new PNMatrix(EXPECTED);
		PNMatrix analysed = analyser.findVectors(c);
		for (int row = 0; row < 6; row++){
			for (int col = 0; col < 4; col++){
				if (analysed.get(row,col) != expected.get(row,col)){
					matches = false;
				}
			}
		}
		assertTrue("error in invariant vectors calculation",matches);
	}
}

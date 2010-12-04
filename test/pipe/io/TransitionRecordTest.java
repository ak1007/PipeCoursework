package pipe.io;

import java.io.RandomAccessFile;
import java.io.File;

import java.util.*;

import junit.framework.TestCase;

/**
 * This JUnit TestCase tests the various read/write functions 
 * in TransitionRecord
 * @author Edwin Chung Mar 2007
 *
 */

public class TransitionRecordTest extends TestCase {

	private RandomAccessFile raFile;
	private File testFile;
		
	protected void setUp() throws Exception {
		super.setUp();
		testFile = new File ("testFile");
		raFile = new RandomAccessFile (testFile,"rw");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		raFile.close();
		testFile.delete();
	}

	public void testReadWrite() throws Exception {
		
		final int [] from = {0,1,2,1};
		final int [] to = {1,2,0,2};
		final double [] rate = {1.1,2.0,2.0,1.0};
		final int [] transitionsNo = {0,0,1,1};
		ArrayList input = new ArrayList();
		TransitionRecord output = new TransitionRecord ();
		
		for (int i = 0; i < from.length; i++) {
			
			TransitionRecord tRecord = new TransitionRecord (from[i],to[i],rate[i],transitionsNo[i]);
			input.add(tRecord);
			tRecord.write(raFile);
			tRecord.write1(raFile);
			
		}
		
		raFile.seek(0);
		
		for (int i = 0; i < from.length; i++) {
			
			boolean result = false;
			output.read(raFile);
			if (output.equals((TransitionRecord)input.get(i)) && 
					output.getRate() == rate[i]) result = true;
			else result = false;
			assertTrue ("Error in reading/writing transitions records", result);
			output.read1(raFile);
			if (output.equals((TransitionRecord)input.get(i)) && 
					output.getTransitionNo() == transitionsNo[i]) result = true;
			else result = false;
			assertTrue ("Error in reading/writing transitions records", result);
		}
		
		
		
		
	}
	
}

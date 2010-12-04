package pipe.dataLayer.calculations;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;

import junit.framework.TestCase;

import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.*;
import pipe.common.dataLayer.*;
import pipe.modules.gspn.*;

/** 
 * This JUnit TestCase tests the two state space generation functions 
 * in StateSpaceGenerator
 * @author Edwin Chung Mar 2007
 * 
 */

public class StateSpaceGeneratorTest extends TestCase {

	private File [] testNetsFiles1;
	private File [] testNetsFiles2;
	private ArrayList testNets;
	private GSPN gspn;
	private int classicgspnNetNo;

	protected void setUp() throws Exception {
		super.setUp();

		ResultsHTMLPane resultspane = new ResultsHTMLPane ();
		testNets = new ArrayList();
		gspn = new GSPN();

		int count = 0;

		try {

			File [] nets = new File(new URI(this.getClass().getClassLoader().
					getResource("Example nets").toString())).listFiles();

			testNetsFiles1 = new File [nets.length];
			testNetsFiles2 = new File [nets.length];

			for (int i = 0; i < nets.length && nets[i].getName().toLowerCase().endsWith(".xml"); i++) {

				DataLayer testNet = new DataLayer(nets[i]);

				if (!nets[i].getName().equalsIgnoreCase("courier protocol.xml") && !nets[i].getName().equalsIgnoreCase("TimelessTrap.xml") && gspn.hasTimedTransitions(testNet)){

					File file1 = new File ("net" + i + "file1");
					File file2 = new File ("net" + i + "file2");

					StateSpaceGenerator.generate(testNet, file1);
					StateSpaceGenerator.generate(testNet, file2, resultspane);
					testNetsFiles1 [count] = file1;
					testNetsFiles2 [count] = file2;
					testNets.add(testNet);
					if (nets[i].getName().equalsIgnoreCase("classicgspn.xml")) classicgspnNetNo = count;
					count++;

				}
			}
		}
		catch (Exception e){
			System.out.println("Unable to start test - error opening example files for testing");
		}

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		for (int i = 0; i < testNetsFiles1.length; i++){

			if (testNetsFiles1[i]!=null) testNetsFiles1[i].delete();
			if (testNetsFiles2[i]!=null) testNetsFiles2[i].delete();

		}
		testNetsFiles1 = testNetsFiles2 = null;
		testNets = null;
		gspn = null;
	}

	public void testStateRecords() throws Exception {

		for (int c = 0 ; c < testNetsFiles1.length && testNetsFiles1[c]!=null; c++) {

			StateList sl1 = new StateList(testNetsFiles1[c], true);
			StateList sl2 = new StateList(testNetsFiles2[c], false);

			if (gspn.hasImmediateTransitions((DataLayer)testNets.get(c))) {

				assertFalse(sl2.size()>sl1.size());
				for (int i = 0; i < sl1.size(); i++){

					for (int j = 0; j < sl1.size(); j++){

						if (j != i)	assertFalse("There are duplicate entries in the state records",
								sl1.compareMarking(sl1.get(i), sl1.get(j)));

					}
				}
			}
			else { 

				assertTrue("The size of the state records is different",
						sl1.size() == sl2.size());

				for (int i = 0; i < sl1.size(); i++){

					assertTrue("The markings of the states in the state records are different",
							sl1.compareMarking(sl1.get(i), sl2.get(i)));

					for (int j = 0; j < sl1.size(); j++){

						if (j != i)	assertFalse("There are duplicate entries in the state records",
								sl1.compareMarking(sl1.get(i), sl1.get(j)));

					}
				}
			}
		}
	}


	public void testTransitionRecords() throws Exception {

		final int [] expectedTransitionsNo = {0,1,0,2,0,1,2,1,2};

		for (int c = 0 ; c < testNetsFiles1.length && testNetsFiles1[c]!= null; c++){

			RGFileHeader header1 = new RGFileHeader();
			RGFileHeader header2 = new RGFileHeader();
			RandomAccessFile rgFile1; 
			RandomAccessFile rgFile2; 
			TransitionRecord record1 = new TransitionRecord ();
			TransitionRecord record2 = new TransitionRecord ();

			if (!gspn.hasImmediateTransitions((DataLayer)testNets.get(c))) {

				try {
					rgFile1 = new RandomAccessFile(testNetsFiles1[c], "r");
					header1.read(rgFile1);
					rgFile2 = new RandomAccessFile(testNetsFiles2[c], "r");
					header2.read(rgFile2);

				} catch (IncorrectFileFormatException e1) {
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}

				assertTrue("The size of transition records are different",
						header1.getNumTransitions()==header2.getNumTransitions());

				rgFile1.seek(header1.getOffsetToTransitions());
				rgFile2.seek(header2.getOffsetToTransitions());

				for (int i = 0; i < header1.getNumTransitions(); i++){

					record1.read1(rgFile1);
					record2.read(rgFile2);

					assertTrue("There is discrepancy between the transition records", 
							record1.equals(record2));
					assertTrue("There is discrepancy between the calculated rates", 
							record1.getRate()== record2.getRate());


					if (c == classicgspnNetNo)

						assertTrue("There is discrepancy between the transition numbers recorded",
								record1.getTransitionNo() == expectedTransitionsNo[i]);

				}
			}	
		}
	}
}

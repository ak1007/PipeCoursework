package pipe.modules.reachabilityGraphGenerator;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;

import att.grappa.Graph;
import att.grappa.Parser;


import junit.framework.*;
import pipe.common.dataLayer.*;
import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.io.RGFileHeader;
import pipe.modules.reachability.ReachabilityGraphGenerator;
/** 
 * This JUnit TestCase tests graph integrity
 * @author Matthew Worthington March 2007
 * 
 *
 */

public class ReachabilityGraphTest extends TestCase{
	private File [] nets;
	private ArrayList testNets;
	private ArrayList currentMarkings;
	final static int[][] expectedResults = new int[][] {{6,9},{19,48},{5,8}};
	protected void setUp() throws Exception {
		super.setUp();
		nets = new File(new URI(this.getClass().getClassLoader().
				getResource("Example nets").toString())).listFiles();
		currentMarkings = new ArrayList();
		testNets = new ArrayList();
		addNets("classicgspn");
		addNets("gspn1");	
		addNets("readers & writers");
	}


	protected void tearDown() throws Exception {
		super.tearDown();
		nets = null;
		testNets = null;
		currentMarkings = null;
	}


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


	public void testDotFile()throws Exception{
		
		//File [] testFiles = new File(new URI(this.getClass().getClassLoader().getResource("Dot Files").toString())).listFiles();

		//int numberofTest = testFiles.length;
		//for (int count=0;numberofTest>count;count++)
		{
			File [] testFiles = new File(new URI(this.getClass().getClassLoader().
					getResource("Dot Files").toString())).listFiles();

			int numberofTest = testFiles.length;
			for (int count=0;numberofTest>count;count++)
			{
			//FileInputStream expected = new FileInputStream(testFiles[count].getAbsolutePath());
			Graph testGraph1;
			Parser dotParser= null;
			
			RGFileHeader header = new RGFileHeader();
			
			File reachabilityFile=new File("test.rg");
			StateSpaceGenerator.generate((DataLayer)testNets.get(count), reachabilityFile);
			
			RandomAccessFile racFile = new RandomAccessFile(reachabilityFile, "r");
			header.read(racFile);

			InputStream result = ReachabilityGraphGenerator.generateDotFile(reachabilityFile);
			dotParser = new Parser(result,System.err);
			dotParser.parse();
			testGraph1 = dotParser.getGraph();
			
			//check both graphs have same number of states
			assertTrue(testGraph1.countOfElements(1)==expectedResults[count][0]);
			//check both graphs have same number of arcs
			assertTrue(testGraph1.countOfElements(2)==expectedResults[count][1]);
			
			}
		}
	}

}
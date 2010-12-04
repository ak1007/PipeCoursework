package pipe.modules.reachabilityGraphGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import junit.framework.*;
import pipe.common.dataLayer.*;
import pipe.dataLayer.calculations.StateSpaceGenerator;
import pipe.modules.reachability.ReachabilityGraphGenerator;
/** 
 * This JUnit TestCase tests dot file integrity
 * @author Matthew Worthington March 2007
 * 
 *
 */
public class ReachabilityGraphGeneratorTest extends TestCase{
	private File [] nets;
	private ArrayList testNets;
	private ArrayList currentMarkings;
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

		//File [] testFiles = new File(new URI(this.getClass().getResource("test.Dot File").toString())).listFiles();
				//getResource("Dot Files").toString())).listFiles();
		File [] testFiles = new File(new URI(this.getClass().getClassLoader().getResource("Dot Files").toString())).listFiles();
		int numberofTest = testFiles.length;
		String filename=null;
		
		for (int count=0;numberofTest>count;count++)
		{
			for (int loop=0; numberofTest>loop; loop++)
				switch (count){
				case 0: if (testFiles[loop].getAbsolutePath().endsWith("ClassicGSPN.dot"))
							filename=testFiles[loop].getAbsolutePath();break;
				case 1: if (testFiles[loop].getAbsolutePath().endsWith("GSPN1.dot"))
					filename=testFiles[loop].getAbsolutePath();break;
				case 2: if (testFiles[loop].getAbsolutePath().endsWith("Readers&Writers.dot"))
					filename=testFiles[loop].getAbsolutePath();break;
				}
				
			FileInputStream expected = new FileInputStream(filename);

			File reachabilityFile=new File("testing.rg");
			StateSpaceGenerator.generate((DataLayer)testNets.get(count), reachabilityFile);
			InputStream result = ReachabilityGraphGenerator.generateDotFile(reachabilityFile);

			int resultInt = result.read();
			int expectedInt=expected.read();

			while (resultInt!=-1 && expectedInt!=-1)
			{
				
				assertTrue(resultInt==expectedInt);
				//check each individual character of the dot file
				resultInt = result.read();
				expectedInt=expected.read();

			}
			
			assertTrue(resultInt==expectedInt);
			//check that both dot files terminate at same point
		}
	}

}
package pipe.modules.dnamacaTagged;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;
import pipe.gui.CreateGui;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.GraphPanelPane;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.Module;
import pipe.modules.clientCommon.CommonMethods;

public class DnamacaTagged implements Module{

	private static final String MODULE_NAME = "DNAmaca for tagged net";
	  private Place places[];
	  private Transition transitions[];
	  private DataLayer pnmldata;
	  private String modString = "";
	  private String tagPlace = "tagged_location";
	  private int taggedPlaceIndex = -1;
	  final int UNTAGGED = 0;
	  final int ORIGINAL = 1;
	  final int CLONED = 2;
	  private boolean tagged = false;
	  
	  private String[] performanceDesc_original;
	  private String[] performanceDesc_clone;
	  private boolean doTokenDistributionForTagged = false;
	  

	  private JDialog guiDialog;
	  private ResultsHTMLPane resultText;
	  
	  
	  public String getName() {
			return MODULE_NAME;
		}
	  
	  
	  public void run(DataLayer _pnmldata){
		  
		  
		  pnmldata = _pnmldata;
		  places = pnmldata.getPlaces();
		  transitions = pnmldata.getTransitions();
		  performanceDesc_original = new String[transitions.length];
		  performanceDesc_clone = new String[transitions.length];
		  
		  
		  System.out.println("\n running dnamaca tagged");
		  
		  
		  //		 Build interface
			guiDialog = new JDialog(CreateGui.getApp(),MODULE_NAME,true);
			
			Container contentPane=guiDialog.getContentPane();
			contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));

			resultText = new ResultsHTMLPane(pnmldata.getURI());
			contentPane.add(resultText);
			
			String buttonLabels[] = {"Run DNAmaca", "Validate"};
			ActionListener buttonHandlers[] = {runDnamacaAction, validateAction};
			ButtonBar buttons;
			contentPane.add(buttons = new ButtonBar(buttonLabels, buttonHandlers));
			

		    guiDialog.pack();
			guiDialog.setLocationRelativeTo(null);    
			guiDialog.setVisible(true);
		  
	  }
	  
	  
	  ActionListener runDnamacaAction=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				
				if( !pnmldata.hasValidatedStructure() )
		    		resultText.setText("The structure contains tagged elements and needs to be validated first" );
				
				
				else
				{
					try{
					  	//modFile = File.createTempFile("dnamaca", ".mod");
					   
						//FileWriter out1 = new FileWriter(modFile);
					  
					    //FileWriter out1 = new FileWriter("/homes/wl107/Desktop/dnamaca/dnamaca/bin/current.mod");
						FileWriter out1 = new FileWriter("current.mod");
					    //FileWriter fstream = new FileWriter("/homes/wl107/Desktop/current.txt");
						FileWriter fstream = new FileWriter("current.txt");
						//FileWriter fstream = new FileWriter("C:/current.txt");
					    BufferedWriter out = new BufferedWriter(fstream);
					    generateMod();
					    out.write(modString);
					    out.close();
					    out1.write(modString);
					    out1.close();
					    
					  }
				  catch (Exception e)
				  {
					    System.err.println("Error: " + e.getMessage());
				  }
		  
					
				  boolean outputPerformanceResult = false;
				  String outputResult = "<br><h2>performance results</h2><br/> <p>";
				  String result = "\n";
				  try
				    {
				      Runtime rt = Runtime.getRuntime();
				      
				      Process proc = rt.exec(new String[]{"dnamaca", "current.mod"});
				      
				      InputStream stdin = proc.getInputStream();
				     InputStreamReader isr = new InputStreamReader(stdin);
				      
				      InputStream stderr = proc.getErrorStream();
				      InputStreamReader isr2 = new InputStreamReader(stderr);

				      BufferedReader br = new BufferedReader(isr); 
				      BufferedReader br2 = new BufferedReader(isr2);
				      
				      String line = null;
				      
				      
				      
				      while ( (line = br.readLine()) != null)
				      {
				        System.out.println(line);
				        result+=line + "\n";
				      }
				    
				      int exitVal = proc.waitFor();  
				     
				      System.out.println("Process exitValue: " + exitVal); 
				    } catch (Throwable t) { 
				      t.printStackTrace(); 
				    }    
					 
				    String pe = "\n";
					Pattern p = Pattern.compile(pe);
					String[] lines = p.split(result);
					

				    	for(int i=0; i<lines.length;i++)
						{
							if(lines[i].indexOf("(begin performance results)")>=0)
								outputPerformanceResult = true;
							
							if(outputPerformanceResult)outputResult+= "<br>" + lines[i] + "<br/>";
						}
				    	
				    	outputResult+= "</p>";
				    if(outputPerformanceResult)	{
				    	resultText.setText(outputResult);
				    }
				    else resultText.setText("Error outputing performance result");
	
					
				}
		
			}
			
	  };
	  
	  
	  ActionListener validateAction=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				boolean result = pnmldata.validTagStructure();
			}
			
	  };
	  
	  
	  private void generateMod() {
			modString = "";
			model();
			method();
			performance();
			
		}
		
		private void model() {
			modString += "\\model{\n";
			stateVector();
			initial();
			transitions();
			
			modString += "}\n\n";		
		}
	  
		private void performance() {
			modString += "\\performance{\n";		
			transitionMeasures();
			tokenDistribution();
			modString += "}\n";		
			
		}
		
		private void tokenDistribution() {
			for(int i=0; i<places.length; i++) {
				
				modString += "\t\\statemeasure{Mean tokens on place " + places[i].getId() + "}{\n";
				modString += "\t\t\\estimator{mean variance distribution}\n";
				modString += "\t\t\\expression{" + places[i].getId() + "}\n";
				modString += "\t}\n";
				
				//if the net contain any tagged element then calculate
				//probability of each place having tagged token
				if(doTokenDistributionForTagged)
				{
					modString += "\t\\statemeasure{Probability of having tagged token on place " + places[i].getId() + "}{\n";
					modString += "\t\t\\estimator{mean}\n";
					modString += "\t\t\\expression{" + places[i].getId() + " > 0" +
							" && tagged_location == "+ CommonMethods.getPlaceIndex(places[i].getId(), places) +"}\n"; 
					modString += "\t}\n";		
				}
				
			}
		}

		
		private void transitionMeasures() {
			for(int i=0; i<transitions.length; i++) {
				
				Iterator arcsTo = transitions[i]. getConnectToIterator();
				boolean taggedArc = false;
				
			  	while(arcsTo.hasNext())			  		
			  		if(  ((Arc)arcsTo.next()).isTagged() )
			  		{
			  			taggedArc = true;
			  			doTokenDistributionForTagged = true;
			  		}
			  	
			  	
			  	if(taggedArc)
			  	{
			  		transitionMeasuresDesc(i,ORIGINAL);
			  		transitionMeasuresDesc(i,CLONED);
			  	}
			  	else
			  		transitionMeasuresDesc(i,UNTAGGED);
		
				
			}
		}
		
		private void transitionMeasuresDesc(int i, int type){
			
			String id = transitions[i].getId();
			if(type==CLONED) id += "_tagged";
			
			modString += "\t\\statemeasure{Enabled probability for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\expression{(";
			
			if(type==UNTAGGED) 
				modString += CommonMethods.getTransitionConditions(i, transitions);
				
			else if(type==ORIGINAL) 
				modString += performanceDesc_original[i];
				
			else if(type==CLONED)
				modString += performanceDesc_clone[i];
				
			modString += ") ? 1 : 0}\n";
			modString += "\t}\n";
			
			modString += "\t\\countmeasure{Throughput for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\precondition{1}\n";
			modString += "\t\t\\postcondition{1}\n";
			modString += "\t\t\\transition{" + id + "}\n";
			modString += "\t}\n";
			
		}
		
		
		private void method()
		{
			modString += "\\solution{\n\t\\method{sor}\n\n}";
		}
		
		private void stateVector() 
		{	
			
			modString += "\t\\statevector{\n";
			modString += "\t\t\\type{short}{";
			
			modString += places[0].getId();
			if(places[0].isTagged())
			{
				taggedPlaceIndex = 0;
				tagged = true;
			}
			
			for(int i=1; i<places.length; i++) {
				modString += ", "+places[i].getId();
				if(places[i].isTagged())
				{
					taggedPlaceIndex = i;
					tagged = true;
				}
				
			}

			if(tagged)modString += ", " + tagPlace;		
				
			modString += "}\n";
			modString += "\t}\n\n";
		}
	  
		private void initial() {
			modString += "\t\\initial{\n";			
			modString += "\t\t";
			for(int i=0; i<places.length; i++) {
				modString += places[i].getId()+" = " + places[i].getCurrentMarking()+"; ";
			}
			if(tagged) modString += tagPlace + " = " + taggedPlaceIndex + ";";
			modString += "\n\t}\n";					
		}
		
		private void transitions()
		{
			
			for(int i=0; i<transitions.length; i++)
			{
			  	boolean taggedArc = false;
				int numInputArc=0;
			  	Iterator arcsTo = transitions[i]. getConnectToIterator();
			  	Iterator arcsFrom = transitions[i]. getConnectFromIterator();
			  	/*since the net has been validated, if the transition has a tagged input arc
			  	 * it must have corresponding tagged output, hence need to check for tagged input
			  	 */ 
			  	while(arcsTo.hasNext()){
			  		
			  		if(  ((Arc)arcsTo.next()).isTagged() )
			  		{
			  			taggedArc = true;
			  			numInputArc++;
			  		}
			  		
			  	}
			  	
			  	if(taggedArc) 
			  	{
			  		CommonMethods.writeTransition(ORIGINAL, i, numInputArc, transitions, places, modString,
			  				pnmldata, performanceDesc_original, performanceDesc_clone, false);
			  		CommonMethods.writeTransition(CLONED, i, numInputArc, transitions, places, modString,
			  				pnmldata, performanceDesc_original, performanceDesc_clone, false);
			  	}
			  	else 
			  	{
			  		CommonMethods.writeTransition(UNTAGGED, i, numInputArc, transitions, places, modString,
			  				pnmldata, performanceDesc_original, performanceDesc_clone, false);
			  	}
			  		
			}//end for
		}//end transition
		
}

package pipe.modules.passageTimeForTaggedNet;


import java.io.BufferedWriter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.StateGroup;
import pipe.common.dataLayer.Transition;
import pipe.modules.Common;




public class TransModel {

	
	 
	  private Place places[];
	  private Transition transitions[];
	  private DataLayer pnmldata;
	  private String modString = "";
	  private String perfString = "";
	  private String tagPlace = "tagged_location";
	  private int taggedPlaceIndex = -1;
	  final int UNTAGGED = 0;
	  final int ORIGINAL = 1;
	  final int CLONED = 2;
	  
	  private ArrayList<StateGroup> sourceStateGrps, destStateGrps;
	  private AnalysisSetting timePoints;

	  public TransModel(DataLayer pnml, ArrayList<StateGroup> sourceState, ArrayList<StateGroup> destinationState, AnalysisSetting timeSettings){
		  
		  pnmldata = pnml;
		  places = pnmldata.getPlaces();
		  transitions = pnmldata.getTransitions();
		  sourceStateGrps=sourceState;
		  destStateGrps =destinationState;
		  timePoints =  timeSettings;
		  produceModel();
		  
	  }
	  
	  
	  public void produceModel(){
		  

		  System.out.println("\n Translate model");
		  
		  try{

			    FileWriter out1 = new FileWriter("current.mod");
			    
				
			    
//			    FileWriter fstream = new FileWriter("/homes/wl107/Desktop/current.txt");
			    
			    FileWriter fstream = new FileWriter("current.txt");
		
			    
			  	//FileWriter out1 = new FileWriter("c:\\current.mod");
			    //FileWriter fstream = new FileWriter("c:\\current.txt");
			    BufferedWriter out = new BufferedWriter(fstream);
			    
				
			    
			    generateMod();
			    
				  System.out.println("\n Generated model");		    
			    
			    out.write(modString);
			    out.write(perfString);
			    out.close();
			    out1.write(modString);
			    out1.write(perfString);
			    out1.close();
			    
			  }
		  catch (Exception e)
		  {
			    System.err.println("Error: " + e.getMessage());
		  }
 
			
	  }
	  
	  private void generateMod() {
			modString = "";
			model();
			method();
			//performance();
			passageTime();
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
			tokenDistribution();
			transitionMeasures();
			modString += "}\n";		
			
		}
		
		private void tokenDistribution() {
			for(int i=0; i<places.length; i++) {
				modString += "\t\\statemeasure{Mean tokens on place " + places[i].getId() + "}{\n";
				modString += "\t\t\\estimator{mean variance distribution}\n";
				modString += "\t\t\\expression{" + places[i].getId() + "}\n";
				modString += "\t}\n";
			}
		}
		
		private void transitionMeasures() {
			for(int i=0; i<transitions.length; i++) {
				
				Iterator arcsTo = transitions[i]. getConnectToIterator();
				boolean taggedArc = false;
				
			  	while(arcsTo.hasNext())			  		
			  		if(  ((Arc)arcsTo.next()).isTagged() )
			  			taggedArc = true;
			  	
			  	transitionMeasuresDesc(i,false);
			  	if(taggedArc)transitionMeasuresDesc(i,true);
		
				
			}
		}
		
		private void transitionMeasuresDesc(int i, boolean clone){
			
			String id = transitions[i].getId();
			if(clone) id += "_tagged";
			
			modString += "\t\\statemeasure{Enabled probability for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\expression{(" + Common.getTransitionConditions(i,transitions) + ") ? 1 : 0}\n";
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
				taggedPlaceIndex = 0;
			
			for(int i=1; i<places.length; i++) {
				modString += ", "+places[i].getId();
				if(places[i].isTagged())
					taggedPlaceIndex = i;
				
			}

			modString += ", " + tagPlace;		
				
			modString += "}\n";
			modString += "\t}\n\n";
		}
	  
		private void initial() {
			modString += "\t\\initial{\n";			
			modString += "\t\t";
			for(int i=0; i<places.length; i++) {
				modString += places[i].getId()+" = " + places[i].getCurrentMarking()+"; ";
			}
			modString += tagPlace + " = " + taggedPlaceIndex + ";";
			modString += "\n\t}\n";					
		}
		
		private void transitions()
		{
			
			for(int i=0; i<transitions.length; i++)
			{
			  	boolean taggedArc = false;
				int numInputArc=0;
			  	Iterator arcsTo = transitions[i]. getConnectToIterator();
			  	
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
			  		Common.writeTransition(ORIGINAL, i, numInputArc, transitions, places, modString, pnmldata,
			  				null, null, true);
			  		Common.writeTransition(CLONED, i, numInputArc, transitions, places, modString, pnmldata,
			  				null, null, true);
			  	}
			  	else 
			  	{
			  		Common.writeTransition(UNTAGGED, i, numInputArc, transitions, places, modString, pnmldata,
			  				null, null, true);
			  	}
			  		
			}//end for
		}//end transition
		
		
		private void passageTime()
		{
			
			perfString += "\n\\passage{\n";
			
			if(sourceStateGrps != null && destStateGrps != null && timePoints != null) {
				// Add source conditions
				perfString += "\t\\sourcecondition{";
				stateGroups(sourceStateGrps);
				perfString += "}\n";
			
				//  Add target conditions
				perfString += "\t\\targetcondition{";
				stateGroups(destStateGrps);
				perfString += "}\n";
			
				// Add time parameters
				perfString += "\t\\t_start{" + timePoints.startTime + "}\n";
				perfString += "\t\\t_stop{" + timePoints.endTime + "}\n";
				perfString += "\t\\t_step{" + timePoints.timeStep + "}\n";
				
				perfString += "}\n";
			}

		}
		
		private void stateGroups(ArrayList<StateGroup> stateGroups)
		{
			String[] currentCondition;
			int groupCount =0;
			int tag_count =0;
			for(StateGroup curStateGroup : stateGroups)
			{
				boolean first_con = false;
				boolean and_con = false;
				currentCondition = curStateGroup.getConditions();
				String[] tag = new String[currentCondition.length];
				tag_count=0;
				// for any group after the first add the OR 
				if (groupCount > 0)
					perfString += " || ";
				
				
				perfString += "(";
				if(currentCondition[0].indexOf("tagged_location")>=0)
				tag[tag_count++]=currentCondition[0];
				else {
					perfString += currentCondition[0];
					first_con = true;
				}
				
				for(int i=1; i< currentCondition.length;i++)
					if(currentCondition[i].indexOf("tagged_location")>=0)
						tag[tag_count++]=currentCondition[i];
					else {
						if(first_con){
							perfString += " && ";
							
						}
						
						first_con = true;
						
						perfString += currentCondition[i];
					}
						
				for(int i=0; i<tag_count; i++){
					
					if(i==0){
						if(first_con)perfString+= " &&";
						perfString+=" (";
					}
					
					perfString+=tag[i];
					
					if(i<tag_count-1)perfString+="||";
					if(i==tag_count-1) perfString+=")";
					
				}
					
				
				perfString += ")";
				
				groupCount++;
			}
		}
		
	
}

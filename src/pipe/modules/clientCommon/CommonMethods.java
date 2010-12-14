package pipe.modules.clientCommon;

import java.util.Iterator;

import pipe.common.dataLayer.Arc;
import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;

public class CommonMethods {

	
	public static void writeTransition(int type, int i, int numInputArc, Transition[] transitions, 
			Place[] places, String modString, DataLayer pnmldata, String[] performanceDesc_original, 
			String[] performanceDesc_clone, final boolean transmodel) 
	{
		
		Iterator arcToTransitions = transitions[i]. getConnectToIterator();
	  	Iterator arcFromTransitions = transitions[i]. getConnectFromIterator();
		
		final int UNTAGGED = 0;
		final int ORIGINAL = 1;
		final int CLONED = 2;
	  	
		int[] tagInputPlaceIndex = new int[numInputArc];
		int taggedInput=0;
		int tagOutputPlaceIndex = -1;

	  	
	  	/* if type is CLONED or ORIGINAL, 
	  	 * need to find tagged input places and tagged output place
	  	 */
	  	if(type != UNTAGGED) 
	  	{
	  		
	  		while(arcToTransitions.hasNext())
	  		{
		  		
	  			/*
	  			 * if the arc is tagged, 
	  			 * there's possibility that the place attach to it
	  			 * may contain a tagged token
	  			 */
	  			final Arc arc = ((Arc)arcToTransitions.next());
		  		if( arc.isTagged()  )
		  		{
		  			tagInputPlaceIndex[taggedInput] = getPlaceIndex(((Place)arc.getSource()).getId(),places);
		  			taggedInput++;
		  		}
		  		
		  	}
	  		
	  		while(arcFromTransitions.hasNext())
	  		{
	  			/*
	  			 * obtain tagged outputPlace index
	  			 */
	  			final Arc arc = ((Arc)arcFromTransitions.next());
		  		if( arc.isTagged()  )
		  		{
		  			tagOutputPlaceIndex = getPlaceIndex(((Place)arc.getTarget()).getId(),places);
		  			break;
		  		}
	  			
	  		}
	  		
	  	}//if !untagged

	  		/*
	  		 * 1) write transition id
	  		 */
	  		if(type == ORIGINAL || type == UNTAGGED) 
				modString += "\t\\transition{"+ transitions[i].getId() +"}{\n";
			
			else if(type == CLONED)	
				modString += "\t\\transition{"+ transitions[i].getId() +"_tagged}{\n";
			
	  		
	  		/*
	  		 * 2) write enabling condition 
	  		 */
	  		if(type == UNTAGGED)
				modString += "\t\t\\condition{" + getTransitionConditions(i,transitions) + "}\n";
			
	  		/*
	  		 * this is transition in mode ut'
	  		 * fire when the input places doesn't contain tagged token
	  		 * if it contains tagged token, then it is enable if the marking on this
	  		 * place is greater than 1
	  		 */
			else if(type == ORIGINAL) 
			{
				
			  	modString += "\t\t\\condition{(" + getTransitionConditions(i,transitions) + 
		  		" && tagged_location != " + tagInputPlaceIndex[0];
			  	
			  	if (!transmodel){
			  		performanceDesc_original[i] = getTransitionConditions(i,transitions) + 
			  			" && tagged_location != " + tagInputPlaceIndex[0];
			  	}
			  	
			  	for(int x=1;x<taggedInput;x++)
			  	{
			  		modString += " && tagged_location!=" + tagInputPlaceIndex[x];
			  		if (!transmodel)
			  			performanceDesc_original[i] += " && tagged_location!=" + tagInputPlaceIndex[x];
			  	}
			  	
			  	modString+= ") || (" + getTaggedTransitionConditions(i, tagInputPlaceIndex,transitions, places)+" )}\n";
			  	if (!transmodel)
			  		performanceDesc_original[i] += " ) || (  " + getTaggedTransitionConditions(i, tagInputPlaceIndex,transitions, places);
			}
	  		/*
	  		 * transition in mode t' can fire when there's 
	  		 * correct marking and tagged_location must be
	  		 * one of the input places
	  		 */
			else if(type == CLONED) 
			{
			  	modString += "\t\t\\condition{(" + getTransitionConditions(i,transitions) + 
			  		") && (tagged_location==" + tagInputPlaceIndex[0];
			  	
			  	if (!transmodel)
			  		performanceDesc_clone[i] = getTransitionConditions(i,transitions) + ") && (tagged_location==" + tagInputPlaceIndex[0];
			  	
			  	
			  	for(int x=1;x<taggedInput;x++)
			  	{
			  		modString += " || tagged_location==" + tagInputPlaceIndex[x];
			  		
			  		if (!transmodel)
			  			performanceDesc_clone[i] += " || tagged_location==" + tagInputPlaceIndex[x];
			  	}

			  	modString += ")}\n";
			}
	  		
	  		
	  		/*
	  		 * 3) write action
	  		 */
	  		modString += "\t\t\\action{\n";
	  		arcToTransitions = transitions[i]. getConnectToIterator();
		  	arcFromTransitions = transitions[i]. getConnectFromIterator();
		  	
		  	int[][] incidenceMatrix = pnmldata.getIncidenceMatrix();
		  	
		  	while (arcToTransitions.hasNext()) 
		  	{
		  		final Arc arc = ((Arc)arcToTransitions.next());
		  		String currentId = arc.getSource().getId();
		  		int placeNo = getPlaceIndex(currentId,places);
		  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]<0  )
		  		{
		  			modString += "\t\t\tnext->"+currentId;
		  			modString += " = "+currentId+" - " + arc.getWeight() +  ";\n";
		  		}
		  	}
		  	while (arcFromTransitions.hasNext()) 
		  	{
		  		final Arc arc = ((Arc)arcFromTransitions.next());
		  		String currentId = arc.getTarget().getId();
		  		int placeNo = getPlaceIndex(currentId,places);
		  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]>0  )
		  		{
		  			modString += "\t\t\tnext->"+currentId;
		  			modString += " = "+currentId+" + " + arc.getWeight() +  ";\n";
		  		}
		  	}
		  	
		  	if(type==CLONED)
				modString += "\t\t\tnext->tagged_location=" + tagOutputPlaceIndex + ";\n"; 
			
			
			modString += "\t\t}\n";
	  		
			/*
			 * 4) rate and weight
			 */
			if(type == UNTAGGED)
			{
				if (transitions[i].isTimed()) 
			  		modString += "\t\t\\rate{" + transitions[i].getRate();  	
				else 
			  		modString += "\t\t\\weight{" + transitions[i].getRate();
				
				if(transitions[i].isInfiniteServer())
				{
					String tagged_place = "(";
					
					arcToTransitions = transitions[i]. getConnectToIterator();
					if(arcToTransitions.hasNext()) 
				  	{
				  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
				  	}
					while(arcToTransitions.hasNext()) 
				  	{
				  		final Arc arc = ((Arc)arcToTransitions.next());
				  		String currentId = arc.getSource().getId();
				  		tagged_place += "+" + currentId;
				  	}
						
					tagged_place += ")";
					
					modString += "*" + tagged_place + "}\n";	  	
				}
				else
				{
					modString += "}\n";
				}
				
			  	
			}
			else if(type == ORIGINAL)
			{
				double rate = transitions[i].getRate();
				
				if (transitions[i].isTimed())			  	
			  		modString += "\t\t\\rate{";
			  	
			  	else // not timed transition
			  		modString += "\t\t\\weight{";
				
					
				if(transitions[i].isInfiniteServer())
				{
					
					String tagged_place = "(";
					
					arcToTransitions = transitions[i]. getConnectToIterator();
					if(arcToTransitions.hasNext()) 
				  	{
				  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
				  	}
					while(arcToTransitions.hasNext()) 
				  	{
				  		final Arc arc = ((Arc)arcToTransitions.next());
				  		String currentId = arc.getSource().getId();
				  		tagged_place += "+" + currentId;
				  	}
						
					tagged_place += ")";
			  	
						
						modString += " tagged_location== " + tagInputPlaceIndex[0] + 
			  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i, transitions ) 
			  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + ") * " + tagged_place + " : ";
			  		
						int index=1;
			  		
						while(index<taggedInput)
						{
							modString += " tagged_location== " + tagInputPlaceIndex[index] + 
							"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i, transitions ) 
							+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + ") * " + tagged_place + " : ";
			  		
							index++;
						}
			  		
			  		
						modString += rate + "*" + tagged_place +"}\n";

				}
					
				else //not infiniteServer
				{
						modString += " tagged_location== " + tagInputPlaceIndex[0] + 
			  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i, transitions ) 
			  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + ") : ";
			  		
						int index=1;
			  		
						while(index<taggedInput)
						{
							modString += " tagged_location== " + tagInputPlaceIndex[index] + 
							"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i, transitions ) 
							+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + ") : ";
			  		
							index++;
						}
						modString += rate +"}\n";	
				}

			}// end else if original
			else if(type == CLONED)
			{
				double rate = transitions[i].getRate();
				
			  	if (transitions[i].isTimed())			  	
			  		modString += "\t\t\\rate{";
			  	
			  	else // not timed transition
			  		modString += "\t\t\\weight{";
			  	
			  	if(transitions[i].isInfiniteServer())
			  	{
			  		
			  		String tagged_place = "(";
					
					arcToTransitions = transitions[i]. getConnectToIterator();
					if(arcToTransitions.hasNext()) 
				  	{
				  		tagged_place += ((Arc)arcToTransitions.next()).getSource().getId();;
				  	}
					while(arcToTransitions.hasNext()) 
				  	{
				  		final Arc arc = ((Arc)arcToTransitions.next());
				  		String currentId = arc.getSource().getId();
				  		tagged_place += "+" + currentId;
				  	}
						
					tagged_place += ")";
						
			  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
			  			"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i, transitions ) 
			  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + "*" + tagged_place + " : ";
			  		
			  			int index=1;
			  		
			  			while(index<taggedInput)
			  			{
			  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
			  				"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i, transitions ) 
			  				+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + "*" + tagged_place + " : ";
			  		
			  				index++;
			  			}
			  		
			  		
			  			modString += rate + "*" + tagged_place +"}\n";
			  			
			  	}
			  		
			  	else // not InfiniteServer
			  	{
			  			
			  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
			  			"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[0]].getId(), i, transitions ) 
			  			+ "/(double)" + places[tagInputPlaceIndex[0]].getId() +") * " + rate + " : ";
			  		
			  			int index=1;
			  		
			  			while(index<taggedInput)
			  			{
			  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
			  				"? ((double)"+ getArcWeightFromPlace(places[tagInputPlaceIndex[index]].getId(), i, transitions ) 
			  				+ "/(double)" + places[tagInputPlaceIndex[index]].getId() +") * " + rate + " : ";
			  		
			  				index++;
			  			}
			  		
			  			modString += rate +"}\n";
	
			  	}

			}//end else if cloned
			  	
			modString += "\t}\n";
			
		
	}//end writeTransition
	
	public static int getPlaceIndex(String placeName, Place[] places){		
		int index = -1;
		for(int i=0; i<places.length; i++) {		
			if(places[i].getId()==placeName)
			{
				index = i;
				break;
			}
		}
//		System.out.println("Returning " + index);
		
		return index;
	}
	
	public static String getTransitionConditions(int transitionNum, Transition[] transitions) {
		
		String condition = new String();
	  	Iterator arcsTo = transitions[transitionNum]. getConnectToIterator();
	  	if (arcsTo.hasNext()){
	  		final Arc arc = (Arc)arcsTo.next();
	  		condition += arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
	  	}
	  		
	  	while (arcsTo.hasNext())
	  	{
	  		final Arc arc = (Arc)arcsTo.next();
	  		condition += " && "+arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
	  	}
	  	return condition;
		
	}
	
	public static int getArcWeightFromPlace (String placeId, int i, Transition[] transitions)
	{
		Iterator arcsTo = transitions[i]. getConnectToIterator();
		
		//System.out.println("\n in get arc weight " + placeId);
		while(arcsTo.hasNext())
		{
			final Arc arc = (Arc)arcsTo.next();
			//System.out.println("\n"+arc.getSource().getId());
			if( arc.getSource().getId() == placeId ){
				//System.out.println("\nfound match");
				return arc.getWeight();
			}
		}
		
		return -1;
	}
	
	/*
	 * for each of the place connected to input tagged arc,
	 * check if it equals to tagged_location, if yes then, it must
	 * have marking one more greater than the backward incidence function
	 * otherwise, everything is normal
	 */
	private static String getTaggedTransitionConditions(int transitionNum, int[] tagInputPlace, Transition[] transitions, Place[] places) {
		
		String condition = new String();
	  	Iterator arcsTo = transitions[transitionNum]. getConnectToIterator();
	  	if (arcsTo.hasNext()){
	  		final Arc arc = (Arc)arcsTo.next();
	  		if(arc.isTagged())
	  		{
	  			condition += "((tagged_location== "+getPlaceIndex(arc.getSource().getId(),places)
	  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1+1)
	  				+ ") || ( tagged_location!="+getPlaceIndex(arc.getSource().getId(),places)
	  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1)
	  				+ "))";
	  		}
	  		else condition += arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
	  	}
	  		
	  	while (arcsTo.hasNext())
	  	{
	  		
	  		final Arc arc = (Arc)arcsTo.next();
	  		if(arc.isTagged())
	  		{
	  			condition += " && ((tagged_location== "+getPlaceIndex(arc.getSource().getId(),places)
	  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1+1)
	  				+ ") || ( tagged_location!="+getPlaceIndex(arc.getSource().getId(),places)
	  				+ " && " + arc.getSource().getId()+" > "+ (arc.getWeight()-1)
	  				+ "))";
	  		}
	  		else condition += " && "+arc.getSource().getId()+" > "+ (arc.getWeight() - 1);
  		
	  	}
	  	
	  	return condition;

	}
	
}

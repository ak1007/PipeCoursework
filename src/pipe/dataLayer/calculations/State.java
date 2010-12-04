/*
 * Created on 01-Jul-2005
 */
package pipe.dataLayer.calculations;

/**
 * @author Nadeem
 *
 * This class is used to hold basic details of a state which in the case
 * of a Petri Net is it's marking.
 * 
 */
public class State {
	int[] state;
	
	public State(int[] new_state){
		setState(new_state);
	}
	
	public State(State new_state){
		setState(new_state.getState());
	}
	
	public void setState(int[] new_state){
		state = new int[new_state.length];
		System.arraycopy(new_state, 0, state, 0, new_state.length);
	}
	
	public int[] getState(){
		return state;
	}
	
	/**
	 * equals()
	 * Overloads the Object.equals() method. Tests the supplied
	 * state object parameter to see if it represents exactly the
	 * same state as this object. Returns true if and only if the
	 * arrays containing the states are the same length and contain
	 * the same values at the same element indices.
	 * 
	 * @param test		The state object to be compared to this one
	 * @return
	 */
	public boolean equals(State test){
		int[] teststate = test.getState();
		
		if(teststate.length != state.length)
			return false;
		
		for(int index = 0; index < state.length; index++){
			if(state[index] != teststate[index])
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * hashCode()
	 * This overrides the Object.hashCode() method.
	 */
	public int hashCode(){
		int total = 0;
		
		for(int offset = 0; offset < state.length; offset++){
			total = (2*total);
			for(int index = 0; index < (state.length - offset); index++){
				total += state[index];
			}
		}
		// As the above for loop results in massive integers
		// check we haven't overflowed and gone negative.
		// If we have then wrap round to zero.
		if(total < 0)
			total = Integer.MAX_VALUE+total;
		
		return total;
	}
	
	
	/**
	 * hashCode2()
	 * This is an extra hashing function used for collision
	 * resolution. If both the original hashcode and this
	 * hashcode match that of another state object, then
	 * they are very probably the same state.
	 * @return
	 */
	public int hashCode2(){
		int total = 0;
		
		for(int offset = 0; offset < state.length; offset++){
			total = 2*total;
			for(int index = offset; index < state.length; index++){
				total += state[index];
			}
		}
		return total;
	}
	
	public String toString(){
		String output = new String();
		int length = state.length;
		
		output += state[0] + ", ";
		for(int i = 1; i < length-1; i++){
			output += state[i] + ", ";
		}
		output += state[length-1];
		
		return output;
	}
}

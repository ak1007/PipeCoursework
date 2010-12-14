package pipe.modules;

import pipe.common.dataLayer.Place;
import pipe.common.dataLayer.Transition;

public class GetTaggedTransitionConditionsParameter {
	public int transitionNum;
	public int[] tagInputPlaces;
	public Place[] places;
	public Transition[] transitions;

	public GetTaggedTransitionConditionsParameter(int transitionNum,
			int[] tagInputPlaces, Place[] places, Transition[] transitions) {
		this.transitionNum = transitionNum;
		this.tagInputPlaces = tagInputPlaces;
		this.places = places;
		this.transitions = transitions;
	}
}
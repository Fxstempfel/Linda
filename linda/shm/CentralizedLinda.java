package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import java.util.*;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	List<Tuple> tupleSpace;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
    }

	public void write(Tuple t) {
		tupleSpace.add(t);
	}

	// First version : it is assumed that the template matches a tuple in the tupleSpace
	// Need to block if there is no matching tuple
	public Tuple take(Tuple template) {
		int i = findNext(0, template);
		if (i != -1) {
			Tuple t = tupleSpace.remove(i);
		} 
		return t;
	}

	public Tuple read(Tuple template) {
		Tuple result;
		int index = findNext(0, template);
		if (index == -1){
			//sleep
		} else {
			result = tupleSpace.get(index);
		}
	}

	public Tuple tryTake(Tuple template) {
		int i = findNext(0, template);
		if (i != -1) {
			Tuple t = tupleSpace.remove(i);
		} else {
			Tuple t = null;
		}
		return t;
	}

	public Tuple tryRead(Tuple template) {

	}

	public List<Tuple> takeAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		
		int i = 0;
		while (i < tupleSpace.size()) {
			i = findNext(i); // i is the index of template's next occurence 
			// If there is no more matching tuples in the tuplespace,
			// quit the loop
			if (i == -1) {
				break;
			}
			// Else, pop the matching tuple and add it to the list
			else {
				listMatches.add(tupleSpace.remove(index));
			}
			i++;
		}

		return listMatches;
	}

	public Collection<Tuple> readAll(Tuple template) {
		
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {	

	}

	/**
	 * returns the index of the next tuple that matches the template in the tuplespace.
	 * @param startIndex: the index wher to start search (inclusive)
	 * @param template: the template to match
	 * @return the index of the next tuple, or -1 if the search reach the end
	 */
	private int findNext(int startIndex, Tuple template){
		int i=startIndex;
		int result;
		boolean notfound = true;
		while (notfound && ( i < this.tupleSpace.size())){
			if (Tuple.matches(tupleSpace(i), template)){
				notfound = false;
			}
			i++;
		}
		if (notfound) {
			result = -1;
		} else {
			result = i-1;
		}

		return result;
	}

	public void debug(String prefix) {

	}

}

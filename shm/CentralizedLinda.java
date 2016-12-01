package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import java.util.*;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	Collection<Tuple> tupleSpace;/* espace des tuples */

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
    }

	public void write(Tuple t) {
		tupleSpace.add(t);
	}

	public Tuple take(Tuple template) {
		boolean notFound;
		while (notFound) {
			
		}
	}

	public Tuple read(Tuple template) {
		
	}

	public Tuple tryTake(Tuple template) {

	}

	public Tuple tryRead(Tuple template) {

	}

	public Collection<Tuple> takeAll(Tuple template) {

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
			if (Tuple.matches(tupleSpace(i), template){
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

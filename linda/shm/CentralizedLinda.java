package linda.shm;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	private List<Tuple> tupleSpace;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
    }

    @Override
	public void write(Tuple t) {
		tupleSpace.add(t);
	}

	// First version : it is assumed that the template matches a tuple in the tupleSpace
	// Need to block if there is no matching tuple
	@Override
	public Tuple take(Tuple template) {
        Tuple t = null;
		int i = findNext(0, template);
		if (i != -1) {
			t = tupleSpace.remove(i);
		} 
		return t;
	}

	@Override
	public Tuple read(Tuple template) {
		Tuple result = null;
		int index = findNext(0, template);
		if (index == -1){
			//sleep
		} else {
			result = tupleSpace.get(index);
		}
		return result;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		int i = findNext(0, template);
        Tuple t;
		if (i != -1) {
			t = tupleSpace.remove(i);
		} else {
			t = null;
		}
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
        int i = findNext(0, template);
        Tuple t;
		if (i != -1) {
			t = tupleSpace.get(i);
		} else {
			t = null;
		}
		return t;
	}

	@Override
	public List<Tuple> takeAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		
		int i = 0;
		while (i < tupleSpace.size()) {
			i = findNext(i, template); // i is the index of template's next occurence
			// If there is no more matching tuples in the tuplespace,
			// quit the loop
			if (i == -1) {
				break;
			}
			// Else, pop the matching tuple and add it to the list
			else {
				listMatches.add(tupleSpace.remove(i));
			}
			i++;
		}

		return listMatches;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		
		int i = 0;
		while (i < tupleSpace.size()) {
			i = findNext(i, template); // i is the index of template's next occurence
			// If there is no more matching tuples in the tuplespace,
			// quit the loop
			if (i == -1) {
				break;
			}
			// Else, pop the matching tuple and add it to the list
			else {
				listMatches.add(tupleSpace.get(i));
			}
			i++;
		}

		return listMatches;
	}

	@Override
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
			if (tupleSpace.get(i).matches(template)){
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

	@Override
    public void debug(String prefix) {
		// Print the tupleSpace
		System.out.println("TupleSpace : ");
		for (Tuple t : tupleSpace) {
			System.out.println(t);
		}
	}

}

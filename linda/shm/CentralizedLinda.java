package linda.shm;

import linda.*;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	/** TODO: ajouter le reveil en chaine, et ajouter le reveil lors de l'ecriture */
	private List<Tuple> tupleSpace;
	private ReentrantLock monitor;
	/** map of the requests:
	 * Tuple type corresponds to the template requested,
	 * Condition are used to wake the threads that made this request
	 */
	private Map<Tuple, Condition>tupleAviable;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
		monitor = new ReentrantLock();
		tupleAviable = new HashMap<Tuple, Condition>();
    }

    @Override
	public void write(Tuple t) {
		monitor.lock();
    	tupleSpace.add(t);
		Condition condition = popRequest(t);
		if (condition != null){
			condition.signal();
		}
		monitor.unlock();
	}

	// First version : it is assumed that the template matches a tuple in the tupleSpace
	// Need to block if there is no matching tuple
	@Override
	public Tuple take(Tuple template) {
		monitor.lock();
        Tuple t = null;
		int i = findNext(0, template);
		if (i != -1) {
			t = tupleSpace.remove(i);
		} else {
			try {
				registerRequest(template).await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		monitor.unlock();
		return t;
	}

	@Override
	public Tuple read(Tuple template) {
		monitor.lock();
		Tuple result = null;
		int index = findNext(0, template);
		if (index == -1) {
			try {
				Condition condition = registerRequest(template);
				condition.await();
				condition.signal();//signal other threads
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			result = tupleSpace.get(index);
		}
		monitor.unlock();
		return result;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		monitor.lock();
		int i = findNext(0, template);
        Tuple t;
		if (i != -1) {
			t = tupleSpace.remove(i);
		} else {
			t = null;
		}
		monitor.unlock();
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		monitor.lock();
        int i = findNext(0, template);
        Tuple t;
		if (i != -1) {
			t = tupleSpace.get(i);
		} else {
			t = null;
		}
		monitor.unlock();
		return t;
	}

	@Override
	public List<Tuple> takeAll(Tuple template) {
		monitor.lock();
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
		}
		monitor.unlock();

		return listMatches;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		monitor.lock();
		List<Tuple> listMatches = new ArrayList<Tuple>();
		
		int i = 0;
		while (i < tupleSpace.size()) {
			i = findNext(i, template); // i is the index of template's next occurence
			// If there is no more matching tuples in the tuplespace,
			// quit the loop
			if (i == -1) {
				break;
			}
			// Else, get the matching tuple
			else {
				listMatches.add(tupleSpace.get(i));
			}
			i++;
		}
		monitor.unlock();

		return listMatches;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {	
		Tuple t;
		if (timing == eventTiming.IMMEDIATE) {
			// TODO: should wait for a matching template to be in the TS to be
			// triggered
			if (mode == eventMode.TAKE) {
				t = tryTake(template);
			} else {
				// mode == READ
				t = tryRead(template);
			}
			callback.call(t);

		} else {
			// TODO: should not be triggered at once
			// timing = FUTURE
			if (mode == eventMode.TAKE) {
				t = take(template);
			} else {
				// mode = READ
				t = read(template);
			}
			callback.call(t);
		}
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

	/**
	 * register a tuple request inside the Condition collection
	 * @param template the template we tried to match
     */
	private Condition registerRequest(Tuple template){
		return tupleAviable.getOrDefault(template, monitor.newCondition());
	}

	/**
	 * pop a tuple request from the Condition collection
	 * NOTE: the algorithm makes no differences between read/take
	 * @param tuple the newly available tuple
	 * @return the condition associated to the tuple request or null if it don't exist
     */
	private Condition popRequest(Tuple tuple){
		for ( Tuple key : tupleAviable.keySet()){
			if (tuple.matches(key)){
				Condition condition = tupleAviable.get(key);
				// if nobody is waiting for the condition
				if (!monitor.hasWaiters(condition)) {
					tupleAviable.remove(key);
				}
				return condition;
			}
		}
		return null;
	}

	@Override
    public void debug(String prefix) {
		// Print the tuples in the tupleSpace
		System.out.println(prefix + " TupleSpace : ");
		for (Tuple t : tupleSpace) {
			System.out.println(t);
		}
	}

}

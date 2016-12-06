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
	private Map<Tuple, Callback> pendingReads;
	private Map<Tuple, Callback> pendingTakes;
	/** map of the requests:
	 * Tuple type corresponds to the template requested,
	 * Condition are used to wake the threads that made this request
	 */
	//private Map<Tuple, Condition>tupleAviable;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
		//tupleAviable = new HashMap<Tuple, Condition>();
		pendingTakes = new HashMap<Tuple, Callback>();
		pendingReads = new HashMap<Tuple, Callback>();
    }

    @Override
	public void write(Tuple t) {
		boolean matchingTake = false;
		synchronized (int key) {
			for (Tuple template : pendingReads.keySet()) {
				if (template.matches(t)) {
					pendingReads.remove(template).call(t.deepclone());
				}
			}
			for (Tuple template : pendingTries.keySet()) {
				if (template.matches(t)) {
					pendingTakes.remove(template).call(t.deepclone());
					matchingTake = true;
					break;
				}
			}
			if (!matchingTake) {
				tupleSpace.add(t.deepclone());
			}
		}
	}

	// First version : it is assumed that the template matches a tuple in the tupleSpace
	// Need to block if there is no matching tuple
	@Override
	public Tuple take(Tuple template) {
		callback = new BlockingCallback();
		eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (int key) {
			while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			}
		}
		return callback.result;
	}

	@Override
	public Tuple read(Tuple template) {
		callback = new BlockingCallback();
		eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (int key) {
			while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			}
		}
		return callback.result;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple t = null;
		synchronized (this) {
			for (Tuple t2 : tupleSpace) {
				if (t2.matches(template)) {
					t = t2;
					tupleSpace.remove(t2);
					break;
				}
			}
		}
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		Tuple t = null;
		synchronized (this) {
			for (Tuple t2 : tupleSpace) {
				if (t2.matches(template)) {
					t = t2;
					break;
				}
			}
		}
		return t;	
	}

	@Override
	public List<Tuple> takeAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		Tuple t;
		do {
			t = tryTake(template);
			if (t != null) {
				listMatches.add(t);
			}
		} while (t != null);

		return listMatches;
	}

	@Override
	public List<Tuple> readAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		Tuple t;
		do {
			t = tryRead(template);
			if (t != null) {
				listMatches.add(t);
			}
		} while (t != null);

		return listMatches;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {	
		Tuple t=null;
		synchronized (this) {
			if (timing == eventTiming.IMMEDIATE) {
				if (mode == eventMode.TAKE) {
					t = tryTake(template);
				} else {
					// mode == READ
					t = tryRead(template);
				}
				if (t == null) {
					if (mode == eventMode.TAKE) {
						pendingTakes.put(template, callback);
					} else {
						pendingReads.put(template, callback);
					}
				} else {
					callback.call(t); // Retirer du synchronized ?
				}
			} else {
				// timing = FUTURE
				if (mode == eventMode.TAKE) {
					pendingTakes.put(template, callback);
				} else {
					// mode = READ
					pendingReads.put(template, callback);
				}

			}
		}
	}



	@Override
    public void debug(String prefix) {
		// Print the tuples in the tupleSpace
		System.out.println(prefix + " TupleSpace : ");
		for (Tuple t : tupleSpace) {
			System.out.println(t);
		}
	}

	private class BlockingCallback implements Callback {
		
		Tuple result;
		
		private BlockingCallback() {}

		public void call(Tuple t) {
			synchronized (this) {
				result = t;
				notify(); 
			}
		}
	}
}

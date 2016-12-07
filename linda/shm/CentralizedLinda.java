package linda.shm;

import linda.*;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private List<Tuple> tupleSpace;
	// we use the queue interface to handle multiple callbacks over one key
	private Map<Tuple, Queue<Callback>> pendingReads;
	private Map<Tuple, Queue<Callback>> pendingTakes;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
		pendingTakes = new HashMap<Tuple, Queue<Callback>>();
		pendingReads = new HashMap<Tuple, Queue<Callback>>();
    }

	public void write(Tuple t) {
		boolean matchingTake = false;
		synchronized (this) {
			// Call the callbacks of the pending reads on the matching template
			for (Tuple template : pendingReads.keySet()) {
				if (t.matches(template)) {
					for (Callback c : pendingReads.remove(template)) {
						c.call(t.deepclone());
					}
				}
			}
			// Call the callback of the first occurence of the pending takes on
			// the key matching the template
			for (Tuple template : pendingTakes.keySet()) {
				if (t.matches(template)) {
					Queue<Callback> queue = pendingTakes.get(template); 
					// Remove the head of the queue
					queue.remove().call(t.deepclone());
					if (queue.isEmpty()) {
						// Remove the key if there is no more pending takes
						pendingTakes.remove(template);
					}
					matchingTake = true;
					break;
				}
			}
			// If there is no pending take over the tuple t, add it to the
			// tupleSpace
			if (!matchingTake) {
				tupleSpace.add(t.deepclone());
			}
		}
	}

	public Tuple take(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			//while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			//}
			return callback.result;
		}
	}

	public Tuple read(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			//while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			//}
			return callback.result;
		}
	}

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

	public List<Tuple> readAll(Tuple template) {
		List<Tuple> listMatches = new ArrayList<Tuple>();
		synchronized (this) {
			for (Tuple t2 : tupleSpace) {
				if (t2.matches(template)) {
					listMatches.add(t2);
				}
			}
		}
		return listMatches;
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {	
		Tuple t=null;
		boolean tupleFound = false;
		synchronized (this) {
			if (timing == eventTiming.IMMEDIATE) {
				if (mode == eventMode.TAKE) {
					t = tryTake(template);
				} else {
					// mode == READ
					t = tryRead(template);
				}
				if (t == null) {
					// If the tuple was not found in the tupleSpace, add it to
					// the pending operations
					if (mode == eventMode.TAKE) {
						addCallbackToQueue(pendingTakes, template, callback);
					} else {
						addCallbackToQueue(pendingReads, template, callback);
					}
				} else {
					tupleFound = true;
				}
			} else {
				// timing = FUTURE
				// Add the callback to the pending operations
				if (mode == eventMode.TAKE) {
					addCallbackToQueue(pendingTakes, template, callback);
				} else {
					// mode = READ
					addCallbackToQueue(pendingReads, template, callback);
				}

			}
		}
		// If the tuple was found in the tupleSpace, run the callback
		if (tupleFound) {
			callback.call(t); 
		}
	}

	private void addCallbackToQueue(Map<Tuple, Queue<Callback>> map, Tuple template, Callback callback) {
		// If the key already exists, add the callback to the
		// corresponding queue
		if (map.containsKey(template)) {
			map.get(template).add(callback);
		} else {
			// Else, create a new queue
			Queue<Callback> newQueue = new LinkedList<Callback>();
			newQueue.add(callback);
			map.put(template, newQueue);
		}
	}

    public void debug(String prefix) {
		// Print the tuples in the tupleSpace
		System.out.println(prefix + " TupleSpace : ");
		for (Tuple t : tupleSpace) {
			System.out.println(t);
		}

		//Print pendingReads
		System.out.println(prefix + " PendingReads : ");
		for (Tuple t : pendingReads.keySet()) {
			System.out.println(t);
		}

		//Print pendinTakesg
		System.out.println(prefix + " PendingTakes : ");
		for (Tuple t : pendingTakes.keySet()) {
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

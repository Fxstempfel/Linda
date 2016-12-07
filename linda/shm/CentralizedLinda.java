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
	private Map<Tuple, Callback> pendingReads;
	private Map<Tuple, Callback> pendingTakes;

    public CentralizedLinda() {
		tupleSpace = new ArrayList<Tuple>();
		pendingTakes = new HashMap<Tuple, Callback>();
		pendingReads = new HashMap<Tuple, Callback>();
    }

	public void write(Tuple t) {
		boolean matchingTake = false;
		synchronized (this) {
			for (Tuple template : pendingReads.keySet()) {
				if (t.matches(template)) {
					pendingReads.remove(template).call(t.deepclone());
				}
			}
			for (Tuple template : pendingTakes.keySet()) {
				if (t.matches(template)) {
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

	public Tuple take(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			}
			return callback.result;
		}
	}

	public Tuple read(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			while (callback.result == null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			}
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
		Tuple t;
		do {
			t = tryRead(template);
			if (t != null) {
				listMatches.add(t);
			}
		} while (t != null);

		return listMatches;
	}

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

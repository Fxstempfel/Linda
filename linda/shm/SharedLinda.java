package linda.shm;
import linda.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class SharedLinda implements Linda {

	private List<CentralizedLinda> lindaSpace = new ArrayList<CentralizedLinda>();
	private int maxThreads;
	private Map<Tuple, Queue<Callback>> pendingReads;
	private Map<Tuple, Queue<Callback>> pendingTakes;
	private int nbPendingTakes;
	private int nbPendingReads;
	private boolean isReading;



	public SharedLinda() {
		this.nbPendingTakes = 0;
		this.nbPendingReads = 0;
		this.isReading = false;
		this.maxThreads = 4;
		this.pendingReads = new HashMap<Tuple, Queue<Callback>>();
		this.pendingTakes = new HashMap<Tuple, Queue<Callback>>();
		for (int i=0;i<maxThreads;i++) {
			this.lindaSpace.add(new CentralizedLinda());
		}
	}
	
	
	public SharedLinda(int maxThreads) {
		this.nbPendingTakes = 0;
		this.nbPendingReads = 0;
		this.isReading = false;
		this.maxThreads = maxThreads;
		this.pendingReads = new HashMap<Tuple, Queue<Callback>>();
		this.pendingTakes = new HashMap<Tuple, Queue<Callback>>();
		for (int i=0;i<maxThreads;i++) {
			this.lindaSpace.add(new CentralizedLinda());
		}
	}


	@Override
	public void write(Tuple t) {
		synchronized(this){
			while(this.isReading) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		boolean takeMatched = false;
		for(Tuple template : pendingReads.keySet()){
			if(t.matches(template)) {
				for(Callback c : pendingReads.remove(template)) {
					c.call(t.deepclone());
				}
			}
		}
		for (Tuple template : pendingTakes.keySet()) {
			if (t.matches(template)) {
				Queue<Callback> queue = pendingTakes.get(template); 
				// Remove the head of the queue
				queue.remove().call(t.deepclone());
				if (queue.isEmpty()) {
					// Remove the key if there is no more pending takes
					pendingTakes.remove(template);
				}
				takeMatched = true;
				break;
			}
		}
		}
		if(!takeMatched){
			Random rand = new Random();
			int index = rand.nextInt(this.maxThreads);
			this.lindaSpace.get(index).write(t);
		}
	}


	private Tuple accessFromIndex(int index,eventMode me, Tuple template) {
		int localIndex = index;
		Tuple t = null;
		boolean found = false;
		while(localIndex != -1 && !found) {
			switch (me) {
			case READ : 
				t = this.lindaSpace.get(localIndex).tryRead(template);
				if(t != null) {
					found = true;
				} 
				break;
			case TAKE : 
				t = this.lindaSpace.get(localIndex).tryTake(template);
				if(t != null) {
					found = true;
				}
				break;
			default : 
				break;

			}
			localIndex = (localIndex + 1) % this.maxThreads; 
			if(localIndex == index) {
				localIndex = -1;
			}

		}
		return t;
	}


	@Override
	public Tuple take(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		this.nbPendingTakes++;
		eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			if (callback.result==null) {
				try {
					callback.wait();
				} catch(Exception e) {
				}
			}
			this.nbPendingTakes--;
			return callback.result;
		}
	}

	@Override
	public Tuple read(Tuple template) {
		BlockingCallback callback = new BlockingCallback();
		this.nbPendingReads++;
		eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,
				template, callback);
		synchronized (callback) {
			if (callback.result==null) {
				try {
					callback.wait();
				} catch (Exception e) {
				}
			}
			this.nbPendingReads--;
			return callback.result;
		}
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple t =  null;
		synchronized(this) {
			this.isReading = true;
		}
		Random rand = new Random();
		int index = rand.nextInt(this.maxThreads);
		t = accessFromIndex(index,eventMode.TAKE,template);
		synchronized(this) {
			this.isReading = false;
			this.notifyAll();
		}
		return t;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		Tuple t =  null;
		synchronized(this) {
			this.isReading = true;
		}
		Random rand = new Random();
		int index = rand.nextInt(this.maxThreads);
		t = accessFromIndex(index,eventMode.READ,template);
		synchronized(this) {
			this.isReading = false;
			this.notifyAll();
		}
		return t;
	}

	
	private Collection<Tuple> accessFromIndexCollec(eventMode me, Tuple template) {
		int localIndex = 0;
		Collection<Tuple> t  = new ArrayList<Tuple>();
		while(localIndex != -1) {
			switch (me) {
			case READ : 
				t.addAll(this.lindaSpace.get(localIndex).readAll(template));
				break;
			case TAKE : 
				t.addAll(this.lindaSpace.get(localIndex).takeAll(template));
				break;
			default : 
				break;

			}
			localIndex = (localIndex + 1) % this.maxThreads; 
			if(localIndex == 0) {
				localIndex = -1;
			}

		}
		return t;
	}
	
	
	
	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> t = accessFromIndexCollec(eventMode.TAKE, template);
		return t;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> t = accessFromIndexCollec(eventMode.READ, template);
		return t;
	}

	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {	
		Tuple t=null;
		boolean tupleFound = false;
		synchronized (this) {
			if (timing == eventTiming.IMMEDIATE) {
				Random rand = new Random();
				int index = rand.nextInt(this.maxThreads);
				t = accessFromIndex(index, mode, template);
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

	@Override
	public void debug(String prefix) {
		int i=1;
		for(Linda l : this.lindaSpace) {
			l.debug("Linda Fragment number " + i );
			i++;
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

package linda.shm;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import java.util.*;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	Collection<Tuple> tupleSpace;

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

	public void debug(String prefix) {

	}

}

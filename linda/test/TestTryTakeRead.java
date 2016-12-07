package linda;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class TestTryTakeRead {

	final static Linda linda = new linda.tshm.CentralizedLinda();
	static Tuple t1 = new Tuple(4, 5);
	static Tuple t11 = new Tuple(4, 5);
	static Tuple t2 = new Tuple("hello", 15);
	static Tuple t3 = new Tuple(4, "foo");
	Tuple res;
	Tuple res3;
	Tuple res4;
	Tuple res5;
	Collection<Tuple> res2 = new ArrayList<Tuple>();
	Tuple motif = new Tuple(Integer.class, String.class);




	public void atest() {

		res = linda.tryRead(motif);
		res3 = linda.tryTake(motif);
		System.out.println("(2) write: " + t1);
		linda.write(t1);

		System.out.println("(2) write: " + t11);
		linda.write(t11);

		System.out.println("(2) write: " + t2);
		linda.write(t2);

		System.out.println("(2) write: " + t3);
		linda.write(t3);

		res5 = linda.tryRead(motif);
		res4 = linda.tryTake(motif);
		linda.debug("(2)");


		res2 = linda.takeAll(motif);
		System.out.println("(1) Resultat:" + res);

	}

	
	@Test
	public void test() {
		atest();
		assertEquals(null,res);
		assertEquals(null,res3);
		assertEquals(t3,res4);
		assertEquals(t3,res5);
		assertTrue(res2.isEmpty());
	}
	
}

package linda.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import linda.Tuple;
import linda.Linda;

import org.junit.Test;

public class TestTake {

    final static Linda linda = new linda.shm.SharedLinda();
    static Tuple t1 = new Tuple(4, 5);
    static Tuple t11 = new Tuple(4, 5);
    static Tuple t2 = new Tuple("hello", 15);
    static Tuple t3 = new Tuple(4, "foo");
    Tuple res;
    Collection<Tuple> res2 = new ArrayList<Tuple>();




	public void atest() {
              
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("(2) write: " + t1);
                linda.write(t1);

                System.out.println("(2) write: " + t11);
                linda.write(t11);

                System.out.println("(2) write: " + t2);
                linda.write(t2);

                System.out.println("(2) write: " + t3);
                linda.write(t3);
                                
                linda.debug("(2)");

            }
        }.start();
        
        Tuple motif = new Tuple(Integer.class, String.class);
        res = linda.take(motif);
        res2 = linda.readAll(motif);
        System.out.println("(1) Resultat:" + res);
                
    }

	
	@Test
	public void test() {
		atest();
		assertEquals(t3,res);
		assertTrue(res2.isEmpty());
	}
	

}

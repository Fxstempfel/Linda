package linda;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestWrite {
	
	final static Linda linda = new linda.tshm.CentralizedLinda();
    static Tuple t1 = new Tuple(4, 5);
    static Tuple t11 = new Tuple(4, 5);
    static Tuple t2 = new Tuple("hello", 15);
    static Tuple t3 = new Tuple(4, "foo");
    Tuple res;
    Collection<Tuple> res2 = new ArrayList<Tuple>();




	public void atest() {
              
        new Thread() {
            public void run() {
                System.out.println("(2) write: " + t1);
                linda.write(t1);
            }
        }.start();
        
        new Thread() {
            public void run() {
                System.out.println("(2) write: " + t11);
                linda.write(t11);
            }
        }.start();
        
        new Thread() {
            public void run() {
                System.out.println("(2) write: " + t2);
                linda.write(t2);
            }
        }.start();
        
        new Thread() {
            public void run() {
                System.out.println("(2) write: " + t3);
                linda.write(t3);
            }
        }.start();
        
        try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Tuple motif = new Tuple(Integer.class, String.class);
        res = linda.read(motif);
        res2 = linda.readAll(motif);
        System.out.println("(1) Resultat:" + res);
                
    }

	
	@Test
	public void test() {
		atest();
		assertEquals(t3,res);
	}
	
	@Test
	public void test2() {
		assertEquals("["+t3.toString()+"]",linda.readAll(new Tuple(Integer.class, String.class)).toString());
	}
}

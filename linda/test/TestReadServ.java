package linda.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import linda.*;

import org.junit.Test;

public class TestReadServ {

	final static Linda linda1 = new linda.server.LindaClient();
	final static Linda linda2 = new linda.server.LindaClient();
	final static Linda linda3 = new linda.server.LindaClient();
    static Tuple t1 = new Tuple(4, 5);
    static Tuple t11 = new Tuple(4, 5);
    static Tuple t2 = new Tuple("hello", 15);
    static Tuple t3 = new Tuple(4, "foo");
	static Tuple expected2;
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

                System.out.println("(1) write: " + t1);
                linda1.write(t1);

                System.out.println("(1) write: " + t11);
                linda1.write(t11);

                System.out.println("(2) write: " + t2);
                linda2.write(t2);

                System.out.println("(3) write: " + t3);
                linda3.write(t3);
                                
                linda1.debug("(1)");
                linda2.debug("(2)");
                linda3.debug("(3)");

            }
        }.start();
        
        Tuple motif = new Tuple(Integer.class, String.class);
        Tuple motif2 = new Tuple(Integer.class, Integer.class);
        res = linda1.read(motif);
        res2 = linda2.readAll(motif2);
        System.out.println("(1) Resultat:" + res);
		expected2 = new Tuple(new Tuple(4, 5), new Tuple(4, 5));
        
    }

	
	@Test
	public void test() {
		atest();
		assertEquals(t3,res);
		assertEquals(expected2,res2);
	}
	
}

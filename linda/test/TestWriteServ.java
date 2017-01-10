package linda.test;

import static org.junit.Assert.assertEquals;

import linda.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestWriteServ {

	final static Linda linda1 = new linda.server.LindaClient();
	final static Linda linda2 = new linda.server.LindaClient();
	final static Linda linda3 = new linda.server.LindaClient();

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
				System.out.println("(1) write: " + t1);
                linda1.write(t1);

                System.out.println("(2) write: " + t11);
                linda2.write(t11);

                System.out.println("(3) write: " + t2);
                linda3.write(t2);

                System.out.println("(1) write: " + t3);
                linda1.write(t3);

				linda1.debug("(1)");
			}
		}.start();
        
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Tuple motif = new Tuple(Integer.class, String.class);
		res = linda3.read(motif);
		Tuple motif2 = new Tuple(Integer.class, Integer.class);
		res2 = linda2.readAll(motif2);
		System.out.println("(1) Resultat:" + res);
		
	}

	
	@Test
	public void test() {
		atest();
		assertEquals(t3,res);
		assertEquals(new Tuple(new Tuple(4,5), new Tuple(4,5)),res2);
	}
}

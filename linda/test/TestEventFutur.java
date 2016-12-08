package linda;

import static org.junit.Assert.*;

import org.junit.Test;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.TestEventImmediate.MyCallback;

public class TestEventFutur {

	static Tuple motif = new Tuple(Integer.class, String.class);
    static Tuple t1 = new Tuple(4, 5);
    static Tuple t2 = new Tuple("hello", 15);
    static Tuple t3 = new Tuple(4, "foo");
	int b;
	int d;
    

    static Linda linda = new linda.tshm.CentralizedLinda();

	private  class MyCallback implements Callback {
		int a = 0;
        public void call(Tuple t) {
        	try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Got "+t);
        	a = 1;

            
        }
        
        public int geta(){
        	return a;
        }
    }

    public void atest() {


        System.out.println("(2) write: " + t3);
        linda.write(t3);

    	MyCallback c = new MyCallback();
        linda.eventRegister(eventMode.TAKE, eventTiming.FUTURE, motif, c);
        b = c.geta();
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        System.out.println("(2) write: " + t3);
        linda.write(t3);
        
        d= c.geta();
        linda.debug("(2)");

    }
    
    @Test
    public void test(){
    	atest();
    	assertEquals(0,b);
    	assertEquals(1,d);

    }

}

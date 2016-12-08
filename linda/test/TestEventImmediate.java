package linda.test;

import static org.junit.Assert.*;

import org.junit.Test;

import linda.Linda;
import linda.Tuple;
import linda.Callback;

import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class TestEventImmediate {

    static Tuple motif = new Tuple(Integer.class, String.class);
    static Tuple t1 = new Tuple(4, 5);
    static Tuple t2 = new Tuple("hello", 15);
    static Tuple t3 = new Tuple(4, "foo");
	int b;
	int d;
    

    static Linda linda = new linda.shm.CentralizedLinda();

	public  class MyCallback implements Callback {
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

    	MyCallback c = new MyCallback();
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, c);
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

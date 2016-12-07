package linda.test;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

/**
 * Created by thibaut on 07/12/16.
 */
public class SpeedTester {

    public static final int NB_TESTS = 10000;
    private Linda myLinda;
    private Tuple tuple;
    private Tuple template;

    public static void main(String[] a) {
        SpeedTester tester = new SpeedTester();
        System.out.print("test de "+NB_TESTS+" Writes a la chaîne:");
        System.out.println(tester.testWrites()+"ms");
        System.out.print("test de "+NB_TESTS+" Reads a la chaîne:");
        System.out.println(tester.testReads()+"ms");
        System.out.print("test de "+NB_TESTS+" Takes a la chaîne:");
        System.out.println(tester.testTake()+"ms");
    }

    public SpeedTester(){
        myLinda = new CentralizedLinda();
        tuple = new Tuple("toto", 1);
        template = new Tuple(String.class, Integer.class);
    }

    public SpeedTester(Linda linda){
        myLinda = linda;
    }

    public long testReads(){
        Tuple tuple1;
        testWrites();
        long tdeb = System.currentTimeMillis();
        for (int i = 0; i <NB_TESTS; i++){
            tuple1 = myLinda.read(template);
        }
        long tfin = System.currentTimeMillis();
        testTake();
        return tfin-tdeb;
    }

    public long testWrites(){
        long tdeb = System.currentTimeMillis();
        for (int i = 0; i <NB_TESTS; i++){
            myLinda.write(tuple);
        }
        return System.currentTimeMillis()-tdeb;
    }

    public long testTake(){
        Tuple tuple1;
        testWrites();
        long tdeb = System.currentTimeMillis();
        for (int i = 0; i <NB_TESTS; i++) {
            tuple1 = myLinda.take(template);
        }
        return System.currentTimeMillis()-tdeb;
    }
}

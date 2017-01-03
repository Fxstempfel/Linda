package linda.test;

public class LancerCribleShared {

	public static void main(String[] args) {
		
		TestCribleParallelShared test2 = new TestCribleParallelShared(50000);
		test2.doCrible();
		TestCribleSequentiel test = new TestCribleSequentiel(50000);
		test.doCrible();
		
		
		
		
	}
}

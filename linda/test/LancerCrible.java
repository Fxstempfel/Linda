package linda.test;

public class LancerCrible {

	public static void main(String[] args) {
		
		//TestCribleParallel test2 = new TestCribleParallel(50000);
		//test2.doCrible();
		TestCribleSequentiel test = new TestCribleSequentiel(50000);
		test.doCrible();
		
		
		
		
	}
}

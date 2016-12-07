package linda.test;

public class LancerCrible {

	public static void main(String[] args) {
		TestCribleSequentiel test = new TestCribleSequentiel(100);
		TestCribleParallel test2 = new TestCribleParallel(100);
		test.doCrible();
		test2.doCrible();
		
	}
}

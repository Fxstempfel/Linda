package linda.test;

public class LancerCrible {

	public static void main(String[] args) {

		/*TestCribleSequentiel test = new TestCribleSequentiel(50000);
		test.doCrible();*/
		TestCribleParallel test2 = new TestCribleParallel(5000);
		test2.doCrible();
		TestCribleCluster test3 = new TestCribleCluster(5000);
		test3.doCrible();
		
		
		
		
	}
}

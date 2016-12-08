package linda.test;

public class LancerProducConso {

	public static void main(String[] args){
		TestProducteurConsomateur test = new TestProducteurConsomateur(10, 3);
		test.doPC();
	}
}

package linda.semaphore;

public class TestSemaphore {

	static LindaSemaphore semaphore = new LindaSemaphore(1);
	
	
	public static void main(String[] args ) {
		new Thread() {
			public void run(){
				semaphore.p();
				System.out.println("Thread 1 : Je rentre et je bloque l'accès");
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Thread 1 : Je redonne l'accès");
				semaphore.v();
			}
		}.start();
		
		new Thread() {
			public void run(){
				semaphore.p();
				System.out.println("Thread 2 : Je rentre et je bloque l'accès");
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Thread 2 : Je redonne l'accès");
				semaphore.v();
			}
		}.start();
		
	}
	
}

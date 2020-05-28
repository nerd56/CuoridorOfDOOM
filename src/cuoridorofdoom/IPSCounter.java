package cuoridorofdoom;

class IPSCounter {
	private long prev, curr;
	
	IPSCounter() {
		prev = System.nanoTime();
	}
	
	double getIPS() {
		curr = System.nanoTime();
		return 1000000000.0/(-prev + (prev = curr));
	}
}
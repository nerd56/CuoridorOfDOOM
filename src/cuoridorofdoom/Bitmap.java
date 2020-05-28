package cuoridorofdoom;

class Bitmap {
	public final int width;
	public final int height;
	public final int[] bitmap;
	
	Bitmap(int[] bitmap, int width, int height) {
		this.bitmap = bitmap;
		this.width = width;
		this.height = height;
	}
}
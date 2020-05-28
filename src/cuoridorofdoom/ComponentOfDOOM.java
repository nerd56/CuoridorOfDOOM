package cuoridorofdoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;

import java.awt.image.DataBufferInt;

class ComponentOfDOOM extends JComponent {
	private static final int WIDTH = 640;
	private static final int HEIGHT = 360;
	
	private static final int CENT_X = WIDTH/2;
	private static final int CENT_Y = HEIGHT/2;
	
	private static final int COL_SIZE = HEIGHT;
	
	private Color floorColor = Color.GRAY;
	private Color roofColor = Color.DARK_GRAY;
	private Rectangle2D floorRect = new Rectangle2D.Float(0, CENT_Y, WIDTH, CENT_Y);
	private Rectangle2D roofRect  = new Rectangle2D.Float(0, 0, WIDTH, CENT_Y);
	private Font font = new Font("SansSerif", Font.BOLD, 10);
	private double secondsFromLastFPSUpdate;
	private int fps;
	private BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	
	private Bitmap[] imgs;
	
	Camera camera = new Camera(4.5, 4.5, 0.2, Math.PI/3, 0, WIDTH, Map.map);
	IPSCounter ipsCounter = new IPSCounter();
	
	ComponentOfDOOM() {
		String[] paths = {
			"res/redbrick.png",
			"res/eagle.png",
			"res/nazi2.png",
			"res/bluestone.png"
		};
		try {
			loadImages(paths);
		} catch(java.io.IOException e) {e.printStackTrace();}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		var g2 = (Graphics2D) g;
		
		double cos = Math.cos(camera.pov);
		double sin = Math.sin(camera.pov);
		
		for (int i = CENT_Y; i > 0; i--) {
			double z = COL_SIZE/2.0/i;	
			double tg = Math.tan(camera.fov/2)*z;
			double x = camera.x+cos*z+tg*sin;
			double y = camera.y+sin*z-tg*cos;
			double stepX = sin*tg/WIDTH*2;
			double stepY = cos*tg/WIDTH*2;
			
			for (int j = 0; j < WIDTH; j++) {
				var gr = imgs[2];
				int pix1 = gr.bitmap[Math.floorMod((int)(x*gr.width), gr.width) +
										Math.floorMod((int)(y*gr.height), gr.height)*gr.width];
				pixels[j + (i+CENT_Y-1)*WIDTH] = pix1;
				pixels[j + (CENT_Y-i)*WIDTH] = pix1;
	
				x -= stepX;
				y += stepY;
			}
		}
		
		double[][] rays = camera.castRays();
		for (int i = 0; i < rays.length; i++) {
			double len = rays[i][0];
			Bitmap bitmap = imgs[(int)rays[i][2]-1];
			int size = (int)(COL_SIZE/len);
			double stepy = (double)bitmap.height / size;
			int x = (int)(bitmap.width * rays[i][1]);
			int min = Math.min(HEIGHT, size);
			int yOffset = (HEIGHT-size)/2;
			int startY = 0;
			if (yOffset < 0) {
				startY = -yOffset;
				yOffset = 0;
			}
			
			for (int y = 0; y < min; y++) {
				int pixel = bitmap.bitmap[x + (int)((y+startY)*stepy)*bitmap.width];
				pixels[i + (y+yOffset)*WIDTH] = pixel;
			}
		}
		
		g2.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
		
		double ips = ipsCounter.getIPS();
		secondsFromLastFPSUpdate += 1/ips;
		if (secondsFromLastFPSUpdate >= 1) {
			fps = (int)ips;
			secondsFromLastFPSUpdate--;
			System.out.println("fps " + fps);
		}
		/*
		g2.setFont(font);
		g2.setPaint(Color.WHITE);
		g2.drawString(fps+"", 2, 10);
		*/
	}
	
	void loadImages(String[] paths) throws IOException {
		imgs = new Bitmap[paths.length];
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			BufferedImage img = ImageIO.read(new File(path));
			int imgWidth = img.getWidth();
			int imgHeight = img.getHeight();
			int[] rgb = new int[imgWidth*imgHeight];
			img.getRGB(0, 0, imgWidth, imgHeight, rgb, 0, imgWidth);
			imgs[i] = new Bitmap(rgb, imgWidth, imgHeight);
		}
	}
	
	void drawFloorAndRoof(Graphics2D g2) {
		g2.setPaint(floorColor);
		g2.fill(floorRect);
		g2.setPaint(roofColor);
		g2.fill(roofRect);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}

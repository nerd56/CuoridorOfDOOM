package cuoridorofdoom;

import static java.lang.Math.*;
import java.awt.event.*;
import java.util.*;

class Camera {
	private double sensivity = 3;
	private double speed = 4;
	
	public double x, y;
	public double fov, pov;
	private int raysCount;
	private int[][] map;
	private double step;
	private double r;
	public double h = 0.5;
	
	KeyHandler keyListener = new KeyHandler();
	IPSCounter ipsCounter = new IPSCounter();
	
	Camera(double x, double y, double r, double fov, double pov, int raysCount, String[] map) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.fov = fov;
		this.pov = pov;
		this.raysCount = raysCount;
		this.map = new int[map.length][];
		
		for (int row = 0; row < map.length; row++) {
			String str = map[row];
			int[] line = new int[str.length()];
			for (int col = 0; col < str.length(); col++) {
				line[col] = Integer.parseInt(str.charAt(col)+"");
			}
			this.map[row] = line;
		}
		
		step = fov/raysCount;
	}
	
	double[][] castRays() {
		var rays = new double[raysCount][2];
		double aCos = Math.cos(fov/2);
		double left = -Math.sin(fov/2);
		double step = left/raysCount*-2;
		for (int i = 0; i < rays.length; i++) {
			double angle = pov + atan((left+step*i)/aCos);
			double[] ray = castRay(angle);
			rays[i] = ray;
		}
		return rays;
	}
	
	private double[] castRay(double angle) {
		double aCos = cos(angle);
		double aSin = sin(angle);
		double aTan = aSin/aCos;
		
		int yStep = Double.valueOf(aSin).compareTo(0d);
		double y = (int) (yStep < 0 ? this.y : this.y + 1);
		double x = this.x + (y - this.y) / aTan;
		
		double lenXStep = 1.0/0;	
		double imgColX = 0;
		double imgX = 1;
		for (; y > 0 && y < map.length; y += yStep) {
			if (x > map[0].length-1 || x < 0) break;
			int yy = (int)(yStep < 0 ? y-1 : y);
			if (map[yy][(int)x] != 0) {
				imgColX = x-(int)x;
				if (yStep > 0) imgColX = 1 - imgColX;
				lenXStep = getLen(x, y);
				imgX = map[yy][(int)x];
				break;
			}
			x += yStep/aTan;
		}
		
		int xStep = Double.valueOf(aCos).compareTo(0d);
		x = (int) (xStep < 0 ? this.x : this.x + 1);
		y = this.y + (x - this.x) * aTan;
		double lenYStep = 1.0/0;
		double imgColY = 0;
		double imgY = 1;
		for (; x > 0 && x < map[0].length; x += xStep) {
			if (y > map.length-1 || y < 0) break;
			int xx = (int)(xStep < 0 ? x-1 : x);
			if (map[(int)y][xx] != 0) {
				imgColY = y-(int)y;
				if (xStep < 0) imgColY = 1 - imgColY;
				lenYStep = getLen(x, y);
				imgY = map[(int)y][xx];
				break;
			}
			y += xStep*aTan;
		}
		
		return (lenXStep < lenYStep ?
			new double[]{lenXStep * cos(angle-pov), imgColX, imgX} :
			new double[]{lenYStep * cos(angle-pov), imgColY, imgY});
	}
	
	private double getLen(double x, double y) {
		double deltaX = this.x - x;
		double deltaY = this.y - y;
		return sqrt(deltaX*deltaX + deltaY*deltaY);
	}
	
	void update() {
		double xVel = 0, yVel = 0, hVel = 0, rotation = 0;
		
		ArrayList<Integer> keys = keyListener.getKeys();
		
		if (keys.contains(KeyEvent.VK_W)) {
			xVel += cos(pov);
			yVel += sin(pov);
		}
		if (keys.contains(KeyEvent.VK_S)) {
			xVel -= cos(pov);
			yVel -= sin(pov);
		}
		if (keys.contains(KeyEvent.VK_A)) {
			xVel += cos(pov-PI/2);
			yVel += sin(pov-PI/2);
		}
		if (keys.contains(KeyEvent.VK_D)) {
			xVel -= cos(pov-PI/2);
			yVel -= sin(pov-PI/2);
		}
		if (keys.contains(KeyEvent.VK_Q)) {
			hVel -= 1;
		}
		if (keys.contains(KeyEvent.VK_E)) {
			hVel += 1;
		}
		
		if (keys.contains(KeyEvent.VK_RIGHT))
			rotation += sensivity;
		if (keys.contains(KeyEvent.VK_LEFT))
			rotation -= sensivity;
		
		double k = speed/sqrt(xVel*xVel + yVel*yVel);
		if (!Double.valueOf(k).isInfinite()) {
			xVel *= k; yVel *= k;
		}
		
		double ips = ipsCounter.getIPS();
		
		xVel /= ips;
		yVel /= ips;
		hVel /= ips;
		rotation /= ips;
		
		x += xVel;
		if (map[(int)y][(int)(x-r)] != 0) {
			x = (int)x+r;
		}
		else if (map[(int)y][(int)(x+r)] != 0) {
			x = (int)(x+r)-r;
		}
		y += yVel;
		if (map[(int)(y-r)][(int)x] != 0) {
			y = (int)y+r;
		}
		else if (map[(int)(y+r)][(int)x] != 0) {
			y = (int)(y+r)-r;
		}
		h += hVel;
		if (h > 1) h = 1;
		if (h < 0) h = 0;
		pov += rotation;
	}
}











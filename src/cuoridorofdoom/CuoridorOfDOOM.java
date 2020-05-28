package cuoridorofdoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CuoridorOfDOOM {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			var frame = new FrameOfDOOM();
			new Thread(frame).start();
		});
	}
}

class FrameOfDOOM extends JFrame implements Runnable {
	
	ComponentOfDOOM component;
	
	FrameOfDOOM() {
		component = new ComponentOfDOOM();
		add(component);
		pack();
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width - getWidth())/2,
					(size.height - getHeight())/2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CuoridorOfDOOM");
		setVisible(true);
		
		addKeyListener(component.camera.keyListener);
	}
	
	public void run() {
		while (true) {
			component.camera.update();
			repaint();
		}
	}
}

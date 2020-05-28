package cuoridorofdoom;

import java.awt.event.*;
import java.util.ArrayList;

class KeyHandler extends KeyAdapter {
	private ArrayList<Integer> keys = new ArrayList<>();
	
	@Override
	public void keyPressed(KeyEvent e) {
		Integer keyCode = e.getKeyCode();
		if (!keys.contains(keyCode))
			keys.add(keyCode);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		Integer keyCode = e.getKeyCode();
		keys.remove(keyCode);
	}
	ArrayList<Integer> getKeys() { return keys; }
}
package com.nbuwalda.spaceinvaders.controls;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard {

	private static boolean[] keys = new boolean[1024];

	public static void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyHandler(),
				AWTEvent.KEY_EVENT_MASK);
	}

	public static void init(Component c) {
		c.addKeyListener(new KeyHandler());
	}

	public static boolean isPressed(int key) {
		return keys[key];
	}

	public static void setPressed(int key, boolean pressed) {
		keys[key] = pressed;
	}

	private static class KeyHandler extends KeyAdapter implements
			AWTEventListener {

		public void keyPressed(KeyEvent event) {
			if (event.isConsumed()) {
				return;
			}
			keys[event.getKeyCode()] = true;
		}

		public void keyReleased(KeyEvent event) {
			if (event.isConsumed()) {
				return;
			}

			KeyEvent nextPress = (KeyEvent) Toolkit.getDefaultToolkit()
					.getSystemEventQueue().peekEvent(KeyEvent.KEY_PRESSED);

			if ((nextPress == null) || (nextPress.getWhen() != event.getWhen())
					|| (nextPress.getKeyCode() != event.getKeyCode())) {
				keys[event.getKeyCode()] = false;
			}
		}

		@Override
		public void eventDispatched(AWTEvent event) {
			if (event.getID() == KeyEvent.KEY_PRESSED) {
              	keyPressed((KeyEvent) event);
          	}
          	if (event.getID() == KeyEvent.KEY_RELEASED) {
              	keyReleased((KeyEvent) event);
            }
		}
	}
}

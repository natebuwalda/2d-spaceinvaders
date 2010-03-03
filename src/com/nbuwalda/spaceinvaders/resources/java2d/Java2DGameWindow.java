package com.nbuwalda.spaceinvaders.resources.java2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nbuwalda.spaceinvaders.GameWindow;
import com.nbuwalda.spaceinvaders.GameWindowCallback;
import com.nbuwalda.spaceinvaders.controls.Keyboard;


public class Java2DGameWindow extends Canvas implements GameWindow {

	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
	private boolean gameRunning = true;
	private JFrame frame;
	private int width;
	private int height;
	private GameWindowCallback callback;
	private Graphics2D g;
	
	public Java2DGameWindow() {
		frame = new JFrame();
	}
	
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	public void setResolution(int x, int y) {
		width = x;
		height = y;
	}

	public void startRendering() {
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);
	
		Keyboard.init(this);
		
		setBounds(0,0,width,height);
		panel.add(this);
		
		setIgnoreRepaint(true);
		
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (callback != null) {
					callback.windowClosed();
				} else {
					System.exit(0);
				}
			}
		});

		requestFocus();
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		if (callback != null) {
			callback.initialise();
		}
		
		gameLoop();
	}

	public void setGameWindowCallback(GameWindowCallback callback) {
		this.callback = callback;
	}

	public boolean isKeyPressed(int keyCode) {
		return Keyboard.isPressed(keyCode);
	}
	
	Graphics2D getDrawGraphics() {
		return g;
	}
	
	private void gameLoop() {
		while (gameRunning) {

			g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);
			
			if (callback != null) {
				callback.frameRendering();
			}

			g.dispose();
			strategy.show();
		}
	}
}
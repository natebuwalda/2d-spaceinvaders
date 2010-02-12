package com.nbuwalda.spaceinvaders;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Java2DGameWindow extends Canvas{
	
	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	private BufferStrategy strategy;
	private JFrame mainFrame;
	private GameWindowCallback callback;
	private Graphics2D graphics;
	private boolean gameStarted = false;
	private boolean gameRunning = true;
	
	public Java2DGameWindow() {
		mainFrame = new JFrame();
	}
	
	private void gameLoop() {
		
		while (gameRunning) {			
			Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
			graphics.setBackground(Color.BLACK);
			graphics.fillRect(0, 0, 800, 600);
	
			if (callback != null) {
				callback.frameRendering();
			}

			graphics.dispose();
			strategy.show();
			
		}
	}
}

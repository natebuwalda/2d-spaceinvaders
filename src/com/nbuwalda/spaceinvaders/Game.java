package com.nbuwalda.spaceinvaders;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.nbuwalda.spaceinvaders.entity.AlienEntity;
import com.nbuwalda.spaceinvaders.entity.Entity;
import com.nbuwalda.spaceinvaders.entity.ShipEntity;
import com.nbuwalda.spaceinvaders.entity.ShotEntity;

public class Game extends Canvas {
	private static final long serialVersionUID = 1L;
	
	private static final int LEVEL_COLUMNS = 12;
	private static final int LEVEL_ROWS = 5;
	private static final int FIRING_INTERVAL = 1000;
	private static final int RIGHT_SHIP_VELOCITY = 300;
	private static final int LEFT_SHIP_VELOCITY = -300;
	private static final int DOWN_SHIP_VELOCITY = 30;
	private static final int UP_SHIP_VELOCITY = -30;
	private static final int SHOT_UP_VELOCITY = -100;
	private static final int ALIEN_VELOCITY = -75;

	private BufferStrategy strategy;
	private List<Entity> entities;
	private List<Entity> removeList;
	private ShipEntity ship;
	private int alienCount;
	private int levelRows;
	private int levelColumns;
	private long timeLastFired;
	private String message = "";
	private boolean gameRunning = true;
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean upPressed = false;
	private boolean downPressed = false;
	private boolean firePressed = false;
	private boolean waitingForKeyPress = true;
	private boolean logicRequiredThisLoop = false;

	
	public Game() {
		JFrame mainFrame = new JFrame("Space Invaders");

		JPanel mainPanel = (JPanel) mainFrame.getContentPane();
		mainPanel.setPreferredSize(new Dimension(800, 600));
		mainPanel.setLayout(null);

		setBounds(0, 0, 800, 600);
		mainPanel.add(this);

		setIgnoreRepaint(true);
		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setVisible(true);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		initEntities();
		
		addKeyListener(new KeyInputHandler());
		requestFocus();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.gameLoop();
	}

	private void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();

		while (gameRunning) {
			long sinceLastLoopTime = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
			graphics.setBackground(Color.BLACK);
			graphics.fillRect(0, 0, 800, 600);

			moveEntities(sinceLastLoopTime);
			drawEntities(graphics);
			checkForCollisions();
			
			entities.removeAll(removeList);
			removeList.clear();
			
			performGameLogic();
			waitAndShowMessages(graphics);
			
			graphics.dispose();
			strategy.show();
			
			controlPlayerShip();	
			if (firePressed) {
				tryToFire();
			}
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}

		}

	}

	private void waitAndShowMessages(Graphics2D graphics) {
		if (waitingForKeyPress) {
			graphics.setColor(Color.white);
			graphics.drawString(message,(800 - graphics.getFontMetrics().stringWidth(message))/2, 250);
			graphics.drawString("Press any key",(800 - graphics.getFontMetrics().stringWidth("Press any key"))/2, 300);
		}
	}

	private void performGameLogic() {
		if (logicRequiredThisLoop) {
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				entity.doLogic();
			}
			
			logicRequiredThisLoop = false;
		}
	}

	private void checkForCollisions() {
		for (Entity me : entities) {
			for (Entity him : entities) {
				if ((me != him) && (me.collidesWith(him))) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}
	}

	private void controlPlayerShip() {
		ship.setXVelocity(0);
		ship.setYVelocity(0);
		
		if ((leftPressed) && (!rightPressed)) {
			ship.setXVelocity(LEFT_SHIP_VELOCITY);
		} else if ((rightPressed) && (!leftPressed)) {
			ship.setXVelocity(RIGHT_SHIP_VELOCITY);
		}

		if ((upPressed) && (!downPressed)) {
			ship.setYVelocity(UP_SHIP_VELOCITY);
		} else if ((downPressed) && (!upPressed)) {
			ship.setYVelocity(DOWN_SHIP_VELOCITY);
		}
		
	}

	private void drawEntities(Graphics2D graphics) {
		for (Entity entity : entities) {
			entity.draw(graphics);
		}
	}

	private void moveEntities(long sinceLastLoopTime) {
		if (!waitingForKeyPress) {
			for (Entity entity : entities) {
				entity.move(sinceLastLoopTime);
			}
		}
	}

	private void initEntities() {
		entities = new ArrayList<Entity>();
		removeList = new ArrayList<Entity>();
		
		ship = new ShipEntity(this,"sprites/ship.gif",370,550);
		entities.add(ship);
		
		alienCount = 0;
		levelRows = LEVEL_ROWS;
		levelColumns = LEVEL_COLUMNS;
		
		for (int row = 0; row < levelRows; row++) {
			for (int column = 0; column < levelColumns; column++) {
				Entity alien = new AlienEntity(this,"sprites/alien.gif", (100 + (column * 50)), ((50) + row * 30));
				alien.setXVelocity(ALIEN_VELOCITY);
				entities.add(alien);
				alienCount++;

			}
		}
	}

	private void tryToFire() {
		if (System.currentTimeMillis() - timeLastFired < FIRING_INTERVAL) {
			return;
		}
		
		timeLastFired = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", (int)(ship.getXPosition() + 10), (int)(ship.getYPosition() - 30));
		shot.setYVelocity(SHOT_UP_VELOCITY);
		entities.add(shot);
	}
	
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	public void updateLogic() {
		logicRequiredThisLoop = true;
	}
	
	public void notifyPlayerDeath() {
		message = "Oh no! They got you, try again?";
		waitingForKeyPress = true;
	}

	public void notifyWin() {
		message = "You win!";
		waitingForKeyPress = true;
	}
	
	public void notifyAlienKilled() {
		alienCount--;
		
		if (alienCount == 0) {
			notifyWin();
		}
		
		for (Entity entity : entities) {
			if (entity instanceof AlienEntity) {
				entity.setXVelocity(entity.getXVelocity() * 1.02);
			}
		}
	}
	
	private void startGame() {
		entities.clear();
		initEntities();
		
		leftPressed = false;
		rightPressed = false;
		upPressed = false;
		downPressed = false;
		firePressed = false;
	}
	
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount;

		public void keyPressed(KeyEvent event) {
			if (waitingForKeyPress) {
				return;
			}
			
			switch (event.getKeyCode()){
				case KeyEvent.VK_LEFT:
					leftPressed = true;
					break;
				case KeyEvent.VK_RIGHT:
					rightPressed = true;
					break;
				case KeyEvent.VK_UP:
					upPressed = true;
					break;
				case KeyEvent.VK_DOWN:
					downPressed = true;
					break;
				case KeyEvent.VK_SPACE:
					firePressed = true;
					break;
				default:
					break;
			}
		}
		
		public void keyReleased(KeyEvent event) {
			if (waitingForKeyPress) {
				return;
			}
			
			switch (event.getKeyCode()){
				case KeyEvent.VK_LEFT:
					leftPressed = false;
					break;
				case KeyEvent.VK_RIGHT:
					rightPressed = false;
					break;
				case KeyEvent.VK_UP:
					upPressed = false;
					break;
				case KeyEvent.VK_DOWN:
					downPressed = false;
					break;
				case KeyEvent.VK_SPACE:
					firePressed = false;
					break;
				default:
					break;
			}
		}
		
		public void keyTyped(KeyEvent e) {
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}


}

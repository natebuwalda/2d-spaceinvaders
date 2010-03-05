package com.nbuwalda.spaceinvaders;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.nbuwalda.game.entity.AbstractEntity;
import com.nbuwalda.game.resources.ResourceFactory;
import com.nbuwalda.game.resources.Sprite;
import com.nbuwalda.game.timer.SystemTimer;
import com.nbuwalda.spaceinvaders.entity.AlienEntity;
import com.nbuwalda.spaceinvaders.entity.BomberEntity;
import com.nbuwalda.spaceinvaders.entity.DroneEntity;
import com.nbuwalda.spaceinvaders.entity.EscortEntity;
import com.nbuwalda.spaceinvaders.entity.HunterEntity;
import com.nbuwalda.spaceinvaders.entity.ShipEntity;
import com.nbuwalda.spaceinvaders.entity.ShotEntity;

public class Game extends Canvas implements GameWindowCallback {

	private static final long serialVersionUID = 1L;

	private static final int MAX_ALIEN_SHOTS = 3;
	private static final int LEVEL_COLUMNS = 12;
	private static final int LEVEL_ROWS = 5;
	private static final int FIRING_INTERVAL = 1000;
	private static final int RIGHT_SHIP_VELOCITY = 300;
	private static final int LEFT_SHIP_VELOCITY = -300;
	private static final int DOWN_SHIP_VELOCITY = 30;
	private static final int UP_SHIP_VELOCITY = -30;
	private static final int SHOT_UP_VELOCITY = -200;
	private static final int ALIEN_VELOCITY = -75;

	private List<AbstractEntity> entities = new ArrayList<AbstractEntity>();
	private List<AbstractEntity> removeList = new ArrayList<AbstractEntity>();
	private AbstractEntity ship;
	private GameWindow window;
	private Sprite message;
	private Sprite pressAnyKey;
	private Sprite youWin;
	private Sprite gotYou;

	private long lastFpsTime = 0;
	private int fps;
	private double moveSpeed = 300;
	private long lastFire = 0;
	private long firingInterval = 500;
	private int alienCount;
	private boolean waitingForKeyPress = true;
	private boolean logicRequiredThisLoop = false;
	private boolean fireHasBeenReleased = false;
	private long lastLoopTime = System.currentTimeMillis();
	private String windowTitle = "Space Invaders 104 - Version (0.4)";

	public Game(int renderingType) {
		ResourceFactory.get().setRenderingType(renderingType);
		window = ResourceFactory.get().getGameWindow();

		window.setResolution(800, 600);
		window.setGameWindowCallback(this);
		window.setTitle(windowTitle);

		window.startRendering();
	}

	public void initialise() {
		gotYou = ResourceFactory.get().getSprite("default-sprites/gotyou.gif");
		pressAnyKey = ResourceFactory.get()
				.getSprite("default-sprites/pressanykey.gif");
		youWin = ResourceFactory.get().getSprite("default-sprites/youwin.gif");
		message = pressAnyKey;

		startGame();
	}

	private void startGame() {
		entities.clear();
		initEntities();
	}

	private void initEntities() {
		ship = new ShipEntity(this, "default-sprites/nateship.gif", 370, 550);
		entities.add(ship);

		alienCount = 0;
		for (int row = 0; row < 6; row++) {
			for (int x = 0; x < 12; x++) {
				AbstractEntity alien = null;
				if (row == 2) {
					alien = new HunterEntity(this, 100 + (x * 50), (50) + row * 35);
				} else if (row == 1) {
					alien = new EscortEntity(this, 100 + (x * 50), (50) + row * 35);
				} else if (row == 0) {
					alien = new BomberEntity(this, 100 + (x * 50), (50) + row * 35);
				} else {
					alien = new DroneEntity(this, 100 + (x * 50), (50) + row * 35);
				}
				entities.add(alien);
				alienCount++;
			}
		}
	}

	public void updateLogic() {
		logicRequiredThisLoop = true;
	}

	public void removeEntity(AbstractEntity entity) {
		removeList.add(entity);
	}

	public void notifyDeath() {
		message = gotYou;
		waitingForKeyPress = true;
	}

	public void notifyWin() {
		message = youWin;
		waitingForKeyPress = true;
	}

	public void notifyAlienKilled() {
		alienCount--;

		if (alienCount == 0) {
			notifyWin();
		}

		for (int i = 0; i < entities.size(); i++) {
			AbstractEntity entity = (AbstractEntity) entities.get(i);

			if (entity instanceof AlienEntity) {
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}

	public void tryToFire() {
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this, "default-sprites/shot.gif",
				ship.getX() + 10, ship.getY() - 30);
		entities.add(shot);
	}

	public void frameRendering() {
		long sleepTime = lastLoopTime + 10 - System.currentTimeMillis();
		if (sleepTime > 0) {
			SystemTimer.sleep(sleepTime);
		}

		long frameTime = System.currentTimeMillis() - lastLoopTime;
		SystemTimer.addFrameTime(frameTime);
		lastLoopTime = System.currentTimeMillis();
		fps++;

		if (lastFpsTime >= 1000) {
			window.setTitle(windowTitle + " (FPS: " + fps + ")");
			lastFpsTime = 0;
			fps = 0;
		}

		if (!waitingForKeyPress) {
			for (int i = 0; i < entities.size(); i++) {
				AbstractEntity entity = (AbstractEntity) entities.get(i);
				entity.move(SystemTimer.getAverageFrameTime());
			}
		}

		for (int i = 0; i < entities.size(); i++) {
			AbstractEntity entity = (AbstractEntity) entities.get(i);

			entity.draw();
		}

		for (int p = 0; p < entities.size(); p++) {
			for (int s = p + 1; s < entities.size(); s++) {
				AbstractEntity me = (AbstractEntity) entities.get(p);
				AbstractEntity him = (AbstractEntity) entities.get(s);

				if (me.collidesWith(him)) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}

		entities.removeAll(removeList);
		removeList.clear();

		if (logicRequiredThisLoop) {
			for (int i = 0; i < entities.size(); i++) {
				AbstractEntity entity = (AbstractEntity) entities.get(i);
				entity.doLogic();
			}

			logicRequiredThisLoop = false;
		}

		if (waitingForKeyPress) {
			message.draw(325, 250);
		}

		ship.setHorizontalMovement(0);

		boolean leftPressed = window.isKeyPressed(KeyEvent.VK_LEFT);
		boolean rightPressed = window.isKeyPressed(KeyEvent.VK_RIGHT);
		boolean firePressed = window.isKeyPressed(KeyEvent.VK_SPACE);

		if (!waitingForKeyPress) {
			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);
			}

			if (firePressed) {
				tryToFire();
			}
		} else {
			if (!firePressed) {
				fireHasBeenReleased = true;
			}
			if ((firePressed) && (fireHasBeenReleased)) {
				waitingForKeyPress = false;
				fireHasBeenReleased = false;
				startGame();
			}
		}

		if (window.isKeyPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
	}

	public void windowClosed() {
		System.exit(0);
	}

	public static void main(String argv[]) {
		int result = JOptionPane.showOptionDialog(null, "Java2D or LWJGL?",
				"Java2D or LWJGL?", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "Java2D",
						"LWJGL" }, null);

		if (result == 0) {
			new Game(ResourceFactory.JAVA2D);
		} else if (result == 1) {
			new Game(ResourceFactory.OPENGL_LWJGL);
		}
	}
}
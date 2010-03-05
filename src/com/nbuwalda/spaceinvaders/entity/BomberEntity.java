package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.game.entity.AbstractEntity;
import com.nbuwalda.game.resources.ResourceFactory;
import com.nbuwalda.game.resources.Sprite;
import com.nbuwalda.spaceinvaders.Game;

public class BomberEntity extends AbstractEntity implements Alien {
	private double moveSpeed = 75;
	private Game game;
	private Sprite[] frames = new Sprite[4];
	private long lastFrameChange;
	private long frameDuration = 250;
	private int frameNumber;

	public BomberEntity(Game game, int x, int y) {
		super("default-sprites/red-circle.png", x, y);

		frames[0] = sprite;
		frames[1] = ResourceFactory.get().getSprite("default-sprites/red-circle.png");
		frames[2] = sprite;
		frames[3] = ResourceFactory.get().getSprite("default-sprites/red-circle.png");

		this.game = game;
		dx = -moveSpeed;
	}

	public void move(long delta) {
		lastFrameChange += delta;

		if (lastFrameChange > frameDuration) {
			// reset our frame change time counter
			lastFrameChange = 0;

			// update the frame
			frameNumber++;
			if (frameNumber >= frames.length) {
				frameNumber = 0;
			}

			sprite = frames[frameNumber];
		}

		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}

		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		}

		super.move(delta);
	}


	public void doLogic() {
		dx = -dx;
		y += 10;

		if (y > 570) {
			game.notifyDeath();
		}
	}


	public void collidedWith(AbstractEntity other) {
		
	}
}
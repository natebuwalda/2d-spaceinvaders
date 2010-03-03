package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.game.entity.AbstractEntity;
import com.nbuwalda.spaceinvaders.Game;

public class ShotEntity extends AbstractEntity {
	private double moveSpeed = -300;
	private Game game;
	private boolean used = false;

	public ShotEntity(Game game, String sprite, int x, int y) {
		super(sprite, x, y);

		this.game = game;

		dy = moveSpeed;
	}


	public void move(long delta) {
		super.move(delta);

		if (y < -100) {
			game.removeEntity(this);
		}
	}

	public void collidedWith(AbstractEntity other) {
		if (used) {
			return;
		}

		if (other instanceof AlienEntity) {
			game.removeEntity(this);
			game.removeEntity(other);

			game.notifyAlienKilled();
			used = true;
		}
	}
}
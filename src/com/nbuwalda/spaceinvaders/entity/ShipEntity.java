package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.game.entity.AbstractEntity;
import com.nbuwalda.spaceinvaders.Game;

public class ShipEntity extends AbstractEntity {

	private Game game;

	public ShipEntity(Game game, String ref, int x, int y) {
		super(ref, x, y);
		this.game = game;
	}

	public void move(long delta) {
		if ((dx < 0) && (x < 10)) {
			return;
		}
		
		if ((dx > 0) && (x > 750)) {
			return;
		}

		super.move(delta);
	}

	public void collidedWith(AbstractEntity other) {
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
	}
}
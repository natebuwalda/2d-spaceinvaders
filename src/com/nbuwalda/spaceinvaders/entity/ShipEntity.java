package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.spaceinvaders.Game;

public class ShipEntity extends Entity {

	private Game game;

	public ShipEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		// left and right bounds
		if (getXVelocity() < 0 && getXPosition() < 10) {
			return;
		} else if (getXVelocity() > 0 && getXPosition() > 750) {
			return;
		}
		
		// up and down bounds
		if (getYVelocity() < 0 && getYPosition() < 500) {
			return;
		} else if (getYVelocity() > 0 && getYPosition() > 550) {
			return;
		}
		
		super.move(delta);
	}

	@Override
	public void collidedWith(Entity other) {
		if (other instanceof AlienEntity) {
			game.notifyPlayerDeath();
		}
	}

	@Override
	public void doLogic() {
		// no logic
	}

	
}

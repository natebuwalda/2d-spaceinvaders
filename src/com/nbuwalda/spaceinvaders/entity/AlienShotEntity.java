package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.spaceinvaders.Game;

public class AlienShotEntity extends Entity {

	private Game game;

	public AlienShotEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		super.move(delta);
		
		if (getYPosition() > 600) {
			game.removeEntity(this);
			game.setAlienShotCount(game.getAlienShotCount() - 1);
		}
	}

	@Override
	public void collidedWith(Entity other) {
		if (other instanceof ShipEntity) {
			game.removeEntity(this);
			game.removeEntity(other);	
			game.notifyPlayerDeath();
		}	
	}

	@Override
	public void doLogic() {
		// no logic
	}

	
}

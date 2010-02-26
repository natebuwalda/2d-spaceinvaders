package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.spaceinvaders.Game;
import com.nbuwalda.spaceinvaders.resources.ResourceFactory;

public class AlienShotEntity extends AbstractEntity {

	private Game game;

	public AlienShotEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
		this.getFrames().add(ResourceFactory.getFactory().createSprite("sprites/shot.gif"));
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
	public void collidedWith(AbstractEntity other) {
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


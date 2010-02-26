package com.nbuwalda.spaceinvaders.entity;

import com.nbuwalda.spaceinvaders.Game;
import com.nbuwalda.spaceinvaders.resources.ResourceFactory;

public class ShotEntity extends AbstractEntity {

	private Game game;

	public ShotEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
		this.getFrames().add(ResourceFactory.getFactory().createSprite("sprites/shot.gif"));
	}

	@Override
	public void move(long delta) {
		super.move(delta);
		
		if (getYPosition() < -100) {
			game.removeEntity(this);
		}
	}

	@Override
	public void collidedWith(AbstractEntity other) {
		if (other instanceof AlienEntity) {
			game.removeEntity(this);
			game.removeEntity(other);	
			game.notifyAlienKilled();
		}	
	}

	@Override
	public void doLogic() {
		// no logic
	}

	
}


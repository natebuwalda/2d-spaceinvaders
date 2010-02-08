package com.nbuwalda.spaceinvaders;

public class ShotEntity extends Entity {

	private Game game;

	public ShotEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		super.move(delta);
		
		if (getYPosition() < -100) {
			game.removeEntity(this);
		}
	}

	@Override
	public void collidedWith(Entity other) {
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

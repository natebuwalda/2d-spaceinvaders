package com.nbuwalda.spaceinvaders;

public class AlienEntity extends Entity {

	private Game game;
	
	public AlienEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		if ((getXVelocity() < 0 && getXPosition() < 10)
				|| (getXVelocity() > 0 && getXPosition() > 750)) {
			game.updateLogic();
		}
		
		super.move(delta);
	}

	@Override
	public void collidedWith(Entity other) {
		//no logic
	}

	@Override
	public void doLogic() {
		setXVelocity(getXVelocity() * -1);
		setYPosition(getYPosition() + 10);
		
		if (getYPosition() > 570) {
			game.notifyPlayerDeath();
		}

	}

	
}

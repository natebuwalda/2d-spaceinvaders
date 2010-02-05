package com.nbuwalda.spaceinvaders;

public class AlienEntity extends Entity {

	private Game game;
	
	public AlienEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		if ((getXVelocity() < 0 && getXPostion() < 10)
				|| (getXVelocity() > 0 && getXPostion() > 750)) {
			game.updateLogic();
		}
		
		super.move(delta);
	}

	
}

package com.nbuwalda.spaceinvaders;

public class ShipEntity extends Entity {

	private Game game;

	public ShipEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		if (getXVelocity() < 0 && getXPostion() < 10) {
			System.out.println("Hit left bounds");
			return;
		}
		
		if (getXVelocity() > 0 && getXPostion() > 750) {
			System.out.println("Hit right bounds");
			return;
		}
		
		super.move(delta);
	}

	
}

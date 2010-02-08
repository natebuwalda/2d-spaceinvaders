package com.nbuwalda.spaceinvaders;

public class ShipEntity extends Entity {

	private Game game;

	public ShipEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
	}

	@Override
	public void move(long delta) {
		if (getXVelocity() < 0 && getXPosition() < 10) {
			System.out.println("Hit left bounds");
			return;
		} else if (getXVelocity() > 0 && getXPosition() > 750) {
			System.out.println("Hit right bounds");
			return;
		}
		
		if (getYVelocity() < 0 && getYPosition() < 500) {
			System.out.println("Hit top bounds");
			return;
		} else if (getYVelocity() > 0 && getYPosition() > 550) {
			System.out.println("Hit bottom bounds");
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

	
}

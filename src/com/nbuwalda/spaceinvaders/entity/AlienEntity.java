package com.nbuwalda.spaceinvaders.entity;

import java.util.Random;

import com.nbuwalda.spaceinvaders.Game;
import com.nbuwalda.spaceinvaders.sprite.SpriteStore;

public class AlienEntity extends Entity {

	private Game game;
	private boolean tryToFire = false;
	
	public AlienEntity(Game game, String imageRef, int xStartPosition, int yStartPosition) {
		super(imageRef, xStartPosition, yStartPosition);
		this.game = game;
		this.getFrames().add(SpriteStore.getStore().getSprite("sprites/alien.gif"));
		this.getFrames().add(SpriteStore.getStore().getSprite("sprites/alien2.gif"));
		this.getFrames().add(SpriteStore.getStore().getSprite("sprites/alien.gif"));
		this.getFrames().add(SpriteStore.getStore().getSprite("sprites/alien3.gif"));
	}

	@Override
	public void move(long delta) {
		setLastFrameChange(getLastFrameChange() + delta);
		if (getLastFrameChange() > getFrameDuration()) {
			setLastFrameChange(0);

			setCurrentFrameNumber(getCurrentFrameNumber() + 1);
			if (getCurrentFrameNumber() >= getFrames().size()) {
				setCurrentFrameNumber(0);
			}
			
			setSprite(getFrames().get(getCurrentFrameNumber()));
		}
		
		if ((getXVelocity() < 0 && getXPosition() < 10)
				|| (getXVelocity() > 0 && getXPosition() > 750)) {
			game.updateLogic();
		}
		
		int chanceToFire = 5;
		Random randomCheck = new Random();
		if (randomCheck.nextInt(100) < chanceToFire) {
			tryToFire = true;
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
		if ((getXVelocity() < 0 && getXPosition() < 10)
				|| (getXVelocity() > 0 && getXPosition() > 750)) {
			setXVelocity(getXVelocity() * -1);
			setYPosition(getYPosition() + 10);
			
			if (getYPosition() > 570) {
				game.notifyPlayerDeath();
			}
		}
		
		if (tryToFire && (game.getAlienShotCount() < game.getAlienShotMax())) {
			AlienShotEntity shot = new AlienShotEntity(this.game, "sprites/shot.gif", (int)(this.getXPosition() - 10), (int)(this.getYPosition() + 30));
			shot.setYVelocity(200);
			game.getEntities().add(shot);
			game.setAlienShotCount(game.getAlienShotCount() + 1);
		}
	}

	
}

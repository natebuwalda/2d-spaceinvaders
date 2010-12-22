package com.jyc.scorpios.entity;

import com.jyc.scorpios.ScorpiosGame;

import java.io.IOException;

public class ShotEntity extends AbstractEntity {
    private float moveSpeed = -300;
    private ScorpiosGame game;

    public ShotEntity(ScorpiosGame scorpiosGame, Integer x, Integer y) throws IOException {
        super(scorpiosGame.getSprite("shot.gif"), x, y);

        this.game = scorpiosGame;
		verticalMovement = moveSpeed;
    }

    @Override
    public void move(long delta) {
		super.move(delta);

		if (y < -100) {
            game.getEntityCache().removeEntity(this);
        }
	}

    @Override
    public void collidedWith(AbstractEntity other) {
		if (other instanceof GreenGnatEntity) {
            game.getEntityCache().removeEntity(this);
            game.getEntityCache().removeEntity(other);
            game.notifyAlienKilled();
		}
    }
}

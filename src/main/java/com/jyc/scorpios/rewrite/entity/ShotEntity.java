package com.jyc.scorpios.rewrite.entity;

import com.jyc.scorpios.ScorpiosGame;
import com.jyc.scorpios.rewrite.Sprite;

import java.io.IOException;

public class ShotEntity extends AbstractEntity {
    private float moveSpeed = -300;

    public ShotEntity(Sprite sprite, Integer x, Integer y) throws IOException {
        super(sprite, x, y);

		verticalMovement = moveSpeed;
    }

    @Override
    public void move(long delta) {
		super.move(delta);

		if (y < -100) {
//            game.getEntityCache().removeEntity(this);
        }
	}

    @Override
    public void collidedWith(AbstractEntity other) {
		if (other instanceof GreenGnatEntity) {
//            game.getEntityCache().removeEntity(this);
//            game.getEntityCache().removeEntity(other);
//            game.notifyAlienKilled();
		}
    }
}

package com.jyc.scorpios;

import java.io.IOException;

public class ShotEntity extends AbstractEntity {
    private static final int TOP_BORDER = -100;
    private float moveSpeed = -300;
    private ScorpiosGame game;
    private boolean used;

    public ShotEntity(ScorpiosGame scorpiosGame, String s, int i, int i1) throws IOException {
        super(scorpiosGame.getSprite("shot.gif"), i, i1);

        this.game = scorpiosGame;
		verticalMovement = moveSpeed;
    }

    public void reinitialize(float x, float y) {
        this.x = x;
		this.y = y;
		used = false;
    }

    @Override
    public void move(long delta) {
		super.move(delta);

		if (y < TOP_BORDER) {
			game.removeEntity(this);
		}
	}

    @Override
    public void collidedWith(AbstractEntity other) {
        if (used) {
			return;
		}

		if (other instanceof AlienEntity) {
			game.removeEntity(this);
			game.removeEntity(other);
			game.notifyAlienKilled();
			used = true;
		}
    }
}

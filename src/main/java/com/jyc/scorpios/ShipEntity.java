package com.jyc.scorpios;

import java.io.IOException;

public class ShipEntity extends AbstractEntity {
    private static final int RIGHT_BORDER = 750;
    private static final int LEFT_BORDER = 10;
    private ScorpiosGame game;

    public ShipEntity(ScorpiosGame scorpiosGame, String ref, int x, int y) throws IOException {
        super(scorpiosGame.getSprite(ref), x, y);

        this.game = scorpiosGame;
    }

    @Override
    public void move(long delta) {
		if ((horizontalMovement < 0) && (x < LEFT_BORDER)) {
			return;
		}
		if ((horizontalMovement > 0) && (x > RIGHT_BORDER)) {
			return;
		}

		super.move(delta);
	}

    @Override
    public void collidedWith(AbstractEntity other) {
        if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
    }
}

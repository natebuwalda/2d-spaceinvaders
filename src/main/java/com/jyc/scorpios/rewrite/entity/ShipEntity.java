package com.jyc.scorpios.rewrite.entity;

import com.jyc.scorpios.ScorpiosGame;
import com.jyc.scorpios.rewrite.sprite.Sprite;

import java.io.IOException;

public class ShipEntity extends AbstractEntity {
    private static final int RIGHT_BORDER = 750;
    private static final int LEFT_BORDER = 10;

    public ShipEntity(Sprite sprite, int x, int y) throws IOException {
        super(sprite, x, y);
    }

    @Override
    public void move(long delta) {
        System.out.println("horizontalMovement = " + horizontalMovement);
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
        if (other instanceof GreenGnatEntity) {
//			game.notifyDeath();
		}
    }
}

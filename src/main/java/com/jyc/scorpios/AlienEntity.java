package com.jyc.scorpios;

import java.io.IOException;

public class AlienEntity extends AbstractEntity {
    private static final Integer DOWNWARD_MOVEMENT = 10;
    private static final Integer BOTTOM_BORDER = 570;
    private static final Integer RIGHT_BORDER = 750;
    private static final Integer LEFT_BORDER = 10;
    private ScorpiosGame game;
    private Sprite[] frames = new Sprite[4];
    private Long lastFrameChange = 0L;
    private Long frameDuration = 250L;
    private Integer frameNumber = 1;

    public AlienEntity(ScorpiosGame scorpiosGame, Integer x, Integer y) throws IOException {
        super(scorpiosGame.getSprite("Gnat_Green-F1.png"), x, y);

        frames[0] = scorpiosGame.getSprite("Gnat_Green-F1.png");
        frames[1] = scorpiosGame.getSprite("Gnat_Green-F2.png");
        frames[2] = scorpiosGame.getSprite("Gnat_Green-F3.png");
        frames[3] = scorpiosGame.getSprite("Gnat_Green-F4.png");

        this.game = scorpiosGame;
        Float moveSpeed = 75.0F;
        horizontalMovement = -moveSpeed;
    }

    public void move(long delta) {
        lastFrameChange += delta;

        if (lastFrameChange > frameDuration) {
            lastFrameChange = 0L;

            frameNumber++;
            if (frameNumber >= frames.length) {
                frameNumber = 0;
            }

            sprite = frames[frameNumber];
        }

        if ((horizontalMovement < 0) && (x < LEFT_BORDER)) {
            game.updateLogic();
        }

        if ((horizontalMovement > 0) && (x > RIGHT_BORDER)) {
            game.updateLogic();
        }

        super.move(delta);
    }

    public void doLogic() {
        horizontalMovement = -horizontalMovement;
        y += DOWNWARD_MOVEMENT;

        if (y > BOTTOM_BORDER) {
            game.notifyDeath();
        }
    }

    @Override
    public void collidedWith(AbstractEntity other) {
        //empty on purpose
    }
}

package com.jyc.scorpios;

import java.io.IOException;

public class AlienEntity extends AbstractEntity {
    private static final Integer DOWNWARD_MOVEMENT = 10;
    private static final Integer BOTTOM_BORDER = 570;
    private static final Integer RIGHT_BORDER = 750;
    private static final Integer LEFT_BORDER = 10;
    private Float moveSpeed = 75.0F;
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
        horizontalMovement = -moveSpeed;
    }

    public void move(long delta) {
        // since the move tells us how much time has passed
        // by we can use it to drive the animation, however
        // its the not the prettiest solution
        lastFrameChange += delta;

        // if we need to change the frame, update the frame number
        // and flip over the sprite in use
        if (lastFrameChange > frameDuration) {
            // reset our frame change time counter
            lastFrameChange = 0L;

            // update the frame
            frameNumber++;
            if (frameNumber >= frames.length) {
                frameNumber = 0;
            }

            sprite = frames[frameNumber];
        }

        // if we have reached the left hand side of the screen and
        // are moving left then request a logic update
        if ((horizontalMovement < 0) && (x < LEFT_BORDER)) {
            game.updateLogic();
        }
        // and vice vesa, if we have reached the right hand side of
        // the screen and are moving right, request a logic update
        if ((horizontalMovement > 0) && (x > RIGHT_BORDER)) {
            game.updateLogic();
        }

        // proceed with normal move
        super.move(delta);
    }

    /**
     * Update the game logic related to aliens
     */
    public void doLogic() {
        // swap over horizontal movement and move down the
        // screen a bit
        horizontalMovement = -horizontalMovement;
        y += DOWNWARD_MOVEMENT;

        // if we've reached the bottom of the screen then the player
        // dies
        if (y > BOTTOM_BORDER) {
            game.notifyDeath();
        }
    }

    /**
     * Notification that this alien has collided with another entity
     *
     * @param other The other entity
     */
    public void collidedWith(AbstractEntity other) {
        // collisions with aliens are handled elsewhere
    }
}

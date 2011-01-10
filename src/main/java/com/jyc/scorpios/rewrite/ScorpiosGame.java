package com.jyc.scorpios.rewrite;


import com.jyc.scorpios.*;
import com.jyc.scorpios.rewrite.entity.ShotEntity;
import com.jyc.scorpios.rewrite.entity.AbstractEntity;
import com.jyc.scorpios.rewrite.entity.GreenGnatEntity;
import com.jyc.scorpios.rewrite.entity.ShipEntity;
import com.jyc.scorpios.rewrite.sprite.*;
import com.jyc.scorpios.rewrite.sprite.Sprite;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glViewport;

public class ScorpiosGame {

    private static final String GOTYOU = "gotyou.gif";
    private static final String PRESSANYKEY = "pressanykey.gif";
    private static final String YOUWIN = "youwin.gif";
    private static final String SCORPIOS_VERSION = "Scorpios Alpha v0.2";
    private static final String SHIP = "ship.gif";
    private static final String GNAT_GREEN = "Gnat_Green-F1.png";
    private static final String SHOT = "shot.gif";
    private Integer width = 800;
    private Integer height = 600;
    private GameState gameState = GameState.instance();
    private GameCondition currentCondition;
    private SpriteCache spriteCache = new SpriteCache();
    private EntityCache entityCache = new EntityCache();
    private SoundManager soundManager = new SoundManager();
    private Sprite currentMessage;

    public void start() throws LWJGLException, IOException {
        setDisplayMode();
        Display.setTitle(SCORPIOS_VERSION);
        Display.setFullscreen(false);
        Display.create();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);

        soundManager.initialize(8);
//        SOUND_SHOT = soundManager.addSound("shot.wav");
//        SOUND_HIT = soundManager.addSound("hit.wav");
//        SOUND_START = soundManager.addSound("start.wav");
//        SOUND_WIN = soundManager.addSound("win.wav");
//        SOUND_LOOSE = soundManager.addSound("loose.wav");

        spriteCache.createSprite(GOTYOU);
        spriteCache.createSprite(PRESSANYKEY);
        spriteCache.createSprite(YOUWIN);
        spriteCache.createSprite(SHIP);
        spriteCache.createSprite(GNAT_GREEN);
        spriteCache.createSprite(SHOT);

        entityCache = new EntityCache();
        entityCache.addPlayerShipEntity(new ShipEntity(spriteCache.getSprite(SHIP), 370, 550));
        setupAliens();

        currentMessage = spriteCache.getSprite(PRESSANYKEY);
        currentCondition = GameCondition.RUNNING;
    }

    public void gameLoop() throws IOException {
        while (currentCondition == GameCondition.RUNNING) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            Display.sync(60);

            // update fps
            long delta = Timer.getTime() - gameState.lastLoopTime;
            gameState.lastLoopTime = Timer.getTime();
            gameState.lastFpsTime += delta;
            gameState.fps++;

            if (gameState.lastFpsTime >= 1000) {
                Display.setTitle(SCORPIOS_VERSION + " (FPS: " + gameState.fps + ")");
                gameState.lastFpsTime = 0L;
                gameState.fps = 0;
            }

            // move
            if (!gameState.waitingForKeyPress && !soundManager.isPlayingSound()) {
                for (AbstractEntity entity : entityCache.allEntities()) {
                    entity.move(delta);
                }
            }

            // render
            for (AbstractEntity entity : entityCache.allEntities()) {
                entity.draw();
            }

            // collision detection
            for (int p = 0; p < entityCache.entityCount(); p++) {
                for (int s = p + 1; s < entityCache.entityCount(); s++) {
                    AbstractEntity me = entityCache.allEntities().get(p);
                    AbstractEntity him = entityCache.allEntities().get(s);

                    if (me.collidesWith(him)) {
                        me.collidedWith(him);
                        him.collidedWith(me);
                    }
                }
            }

            // game logic
            if (gameState.logicRequiredThisLoop) {
                for (AbstractEntity entity : entityCache.allEntities()) {
                    entity.doLogic();
                }

                gameState.logicRequiredThisLoop = false;
            }

            // wait state
            if (gameState.waitingForKeyPress) {
                currentMessage.draw(325, 250);
            }


            // gather and record input
            boolean leftPressed = hasInput(Keyboard.KEY_LEFT);
            boolean rightPressed = hasInput(Keyboard.KEY_RIGHT);
            boolean firePressed = hasInput(Keyboard.KEY_SPACE);

            if (gameState.waitingForKeyPress || soundManager.isPlayingSound()) {
                if (!firePressed) {
                    gameState.fireHasBeenReleased = true;
                }
                if ((firePressed) && (gameState.fireHasBeenReleased) && !soundManager.isPlayingSound()) {
                    gameState.waitingForKeyPress = false;
                    gameState.fireHasBeenReleased = false;
                    setupAliens();
//                    soundManager.playSound(SOUND_START);
                }
            } else {
                if ((leftPressed) && (!rightPressed)) {
                    entityCache.playerShip().setHorizontalMovement(-gameState.playerMoveSpeed);
                } else if ((rightPressed) && (!leftPressed)) {
                    entityCache.playerShip().setHorizontalMovement(gameState.playerMoveSpeed);
                } else {
                    entityCache.playerShip().setHorizontalMovement(0);
                }

                if (firePressed) {
                    tryToFire();
                }
            }

            // cleanup dead entities
            entityCache.flushRemovals();

            Display.update();
        }
        Display.destroy();
    }

    public void stop() {
        currentCondition = GameCondition.STOPPED;
    }

    private void setupAliens() throws IOException {
        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        for (int row = 0; row < 5; row++) {
            for (int x = 0; x < 12; x++) {
                entityCache.addAlienEntity(new GreenGnatEntity(spriteCache.getSprite(GNAT_GREEN), 100 + (x * 50), (50) + row * 30));
            }
        }
    }

    private boolean setDisplayMode() {
        try {
            DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(width, height, -1, -1, -1, -1, 60, 60);

            org.lwjgl.util.Display.setDisplayMode(dm, new String[]{
                    "width=" + width,
                    "height=" + height,
                    "freq=" + 60,
                    "bpp=" + org.lwjgl.opengl.Display.getDisplayMode().getBitsPerPixel()
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to enter fullscreen, continuing in windowed mode");
        }

        return false;
    }

    private boolean hasInput(int direction) {
        switch (direction) {
            case Keyboard.KEY_LEFT:
                return Keyboard.isKeyDown(Keyboard.KEY_LEFT);
            case Keyboard.KEY_RIGHT:
                return Keyboard.isKeyDown(Keyboard.KEY_RIGHT);
            case Keyboard.KEY_SPACE:
                return Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        }
        return false;
    }

    private void tryToFire() throws IOException {
        if (System.currentTimeMillis() - gameState.shotLastFiredTime < gameState.playerFiringInterval)
            return;


        gameState.shotLastFiredTime = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(spriteCache.getSprite(SHOT),
                                         new Float(entityCache.playerShip().getX() + 10F).intValue(),
                                         new Float(entityCache.playerShip().getY() - 30F).intValue());
        if (entityCache.shotCount() < 5)
            entityCache.addShotEntity(shot);

//        soundManager.playEffect(SOUND_SHOT);
    }

//    public void notifyDeath() {
//        if (!gameState.waitingForKeyPress) {
//            soundManager.playSound(SOUND_LOOSE);
//        }
//        message = gotYou;
//        gameState.waitingForKeyPress = true;
//    }
//
//    public void notifyWin() {
//        message = youWin;
//        gameState.waitingForKeyPress = true;
//        soundManager.playSound(SOUND_WIN);
//    }
//
//    public void notifyAlienKilled() {
//        if (entityCache.alienCount() <= 1) {
//            notifyWin();
//        }
//
//        for (com.jyc.scorpios.entity.AbstractEntity entity : entityCache.allEntities()) {
//            if (entity instanceof com.jyc.scorpios.entity.GreenGnatEntity) {
//                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02f);
//            }
//        }
//
//        soundManager.playEffect(SOUND_HIT);
//    }
}

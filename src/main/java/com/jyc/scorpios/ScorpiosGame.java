package com.jyc.scorpios;

import com.jyc.scorpios.entity.AbstractEntity;
import com.jyc.scorpios.entity.GreenGnatEntity;
import com.jyc.scorpios.entity.ShipEntity;
import com.jyc.scorpios.entity.ShotEntity;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class ScorpiosGame implements Game {

    private static Boolean isApplication = false;
    private String windowTitle = "Scorpios (v0.1)";
    private Integer height = 600;
    private Integer width = 800;
    private Boolean fullscreen = false;
    private GameCondition currentCondition;
    private GameState gameState = GameState.instance();
    private TextureLoader textureLoader;
    private SoundManager soundManager;
    private EntityCache entityCache;
    private Sprite message;
    private Sprite pressAnyKey;
    private Sprite youWin;
    private Sprite gotYou;
    private Integer SOUND_SHOT;
    private Integer SOUND_HIT;
    private Integer SOUND_START;
    private Integer SOUND_WIN;
    private Integer SOUND_LOOSE;
    private Integer mouseX;

    public ScorpiosGame(boolean fullscreen) throws IOException {
        this.fullscreen = fullscreen;
        initialize();
    }

    @Override
    public void initialize() throws IOException {
        try {
            setDisplayMode();
            Display.setTitle(windowTitle);
            Display.setFullscreen(fullscreen);
            Display.create();

            if (isApplication) {
                Mouse.setGrabbed(true);
            }

            glSetup();
            textureLoader = new TextureLoader();
            entityCache = new EntityCache();
            soundManager = new SoundManager();
            soundSetup();
        } catch (LWJGLException le) {
            System.out.println("Game exiting - exception in initialization:");
            le.printStackTrace();
            currentCondition = GameCondition.ERROR;
            return;
        }

        gotYou = getSprite("gotyou.gif");
        pressAnyKey = getSprite("pressanykey.gif");
        youWin = getSprite("youwin.gif");

        message = pressAnyKey;

        //setup level
        entityCache.addPlayerShipEntity(new ShipEntity(this, "ship.gif", 370, 550));

        for (int row = 0; row < 5; row++) {
            for (int x = 0; x < 12; x++) {
                entityCache.addAlienEntity(new GreenGnatEntity(this, 100 + (x * 50), (50) + row * 30));
            }
        }
    }

    private void soundSetup() {
        soundManager.initialize(8);
        SOUND_SHOT = soundManager.addSound("shot.wav");
        SOUND_HIT = soundManager.addSound("hit.wav");
        SOUND_START = soundManager.addSound("start.wav");
        SOUND_WIN = soundManager.addSound("win.wav");
        SOUND_LOOSE = soundManager.addSound("loose.wav");
    }

    private void glSetup() {
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, width, height);
    }

    @Override
    public void gameLoop() throws IOException {
        while (currentCondition == GameCondition.RUNNING) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            frameRendering();
            Display.update();
        }
        soundManager.destroy();
        Display.destroy();
    }

    @Override
    public GameCondition condition() {
        return currentCondition;
    }

    @Override
    public void start() {
        currentCondition = GameCondition.RUNNING;
    }

    @Override
    public void stop() {
        currentCondition = GameCondition.STOPPED;
    }

    @Override
    public void execute() throws IOException {
        gameLoop();
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

    public void updateLogic() {
        gameState.logicRequiredThisLoop = true;
    }

    public void notifyDeath() {
        if (!gameState.waitingForKeyPress) {
            soundManager.playSound(SOUND_LOOSE);
        }
        message = gotYou;
        gameState.waitingForKeyPress = true;
    }

    public void notifyWin() {
        message = youWin;
        gameState.waitingForKeyPress = true;
        soundManager.playSound(SOUND_WIN);
    }

    public void notifyAlienKilled() {
        if (entityCache.alienCount() <= 1) {
            notifyWin();
        }

        for (AbstractEntity entity : entityCache.allEntities()) {
            if (entity instanceof GreenGnatEntity) {
                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02f);
            }
        }

        soundManager.playEffect(SOUND_HIT);
    }

    public void tryToFire() throws IOException {
        if (System.currentTimeMillis() - gameState.shotLastFiredTime < gameState.playerFiringInterval)
            return;


        gameState.shotLastFiredTime = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(this,
                                         new Float(entityCache.playerShip().getX() + 10F).intValue(),
                                         new Float(entityCache.playerShip().getY() - 30F).intValue());
        if (entityCache.shotCount() < 5)
            entityCache.addShotEntity(shot);

        soundManager.playEffect(SOUND_SHOT);
    }

    public void frameRendering() throws IOException {
        Display.sync(60);

        long delta = Timer.getTime() - gameState.lastLoopTime;
        gameState.lastLoopTime = Timer.getTime();
        gameState.lastFpsTime += delta;
        gameState.fps++;

        if (gameState.lastFpsTime >= 1000) {
            Display.setTitle(windowTitle + " (FPS: " + gameState.fps + ")");
            gameState.lastFpsTime = 0L;
            gameState.fps = 0;
        }

        if (!gameState.waitingForKeyPress && !soundManager.isPlayingSound()) {
            for (AbstractEntity entity : entityCache.allEntities()) {
                entity.move(delta);
            }
        }

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
        entityCache.flushRemovals();

        if (gameState.logicRequiredThisLoop) {
            for (AbstractEntity entity : entityCache.allEntities()) {
                entity.doLogic();
            }

            gameState.logicRequiredThisLoop = false;
        }

        if (gameState.waitingForKeyPress) {
            message.draw(325, 250);
        }

        entityCache.playerShip().setHorizontalMovement(0);
        mouseX = Mouse.getDX();

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
                entityCache = new EntityCache();
                entityCache.addPlayerShipEntity(new ShipEntity(this, "ship.gif", 370, 550));

                // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
                for (int row = 0; row < 5; row++) {
                    for (int x = 0; x < 12; x++) {
                        entityCache.addAlienEntity(new GreenGnatEntity(this, 100 + (x * 50), (50) + row * 30));
                    }
                }
                soundManager.playSound(SOUND_START);
            }
        } else {
            if ((leftPressed) && (!rightPressed)) {
                entityCache.playerShip().setHorizontalMovement(-gameState.playerMoveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
                entityCache.playerShip().setHorizontalMovement(gameState.playerMoveSpeed);
            }

            if (firePressed) {
                tryToFire();
            }
        }

        if ((Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) && isApplication) {
            currentCondition = GameCondition.STOPPED;
        }
    }

    private boolean hasInput(int direction) {
        switch (direction) {
            case Keyboard.KEY_LEFT:
                return Keyboard.isKeyDown(Keyboard.KEY_LEFT) || mouseX < 0;
            case Keyboard.KEY_RIGHT:
                return Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || mouseX > 0;
            case Keyboard.KEY_SPACE: return Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0);
        }
        return false;
    }

    public static void main(String argv[]) throws IOException {
        isApplication = true;
        System.out.println("Use -fullscreen for fullscreen mode");
        new ScorpiosGame((argv.length > 0 && "-fullscreen".equalsIgnoreCase(argv[0]))).execute();
        System.exit(0);
    }

    public Sprite getSprite(String ref) throws IOException {
        return new Sprite(textureLoader, ref);
    }

    public EntityCache getEntityCache() {
        return entityCache;
    }
}

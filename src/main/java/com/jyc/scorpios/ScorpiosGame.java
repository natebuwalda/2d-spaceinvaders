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
    private static Long timerTicksPerSecond = Sys.getTimerResolution();
    private static Boolean isApplication = false;
    private String windowTitle = "Scorpios (v0.1)";
    private Integer height = 600;
    private Integer width = 800;
    private Boolean fullscreen = false;
    private GameState currentState;
    private TextureLoader textureLoader;
    private SoundManager soundManager;
    private EntityCache entityCache;
    private Sprite message;
    private Sprite pressAnyKey;
    private Sprite youWin;
    private Sprite gotYou;
    private Float playerMoveSpeed = 300.0f;
    private Long shotLastFiredTime = 0L;
    private Long playerFiringInterval = 500L;
    private Boolean waitingForKeyPress = true;
    private Boolean logicRequiredThisLoop = false;
    private Long lastLoopTime = getTime();
    private Boolean fireHasBeenReleased = true;
    private Long lastFpsTime = 0L;
    private Integer fps = 0;
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
            currentState = GameState.ERROR;
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
        while (currentState == GameState.RUNNING) {
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
    public GameState state() {
        return currentState;
    }

    @Override
    public void start() {
        currentState = GameState.RUNNING;
    }

    @Override
    public void stop() {
        currentState = GameState.STOPPED;
    }

    @Override
    public void execute() throws IOException {
        gameLoop();
    }

    public static long getTime() {
        return (Sys.getTime() * 1000) / timerTicksPerSecond;
    }

    public static void sleep(long duration) {
        try {
            Thread.sleep((duration * timerTicksPerSecond) / 1000);
        } catch (InterruptedException inte) {
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

    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    public void notifyDeath() {
        if (!waitingForKeyPress) {
            soundManager.playSound(SOUND_LOOSE);
        }
        message = gotYou;
        waitingForKeyPress = true;
    }

    public void notifyWin() {
        message = youWin;
        waitingForKeyPress = true;
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
        if (System.currentTimeMillis() - shotLastFiredTime < playerFiringInterval)
            return;


        shotLastFiredTime = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(this,
                                         new Float(entityCache.playerShip().getX() + 10F).intValue(),
                                         new Float(entityCache.playerShip().getY() - 30F).intValue());
        if (entityCache.shotCount() < 5)
            entityCache.addShotEntity(shot);

        soundManager.playEffect(SOUND_SHOT);
    }

    public void frameRendering() throws IOException {
        Display.sync(60);

        long delta = getTime() - lastLoopTime;
        lastLoopTime = getTime();
        lastFpsTime += delta;
        fps++;

        if (lastFpsTime >= 1000) {
            Display.setTitle(windowTitle + " (FPS: " + fps + ")");
            lastFpsTime = 0L;
            fps = 0;
        }

        if (!waitingForKeyPress && !soundManager.isPlayingSound()) {
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

        if (logicRequiredThisLoop) {
            for (AbstractEntity entity : entityCache.allEntities()) {
                entity.doLogic();
            }

            logicRequiredThisLoop = false;
        }

        if (waitingForKeyPress) {
            message.draw(325, 250);
        }

        entityCache.playerShip().setHorizontalMovement(0);
        mouseX = Mouse.getDX();

        boolean leftPressed = hasInput(Keyboard.KEY_LEFT);
        boolean rightPressed = hasInput(Keyboard.KEY_RIGHT);
        boolean firePressed = hasInput(Keyboard.KEY_SPACE);

        if (waitingForKeyPress || soundManager.isPlayingSound()) {
            if (!firePressed) {
                fireHasBeenReleased = true;
            }
            if ((firePressed) && (fireHasBeenReleased) && !soundManager.isPlayingSound()) {
                waitingForKeyPress = false;
                fireHasBeenReleased = false;
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
                entityCache.playerShip().setHorizontalMovement(-playerMoveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
                entityCache.playerShip().setHorizontalMovement(playerMoveSpeed);
            }

            if (firePressed) {
                tryToFire();
            }
        }

        if ((Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) && isApplication) {
            currentState = GameState.STOPPED;
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

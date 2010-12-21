package com.jyc.scorpios;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ScorpiosGame implements Game {
    private static Long timerTicksPerSecond = Sys.getTimerResolution();
    private static Boolean isApplication = false;

    private String WINDOW_TITLE = "Scorpios (v0.1)";
    private Integer height = 600;
    private Integer width = 800;
    private Boolean fullscreen = false;
    private TextureLoader textureLoader;
    private SoundManager soundManager;
    private List<AbstractEntity> entities = new ArrayList<AbstractEntity>();
    private List<AbstractEntity> removeList = new ArrayList<AbstractEntity>();
    private List<ShotEntity> shots = new ArrayList<ShotEntity>();
    private List<AlienEntity> aliens = new ArrayList<AlienEntity>();
    private ShipEntity ship;
    private Sprite message;
    private Sprite pressAnyKey;
    private Sprite youWin;
    private Sprite gotYou;
    private GameState currentState;
    private Integer shotIndex = 0;
    private Float playerMoveSpeed = 300.0f;
    private Long shotLastFiredTime = 0L;
    private Long playerFiringInterval = 500L;
    private Boolean waitingForKeyPress = true;
    private Boolean logicRequiredThisLoop = false;
    private Long lastLoopTime = getTime();
    private Boolean fireHasBeenReleased = true;
    private Long lastFpsTime = 0L;
    private Integer fps = 0;
    private Integer alienCount;
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
        // initialize the window beforehand
        try {
            setDisplayMode();
            Display.setTitle(WINDOW_TITLE);
            Display.setFullscreen(fullscreen);
            Display.create();

            // grab the mouse, dont want that hideous cursor when we're playing!
            if (isApplication) {
                Mouse.setGrabbed(true);
            }

            // enable textures since we're going to use these for our sprites
            glEnable(GL_TEXTURE_2D);

            // disable the OpenGL depth test since we're rendering 2D graphics
            glDisable(GL_DEPTH_TEST);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            glOrtho(0, width, height, 0, -1, 1);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glViewport(0, 0, width, height);

            textureLoader = new TextureLoader();

            // create our sound manager, and initialize it with 7 channels
            // 1 channel for sounds, 6 for effects - this should be enough
            // since we have a most 4 shots on screen at any one time, which leaves
            // us with 2 channels for explosions.
            soundManager = new SoundManager();
            soundManager.initialize(8);

            // load our sound data
            SOUND_SHOT = soundManager.addSound("shot.wav");
            SOUND_HIT = soundManager.addSound("hit.wav");
            SOUND_START = soundManager.addSound("start.wav");
            SOUND_WIN = soundManager.addSound("win.wav");
            SOUND_LOOSE = soundManager.addSound("loose.wav");
        } catch (LWJGLException le) {
            System.out.println("Game exiting - exception in initialization:");
            le.printStackTrace();
            currentState = GameState.ERROR;
            return;
        }

        // get our sprites
        gotYou = getSprite("gotyou.gif");
        pressAnyKey = getSprite("pressanykey.gif");
        youWin = getSprite("youwin.gif");

        message = pressAnyKey;

        // setup 5 shots
        for (int i = 0; i < 5; i++) {
            shots.add(new ShotEntity(this, "shot.gif", 0, 0));
        }

        // setup the initial game state
        startGame();
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

    private void startGame() throws IOException {
        entities.clear();
        initEntities();
    }

    private void initEntities() throws IOException {
        // create the player ship and place it roughly in the center of the screen
        ship = new ShipEntity(this, "ship.gif", 370, 550);
        entities.add(ship);

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        alienCount = 0;
        for (int row = 0; row < 5; row++) {
            for (int x = 0; x < 12; x++) {
                AlienEntity alien = new AlienEntity(this, 100 + (x * 50), (50) + row * 30);
                entities.add(alien);
                alienCount++;
            }
        }
    }

    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    public void removeEntity(AbstractEntity entity) {
        removeList.add(entity);
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
        alienCount--;

        if (alienCount == 0) {
            notifyWin();
        }

        for (AbstractEntity entity : entities) {
            if (entity instanceof AlienEntity) {
                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02f);
            }
        }

        soundManager.playEffect(SOUND_HIT);
    }

    public void tryToFire() {
        if (System.currentTimeMillis() - shotLastFiredTime < playerFiringInterval) {
            return;
        }

        shotLastFiredTime = System.currentTimeMillis();
        ShotEntity shot = shots.get(shotIndex++ % shots.size());
        shot.reinitialize(ship.getX() + 10F, ship.getY() - 30F);
        entities.add(shot);

        soundManager.playEffect(SOUND_SHOT);
    }

    public void frameRendering() throws IOException {
        Display.sync(60);

        long delta = getTime() - lastLoopTime;
        lastLoopTime = getTime();
        lastFpsTime += delta;
        fps++;

        if (lastFpsTime >= 1000) {
            Display.setTitle(WINDOW_TITLE + " (FPS: " + fps + ")");
            lastFpsTime = 0L;
            fps = 0;
        }

        if (!waitingForKeyPress && !soundManager.isPlayingSound()) {
            for (AbstractEntity entity : entities) {
                entity.move(delta);
            }
        }

        for (AbstractEntity entity : entities) {
            entity.draw();
        }

        // collision detection
        for (int p = 0; p < entities.size(); p++) {
            for (int s = p + 1; s < entities.size(); s++) {
                AbstractEntity me = entities.get(p);
                AbstractEntity him = entities.get(s);

                if (me.collidesWith(him)) {
                    me.collidedWith(him);
                    him.collidedWith(me);
                }
            }
        }

        entities.removeAll(removeList);
        removeList.clear();

        if (logicRequiredThisLoop) {
            for (AbstractEntity entity : entities) {
                entity.doLogic();
            }

            logicRequiredThisLoop = false;
        }

        if (waitingForKeyPress) {
            message.draw(325, 250);
        }

        ship.setHorizontalMovement(0);
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
                startGame();
                soundManager.playSound(SOUND_START);
            }
        } else {
            if ((leftPressed) && (!rightPressed)) {
                ship.setHorizontalMovement(-playerMoveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
                ship.setHorizontalMovement(playerMoveSpeed);
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
                return
                        Keyboard.isKeyDown(Keyboard.KEY_LEFT) ||
                                mouseX < 0;

            case Keyboard.KEY_RIGHT:
                return
                        Keyboard.isKeyDown(Keyboard.KEY_RIGHT) ||
                                mouseX > 0;

            case Keyboard.KEY_SPACE:
                return
                        Keyboard.isKeyDown(Keyboard.KEY_SPACE) ||
                                Mouse.isButtonDown(0);
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

}

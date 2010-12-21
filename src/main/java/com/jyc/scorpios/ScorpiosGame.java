package com.jyc.scorpios;

import com.jyc.game.Game;
import com.jyc.game.GameState;
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
    public static Boolean gameRunning = true;
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
            gameRunning = false;
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
        while (gameRunning) {
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            // let subsystem paint
            frameRendering();

            // update window contents
            Display.update();
        }

        // clean up
        soundManager.destroy();
        Display.destroy();
    }

    @Override
    public GameState state() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute() throws IOException {
        gameLoop();
    }

    /**
     * Get the high resolution time in milliseconds
     *
     * @return The high resolution time in milliseconds
     */
    public static long getTime() {
        // we get the "timer ticks" from the high resolution timer
        // multiply by 1000 so our end result is in milliseconds
        // then divide by the number of ticks in a second giving
        // us a nice clear time in milliseconds
        return (Sys.getTime() * 1000) / timerTicksPerSecond;
    }


    /**
     * Sleep for a fixed number of milliseconds.
     *
     * @param duration The amount of time in milliseconds to sleep for
     */
    public static void sleep(long duration) {
        try {
            Thread.sleep((duration * timerTicksPerSecond) / 1000);
        } catch (InterruptedException inte) {
        }
    }

    /**
     * Sets the display mode for fullscreen mode
     */
    private boolean setDisplayMode() {
        try {
            // get modes
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

    /**
     * Start a fresh game, this should clear out any old data and
     * create a new set.
     */
    private void startGame() throws IOException {
        // clear out any existing entities and intialise a new set
        entities.clear();
        initEntities();
    }

    /**
     * Initialise the starting state of the entities (ship and aliens). Each
     * entitiy will be added to the overall list of entities in the game.
     */
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

    /**
     * Notification from a game entity that the logic of the game
     * should be run at the next opportunity (normally as a result of some
     * game event)
     */
    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    /**
     * Remove an entity from the game. The entity removed will
     * no longer move or be drawn.
     *
     * @param entity The entity that should be removed
     */
    public void removeEntity(AbstractEntity entity) {
        removeList.add(entity);
    }

    /**
     * Notification that the player has died.
     */
    public void notifyDeath() {
        if (!waitingForKeyPress) {
            soundManager.playSound(SOUND_LOOSE);
        }
        message = gotYou;
        waitingForKeyPress = true;
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        message = youWin;
        waitingForKeyPress = true;
        soundManager.playSound(SOUND_WIN);
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        alienCount--;

        if (alienCount == 0) {
            notifyWin();
        }

        // if there are still some aliens left then they all need to get faster, so
        // speed up all the existing aliens
        for (AbstractEntity entity : entities) {
            if (entity instanceof AlienEntity) {
                // speed up by 2%
                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02f);
            }
        }

        soundManager.playEffect(SOUND_HIT);
    }

    /**
     * Attempt to fire a shot from the player. Its called "try"
     * since we must first check that the player can fire at this
     * point, i.e. has he/she waited long enough between shots
     */
    public void tryToFire() {
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - shotLastFiredTime < playerFiringInterval) {
            return;
        }

        // if we waited long enough, create the shot entity, and record the time.
        shotLastFiredTime = System.currentTimeMillis();
        ShotEntity shot = shots.get(shotIndex++ % shots.size());
        shot.reinitialize(ship.getX() + 10F, ship.getY() - 30F);
        entities.add(shot);

        soundManager.playEffect(SOUND_SHOT);
    }

    /**
     * Notification that a frame is being rendered. Responsible for
     * running game logic and rendering the scene.
     */
    public void frameRendering() throws IOException {
        //SystemTimer.sleep(lastLoopTime+10-SystemTimer.getTime());
        Display.sync(60);

        // work out how long its been since the last update, this
        // will be used to calculate how far the entities should
        // move this loop
        long delta = getTime() - lastLoopTime;
        lastLoopTime = getTime();
        lastFpsTime += delta;
        fps++;

        // update our FPS counter if a second has passed
        if (lastFpsTime >= 1000) {
            Display.setTitle(WINDOW_TITLE + " (FPS: " + fps + ")");
            lastFpsTime = 0L;
            fps = 0;
        }

        // cycle round asking each entity to move itself
        if (!waitingForKeyPress && !soundManager.isPlayingSound()) {
            for (AbstractEntity entity : entities) {
                entity.move(delta);
            }
        }

        // cycle round drawing all the entities we have in the game
        for (AbstractEntity entity : entities) {
            entity.draw();
        }

        // brute force collisions, compare every entity against
        // every other entity. If any of them collide notify
        // both entities that the collision has occured
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

        // remove any entity that has been marked for clear up
        entities.removeAll(removeList);
        removeList.clear();

        // if a game event has indicated that game logic should
        // be resolved, cycle round every entity requesting that
        // their personal logic should be considered.
        if (logicRequiredThisLoop) {
            for (AbstractEntity entity : entities) {
                entity.doLogic();
            }

            logicRequiredThisLoop = false;
        }

        // if we're waiting for an "any key" press then draw the
        // current message
        if (waitingForKeyPress) {
            message.draw(325, 250);
        }

        // resolve the movemfent of the ship. First assume the ship
        // isn't moving. If either cursor key is pressed then
        // update the movement appropraitely
        ship.setHorizontalMovement(0);

        // get mouse movement on x axis. We need to get it now, since
        // we can only call getDX ONCE! - secondary calls will yield 0, since
        // there haven't been any movement since last call.
        mouseX = Mouse.getDX();

        // we delegate input checking to submethod since we want to check
        // for keyboard, mouse & controller
        boolean leftPressed = hasInput(Keyboard.KEY_LEFT);
        boolean rightPressed = hasInput(Keyboard.KEY_RIGHT);
        boolean firePressed = hasInput(Keyboard.KEY_SPACE);

        if (!waitingForKeyPress && !soundManager.isPlayingSound()) {
            if ((leftPressed) && (!rightPressed)) {
                ship.setHorizontalMovement(-playerMoveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
                ship.setHorizontalMovement(playerMoveSpeed);
            }

            // if we're pressing fire, attempt to fire
            if (firePressed) {
                tryToFire();
            }
        } else {
            if (!firePressed) {
                fireHasBeenReleased = true;
            }
            if ((firePressed) && (fireHasBeenReleased) && !soundManager.isPlayingSound()) {
                waitingForKeyPress = false;
                fireHasBeenReleased = false;
                startGame();
                soundManager.playSound(SOUND_START);
            }
        }

        // if escape has been pressed, stop the game
        if ((Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) && isApplication) {
            gameRunning = false;
        }
    }

    /**
     * @param direction
     * @return
     */
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

    /**
     * The entry point into the game. We'll simply create an
     * instance of class which will start the display and game
     * loop.
     *
     * @param argv The arguments that are passed into our game
     */
    public static void main(String argv[]) throws IOException {
        isApplication = true;
        System.out.println("Use -fullscreen for fullscreen mode");
        new ScorpiosGame((argv.length > 0 && "-fullscreen".equalsIgnoreCase(argv[0]))).execute();
        System.exit(0);
    }

    /**
     * Create or get a sprite which displays the image that is pointed
     * to in the classpath by "ref"
     *
     * @param ref A reference to the image to load
     * @return A sprite that can be drawn onto the current graphics context.
     */
    public Sprite getSprite(String ref) throws IOException {
        return new Sprite(textureLoader, ref);
    }

    public static void gameRunning(Boolean gameRunning) {
        ScorpiosGame.gameRunning = gameRunning;
    }
}

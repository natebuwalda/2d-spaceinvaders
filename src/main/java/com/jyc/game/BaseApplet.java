package com.jyc.game;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.applet.Applet;
import java.awt.*;

public class BaseApplet extends Applet {

    private Canvas parentDisplay;
    private Thread gameThread;
    private Game game;

	public void init() {
        try {
            parentDisplay = new Canvas() {

                @Override
                public void addNotify() {
                    super.addNotify();
                    startLWJGL();
                }

                @Override
                public void removeNotify() {
                    stopLWJGL();
                    super.removeNotify();
                }
            };
            parentDisplay.setSize(getWidth(), getHeight());
            parentDisplay.setFocusable(true);
            parentDisplay.requestFocus();
            parentDisplay.setIgnoreRepaint(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create display");
        }
        add(parentDisplay);
        setLayout(new BorderLayout());
        setVisible(true);
	}

	public void start() {

	}

	public void stop() {

	}

    public void startLWJGL() {
        gameThread = new Thread() {
            @Override
            public void run() {
                try {
                    Display.setParent(parentDisplay);
                    Display.create();
                    initGL();
                } catch (LWJGLException e) {
                        e.printStackTrace();
                }
                game.initialize();
                game.start();
                game.execute();
                while(GameState.RUNNING == game.state()) {
                    game.gameLoop();
                }
            }
        };
        gameThread.start();
	}

	private void stopLWJGL() {
        game.stop();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	public void destroy() {
        remove(parentDisplay);
        super.destroy();
	}

    public void initGL() {

    }

    public Canvas getParentDisplay() {
        return parentDisplay;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}

package com.jyc.scorpios;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.applet.Applet;
import java.awt.*;
import java.io.IOException;

public class ScorpiosApplet extends Applet {

    private Canvas parentDisplay;
    private Thread gameThread;
    private ScorpiosGame game;

    public Canvas getParentDisplay() {
        return parentDisplay;
    }

    public void startLWJGL() {
        gameThread = new Thread() {
            public void run() {

                try {
                    Display.setParent(parentDisplay);
                    game = new ScorpiosGame(false);
                    game.start();
                    game.execute();
                } catch (LWJGLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

    public void start() {

    }

    public void stop() {

    }

    public void destroy() {
        remove(parentDisplay);
        super.destroy();
        System.out.println("Clear up");
    }

    public void init() {
        setLayout(new BorderLayout());
        try {
            parentDisplay = new Canvas() {
                public void addNotify() {
                    super.addNotify();
                    startLWJGL();
                }

                public void removeNotify() {
                    stopLWJGL();
                    super.removeNotify();
                }
            };
            parentDisplay.setSize(getWidth(), getHeight());
            add(parentDisplay);
            parentDisplay.setFocusable(true);
            parentDisplay.requestFocus();
            parentDisplay.setIgnoreRepaint(true);
            setVisible(true);
        } catch (Exception e) {
            System.err.println(e);
            throw new RuntimeException("Unable to create display");
        }
    }
}

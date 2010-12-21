package com.jyc.scorpios;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import java.applet.Applet;
import java.awt.*;
import java.io.IOException;

public class ScorpiosApplet extends Applet {

    Canvas display_parent;
    Thread gameThread;
    ScorpiosGame game;

    public void startLWJGL() {
        gameThread = new Thread() {
            public void run() {

                try {
                    Display.setParent(display_parent);
                    game = new ScorpiosGame(false);
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
        game.gameRunning = false;
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
        remove(display_parent);
        super.destroy();
        System.out.println("Clear up");
    }

    public void init() {
        setLayout(new BorderLayout());
        try {
            display_parent = new Canvas() {
                public void addNotify() {
                    super.addNotify();
                    startLWJGL();
                }

                public void removeNotify() {
                    stopLWJGL();
                    super.removeNotify();
                }
            };
            display_parent.setSize(getWidth(), getHeight());
            add(display_parent);
            display_parent.setFocusable(true);
            display_parent.requestFocus();
            display_parent.setIgnoreRepaint(true);
            setVisible(true);
        } catch (Exception e) {
            System.err.println(e);
            throw new RuntimeException("Unable to create display");
        }
    }
}

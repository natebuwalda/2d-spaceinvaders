package com.jyc.game;

public interface Game {

    void initialize();
    void gameLoop();
    GameState state();
    void start();
    void stop();
    void execute();
}

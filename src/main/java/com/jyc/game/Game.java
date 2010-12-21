package com.jyc.game;

import java.io.IOException;

public interface Game {

    void initialize() throws IOException;
    void gameLoop() throws IOException;
    GameState state();
    void start();
    void stop();
    void execute() throws IOException;
}

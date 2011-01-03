package com.jyc.scorpios;

import java.io.IOException;

public interface Game {

    void initialize() throws IOException;
    void gameLoop() throws IOException;
    GameCondition condition();
    void start();
    void stop();
    void execute() throws IOException;
}

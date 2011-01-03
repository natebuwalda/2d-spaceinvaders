package com.jyc.scorpios;

public class GameState {

    private static GameState thisState;
    public Long shotLastFiredTime = 0L;
    public Long playerFiringInterval = 500L;
    public Boolean waitingForKeyPress = true;
    public Boolean logicRequiredThisLoop = false;
    public Long lastLoopTime = Timer.getTime();
    public Boolean fireHasBeenReleased = true;
    public Long lastFpsTime = 0L;
    public Integer fps = 0;
    public Float playerMoveSpeed = 300.0f;

    private GameState() {
        // this is a singleton (a destructable one though)
    }

    public static GameState instance() {
        if (thisState == null) {
            thisState = new GameState();
        }
        return thisState;
    }

    public static void destroy() {
        thisState = null;
    }
}

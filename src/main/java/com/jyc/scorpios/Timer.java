package com.jyc.scorpios;

import org.lwjgl.Sys;

public class Timer {
    private static Long timerTicksPerSecond = Sys.getTimerResolution();

    public static long getTime() {
        return (Sys.getTime() * 1000) / timerTicksPerSecond;
    }

    public static void sleep(long duration) {
        try {
            Thread.sleep((duration * timerTicksPerSecond) / 1000);
        } catch (InterruptedException inte) {
            // allow the interruption to occur
        }
    }
}

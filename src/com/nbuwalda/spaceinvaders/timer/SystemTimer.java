package com.nbuwalda.spaceinvaders.timer;

import java.util.ArrayList;
import java.util.List;

public class SystemTimer {

	public static List<Long> frameTimes = new ArrayList<Long>();
	
	public static void addFrameTime(Long loopTime) {
		if (frameTimes.size() == 5) {
			frameTimes.remove(0);
		} else {
			frameTimes.add(loopTime);
		}
	}
	
	public static long getAverageFrameTime() {
		long sumOfFrameTimes = 0;
		for (Long frameTime : frameTimes) {
			sumOfFrameTimes += frameTime;
		}
	
		long averageFrameTime = sumOfFrameTimes / 5;
		if (averageFrameTime < 10) {
			averageFrameTime = 10;
		}
		return averageFrameTime;
	}
	
	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
			System.err.println("Unable to sleep");
			e.printStackTrace();
			System.exit(0);
		}
	}
}

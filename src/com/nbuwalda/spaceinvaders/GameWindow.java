package com.nbuwalda.spaceinvaders;

public interface GameWindow {

	void setTitle(String title);
	void setResolution(int x, int y);
	void startRendering();
	void setGameWindowCallback(GameWindowCallback callback);
	boolean isKeyPressed(int keyCode);
}

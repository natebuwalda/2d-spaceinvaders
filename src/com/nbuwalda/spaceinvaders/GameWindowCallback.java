package com.nbuwalda.spaceinvaders;


public interface GameWindowCallback {

	void initialise();
	void frameRendering();
	void windowClosed();
}
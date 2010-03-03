package com.nbuwalda.game.state;

// a simple interface based off the XNA Game class for controlling the flow of a game
public interface GameStateController {

	void initializeGame();
	void loadContent();
	void unloadContent();
	void update(Long gameTime);
	void draw(Long gameTime);
	void run();

}
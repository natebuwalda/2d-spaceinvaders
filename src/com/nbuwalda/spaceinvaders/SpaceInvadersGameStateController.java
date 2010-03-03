package com.nbuwalda.spaceinvaders;

import com.nbuwalda.game.resources.ResourceFactory;
import com.nbuwalda.game.state.GameStateController;

public class SpaceInvadersGameStateController implements GameStateController {

	private int renderingType;
	private GameWindow gameWindow;

	public SpaceInvadersGameStateController() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#initializeGame()
	 */
	public void initializeGame() {
//		ResourceFactory
		this.gameWindow = ResourceFactory.get().getGameWindow();
	}
	
	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#loadContent()
	 */
	public void loadContent() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#unloadContent()
	 */
	public void unloadContent() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#update(java.lang.Long)
	 */
	public void update(Long gameTime) {
		
	}
	
	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#draw(java.lang.Long)
	 */
	public void draw(Long gameTime) {
		
	}
	
	/* (non-Javadoc)
	 * @see com.nbuwalda.spaceinvaders.GameStateController#run()
	 */
	public void run() {
		
	}

	public void setRenderingType(int renderingType) {
		this.renderingType = renderingType;
	}

	public GameWindow getGameWindow() {
		return this.gameWindow;
	}
}

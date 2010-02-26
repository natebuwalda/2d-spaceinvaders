package com.nbuwalda.spaceinvaders.resources;

import com.nbuwalda.spaceinvaders.GameWindow;
import com.nbuwalda.spaceinvaders.Java2DGameWindow;

public class ResourceFactory {

	private static final ResourceFactory single = new ResourceFactory();
	
	public static ResourceFactory getFactory() {
		return single;
	}

	public static final int JAVA2D = 1;
	public static final int OPENGL_JOGL = 2;

	private int renderingType = JAVA2D;
	private GameWindow window;

	private ResourceFactory() {
		
	}
	
	public Sprite createSprite(String imagePath) {
		if (window == null) {
			throw new RuntimeException("GameWindow not intialized");
		}
		
		switch (renderingType) {
			case JAVA2D:
				return Java2DSpriteStore.getStore().getSprite((Java2DGameWindow)window, imagePath);
		}
		return null;
	}

	public GameWindow getGameWindow() {
		if (window == null) {
			switch (renderingType) {
				case JAVA2D:
				{
					window = new Java2DGameWindow();
					break;
 				}
//				case OPENGL_JOGL:
//				{
//					window = new JoglGameWindow();
//					break;
//				}
				default:
					window = new Java2DGameWindow();
			}
		}

		return window;
	}
	
	public void setRenderingType(int type) {
		this.renderingType = type;
	}
}

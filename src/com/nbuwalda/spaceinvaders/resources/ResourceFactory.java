package com.nbuwalda.spaceinvaders.resources;

import com.nbuwalda.spaceinvaders.GameWindow;
import com.nbuwalda.spaceinvaders.resources.java2d.Java2DGameWindow;
import com.nbuwalda.spaceinvaders.resources.java2d.Java2DSpriteStore;
import com.nbuwalda.spaceinvaders.resources.lwjgl.LWJGLGameWindow;
import com.nbuwalda.spaceinvaders.resources.lwjgl.LWJGLSprite;

public class ResourceFactory {
	public static final int JAVA2D = 1;
	public static final int OPENGL_JOGL = 2;
	public static final int OPENGL_LWJGL = 3;

	private int renderingType = JAVA2D;
	private GameWindow window;

	private static final ResourceFactory single = new ResourceFactory();

	public static ResourceFactory get() {
		return single;
	}

	private ResourceFactory() {
	}

	public void setRenderingType(int renderingType) {
		if ((renderingType != JAVA2D) && (renderingType != OPENGL_JOGL)
				&& (renderingType != OPENGL_LWJGL)) {
			throw new RuntimeException("Unknown rendering type specified: "
					+ renderingType);
		}

		if (window != null) {
			throw new RuntimeException(
					"Attempt to change rendering method at game runtime");
		}

		this.renderingType = renderingType;
	}

	public GameWindow getGameWindow() {
		if (window == null) {
			switch (renderingType) {
			case JAVA2D: {
				window = new Java2DGameWindow();
				break;
			}
			case OPENGL_JOGL: {
				throw new RuntimeException("JOGL mode not supported in this version");
			}
			case OPENGL_LWJGL: {
				window = new LWJGLGameWindow();
				break;
			}
			}
		}

		return window;
	}

	public Sprite getSprite(String ref) {
		if (window == null) {
			throw new RuntimeException("Attempt to retrieve sprite before game window was created");
		}

		switch (renderingType) {
		case JAVA2D: {
			return Java2DSpriteStore.get().getSprite((Java2DGameWindow) window,
					ref);
		}
		case OPENGL_JOGL: {
			throw new RuntimeException("JOGL mode not supported in this version");
		}
		case OPENGL_LWJGL: {
			return new LWJGLSprite((LWJGLGameWindow) window, ref);
		}
		}

		throw new RuntimeException("Unknown rendering type: " + renderingType);
	}
}
package com.nbuwalda.spaceinvaders.resources;

import java.awt.Image;

import com.nbuwalda.spaceinvaders.Java2DGameWindow;

public class Java2DSprite implements Sprite {

	private Java2DGameWindow window;
	private Image image;

	public Java2DSprite(Java2DGameWindow window, Image image) {
		this.window = window;
		this.image = image;
	}
	
	@Override
	public void draw(int x, int y) {
		window.getGraphics().drawImage(image, x, y, null);
	}

	@Override
	public int getHeight() {
		return image.getHeight(null);
	}

	@Override
	public int getWidth() {
		return image.getWidth(null);
	}

}

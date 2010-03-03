package com.nbuwalda.spaceinvaders.resources.java2d;

import java.awt.Image;

import com.nbuwalda.spaceinvaders.resources.Sprite;

public class Java2DSprite implements Sprite {
	private Image image;
	private Java2DGameWindow window;
	
	public Java2DSprite(Java2DGameWindow window,Image image) {
		this.image = image;
		this.window = window;
	}
	
	public int getWidth() {
		return image.getWidth(null);
	}

	public int getHeight() {
		return image.getHeight(null);
	}
	
	public void draw(int x,int y) {
		window.getDrawGraphics().drawImage(image,x,y,null);
	}
}
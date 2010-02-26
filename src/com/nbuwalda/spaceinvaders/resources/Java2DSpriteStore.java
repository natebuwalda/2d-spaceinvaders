package com.nbuwalda.spaceinvaders.resources;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.nbuwalda.spaceinvaders.Java2DGameWindow;

public class Java2DSpriteStore {

	private static Java2DSpriteStore single = new Java2DSpriteStore();
	private static Map<String, Sprite> sprites = new HashMap<String, Sprite>();
	
	private Java2DSpriteStore() {
		
	}

	public static Java2DSpriteStore getStore() {
		return single;
	}
	
	public static void addSprite(String imagePath, Sprite sprite) {
		sprites.put(imagePath, sprite);
	}
	
	public Sprite getSprite(Java2DGameWindow window, String imagePath) {
		if (sprites.get(imagePath) != null) {
			return (Sprite) sprites.get(imagePath);
		}
		
		BufferedImage sourceImage = null;
		
		try {
			URL url = this.getClass().getClassLoader().getResource(imagePath);
			
			if (url == null) {
				throw new RuntimeException("Can't find ref: " + imagePath);
			}
			
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load: " + imagePath);
		}
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
		
		image.getGraphics().drawImage(sourceImage, 0, 0, null);
		
		Sprite sprite = new Java2DSprite(window, image);
		sprites.put(imagePath, sprite);
		
		return sprite;
	}
}

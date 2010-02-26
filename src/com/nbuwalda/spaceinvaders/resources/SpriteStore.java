package com.nbuwalda.spaceinvaders.resources;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteStore {

	private static SpriteStore single = new SpriteStore();
	private Map<String, OldSprite> sprites = new HashMap<String, OldSprite>();
	
	public static SpriteStore getStore() {
		return single;
	}
	
	public OldSprite getSprite(String ref) {
		URL url = this.getClass().getClassLoader().getResource(ref);
		OldSprite sprite = null;
		
		if (sprites.get(ref) == null) {
			try {
				BufferedImage sourceImage = ImageIO.read(url);
				GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
				Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
				image.getGraphics().drawImage(sourceImage, 0, 0, null);
	
				sprite = new OldSprite(image);
				sprites.put(ref, sprite);
			} catch (Exception e) {
				System.err.println("ref = " + ref);
				System.err.println("url = " + url);
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			sprite = sprites.get(ref);
		}
		
		return sprite;
	}
}

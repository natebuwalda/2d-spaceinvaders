package com.nbuwalda.spaceinvaders.resources.java2d;

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

import com.nbuwalda.game.resources.Sprite;

public class Java2DSpriteStore {

	private static Java2DSpriteStore single = new Java2DSpriteStore();
	private Map<String, Sprite> sprites = new HashMap<String, Sprite> ();

	public static Java2DSpriteStore get() {
		return single;
	}

	public Sprite getSprite(Java2DGameWindow window, String ref) {
		if (sprites.get(ref) != null) {
			return (Sprite) sprites.get(ref);
		}

		BufferedImage sourceImage = null;

		try {
			URL url = this.getClass().getClassLoader().getResource(ref);

			if (url == null) {
				fail("Can't find ref: " + ref);
			}

			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			fail("Failed to load: " + ref);
		}

		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);

		image.getGraphics().drawImage(sourceImage, 0, 0, null);

		Sprite sprite = new Java2DSprite(window, image);
		sprites.put(ref, sprite);

		return sprite;
	}

	private void fail(String message) {
		System.err.println(message);
		System.exit(0);
	}
}
package com.nbuwalda.spaceinvaders.resources.lwjgl;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.nbuwalda.spaceinvaders.resources.Sprite;

public class LWJGLSprite implements Sprite {

	private Texture texture;
	private int width;
	private int height;

	public LWJGLSprite(LWJGLGameWindow window, String ref) {
		try {
			texture = window.getTextureLoader().getTexture(ref);

			width = texture.getImageWidth();
			height = texture.getImageHeight();
		} catch (IOException e) {
			// a tad abrupt, but our purposes if you can't find a
			// sprite's image you might as well give up.
			System.err.println("Unable to load texture: " + ref);
			System.exit(0);
		}
	}

	public int getWidth() {
		return texture.getImageWidth();
	}

	public int getHeight() {
		return texture.getImageHeight();
	}

	public void draw(int x, int y) {
		GL11.glPushMatrix();

		texture.bind();
		GL11.glTranslatef(x, y, 0);
		GL11.glColor3f(1, 1, 1);

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(0, texture.getHeight());
			GL11.glVertex2f(0, height);
			GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
			GL11.glVertex2f(width, height);
			GL11.glTexCoord2f(texture.getWidth(), 0);
			GL11.glVertex2f(width, 0);
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}

}
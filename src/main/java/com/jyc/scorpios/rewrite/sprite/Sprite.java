package com.jyc.scorpios.rewrite.sprite;

import com.jyc.scorpios.Texture;
import com.jyc.scorpios.TextureLoader;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Sprite {
    private Texture texture;
    private int width;
    private int height;

    public Sprite(Texture texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public void draw(int x, int y) {
        glPushMatrix();

        texture.bind();

        glTranslatef(x, y, 0);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(0, 0);

            glTexCoord2f(0, texture.getHeight());
            glVertex2f(0, height);

            glTexCoord2f(texture.getWidth(), texture.getHeight());
            glVertex2f(width, height);

            glTexCoord2f(texture.getWidth(), 0);
            glVertex2f(width, 0);
        }
        glEnd();
        glPopMatrix();
    }

    public int getWidth() {
        return texture.getImageWidth();
    }

    public int getHeight() {
        return texture.getImageHeight();
    }
}

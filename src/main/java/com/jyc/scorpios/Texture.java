package com.jyc.scorpios;

import static org.lwjgl.opengl.GL11.glBindTexture;

public class Texture {
    private int target;
    private int textureID;
    private int imageWidth;
    private int imageHeight;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;

    public Texture(int target, int textureID) {
        this.target = target;
        this.textureID = textureID;
    }

    public void bind() {
        glBindTexture(target, textureID);
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setHeight(int height) {
        this.imageHeight = height;
        setHeight();
    }

    public void setWidth(int width) {
        this.imageWidth = width;
        setWidth();
    }

    public float getHeight() {
        return heightRatio;
    }

    public float getWidth() {
        return widthRatio;
    }

    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        setHeight();
    }

    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        setWidth();
    }

    private void setHeight() {
        if (texHeight != 0) {
            heightRatio = ((float) imageHeight) / texHeight;
        }
    }

    private void setWidth() {
        if (texWidth != 0) {
            widthRatio = ((float) imageWidth) / texWidth;
        }
    }
}

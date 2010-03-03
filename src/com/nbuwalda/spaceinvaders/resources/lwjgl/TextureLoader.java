package com.nbuwalda.spaceinvaders.resources.lwjgl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class TextureLoader {

	private Map<String, Texture> table = new HashMap<String, Texture>();
	private ColorModel glAlphaColorModel;
	private ColorModel glColorModel;

	public TextureLoader() {
		glAlphaColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
				true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 },
				false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
	}

	private int createTextureID() {
		IntBuffer tmp = createIntBuffer(1);
		GL11.glGenTextures(tmp);
		return tmp.get(0);
	}

	public Texture getTexture(String resourceName) throws IOException {
		Texture tex = (Texture) table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		tex = getTexture(resourceName, GL11.GL_TEXTURE_2D, // target
				GL11.GL_RGBA, // dst pixel format
				GL11.GL_LINEAR, // min filter (unused)
				GL11.GL_LINEAR);

		table.put(resourceName, tex);

		return tex;
	}

	
	public Texture getTexture(String resourceName, int target,
			int dstPixelFormat, int minFilter, int magFilter)
			throws IOException {
		int srcPixelFormat = 0;

		int textureID = createTextureID();
		Texture texture = new Texture(target, textureID);

		GL11.glBindTexture(target, textureID);

		BufferedImage bufferedImage = loadImage(resourceName);
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (target == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
			GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		}

		// produce a texture from the byte buffer
		GL11.glTexImage2D(target, 0, dstPixelFormat, get2Fold(bufferedImage
				.getWidth()), get2Fold(bufferedImage.getHeight()), 0,
				srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold
	 *            The target number
	 * @return The power of 2
	 */
	private int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(BufferedImage bufferedImage,
			Texture texture) {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);

		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable());
		}

		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/**
	 * Load a given resource as a buffered image
	 * 
	 * @param ref
	 *            The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException
	 *             Indicates a failure to find a resource
	 */
	private BufferedImage loadImage(String ref) throws IOException {
		URL url = TextureLoader.class.getClassLoader().getResource(ref);

		if (url == null) {
			throw new IOException("Cannot find: " + ref);
		}

		BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(
				getClass().getClassLoader().getResourceAsStream(ref)));

		return bufferedImage;
	}

	/**
	 * Creates an integer buffer to hold specified ints - strictly a utility
	 * method
	 * 
	 * @param size
	 *            how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(int size) {
		ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}
}

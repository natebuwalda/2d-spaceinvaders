package com.nbuwalda.spaceinvaders;

import java.awt.Graphics2D;

public class Entity {

	private double xPosition = 0.0D;
	private double yPosition = 0.0D;
	private double xVelocity = 0.0D;
	private double yVelocity = 0.0D;
	private Sprite sprite;
	
	public Entity(String imageRef, int xStartPosition, int yStartPosition) {
		this.sprite = SpriteStore.getStore().getSprite(imageRef);
		this.xPosition = xStartPosition;
		this.yPosition = yStartPosition;
	}
	
	public void move(long delta) {
		double xChange = (delta * xVelocity)/1000;
		xPosition += xChange;
		yPosition += (delta * yVelocity)/1000;
		if (this instanceof ShipEntity) {
			System.out.println("x = " + xPosition);
			if (xVelocity != 0) {
				System.out.println("velocity = " + xVelocity);
				System.out.println("");
				System.out.println("x change = " + xChange);
			}
		}
	}

	public void draw(Graphics2D graphics) {
		try {
			sprite.draw(graphics, (int) xPosition, (int) yPosition);
		} catch (Exception e) {
			System.err.println("xPosition = " + xPosition);
			System.err.println("yPosition = " + yPosition);
			System.err.println("sprite = " + sprite);
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected double getXPostion() {
		return xPosition;
	}
	
	protected double getYPosition() {
		return yPosition;
	}
	
	protected double getXVelocity() {
		return xVelocity;
	}
	
	protected double getYVelocity() {
		return yVelocity;
	}

	public void setXVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}
	
	
}

package com.nbuwalda.spaceinvaders;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entity {

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
		double yChange = (delta * yVelocity)/1000;
		yPosition += yChange;
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

	public boolean collidesWith(Entity other) {
		Rectangle me = new Rectangle();
		Rectangle him = new Rectangle();
		
		me.setBounds((int) xPosition, (int) yPosition, this.sprite.getWidth(), this.sprite.getHeight());
		him.setBounds((int) other.getXPosition(), (int) other.getYPosition(), other.getSprite().getWidth(), other.getSprite().getHeight());
		
		return me.intersects(him);
	}
	
	public double getXPosition() {
		return xPosition;
	}
	
	public double getYPosition() {
		return yPosition;
	}
	
	public double getXVelocity() {
		return xVelocity;
	}
	
	public double getYVelocity() {
		return yVelocity;
	}

	public Sprite getSprite() {
		return sprite;
	}
	
	public void setXVelocity(double xVelocity) {
		this.xVelocity = xVelocity;
	}

	public void setYVelocity(double yVelocity) {
		this.yVelocity = yVelocity;
	}
	
	public abstract void collidedWith(Entity other);

}

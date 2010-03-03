package com.nbuwalda.spaceinvaders.entity;

import java.awt.Rectangle;

import com.nbuwalda.spaceinvaders.resources.ResourceFactory;
import com.nbuwalda.spaceinvaders.resources.Sprite;

public abstract class Entity {
	protected double x;
	protected double y;
	protected Sprite sprite;
	protected double dx;
	protected double dy;
	private Rectangle me = new Rectangle();
	private Rectangle him = new Rectangle();

	public Entity(String ref, int x, int y) {
		this.sprite = ResourceFactory.get().getSprite(ref);
		this.x = x;
		this.y = y;
	}

	public void move(long delta) {
		// update the location of the entity based on move speeds
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}

	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}

	public double getHorizontalMovement() {
		return dx;
	}

	public double getVerticalMovement() {
		return dy;
	}

	public void draw() {
		sprite.draw((int) x, (int) y);
	}

	public void doLogic() {
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds((int) other.x, (int) other.y, other.sprite.getWidth(),
				other.sprite.getHeight());

		return me.intersects(him);
	}

	public abstract void collidedWith(Entity other);
}
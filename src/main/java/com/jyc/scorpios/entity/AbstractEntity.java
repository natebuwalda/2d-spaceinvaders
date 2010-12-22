package com.jyc.scorpios.entity;

import com.jyc.scorpios.Sprite;
import org.lwjgl.util.Rectangle;

public abstract class AbstractEntity {
    protected float x;
    protected float y;
    protected Sprite sprite;
    protected float horizontalMovement;
    protected float verticalMovement;
    private Rectangle me = new Rectangle();
    private Rectangle him = new Rectangle();

    public AbstractEntity(Sprite sprite, Integer x, Integer y) {
        this.sprite = sprite;
		this.x = x;
		this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void move(long delta) {
        x += (delta * horizontalMovement) / 1000;
		y += (delta * verticalMovement) / 1000;
    }

    public void draw() {
        sprite.draw((int) x, (int) y);
    }

    public boolean collidesWith(AbstractEntity other) {
        me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds((int) other.x, (int) other.y, other.sprite.getWidth(), other.sprite.getHeight());

		return me.intersects(him);
    }

    public abstract void collidedWith(AbstractEntity other);

    public void doLogic() {
        //empty on purpose
    }

    public void setHorizontalMovement(float dx) {
		this.horizontalMovement = dx;
	}

    public float getHorizontalMovement() {
        return horizontalMovement;
    }

    public void setVerticalMovement(float dy) {
		this.verticalMovement = dy;
	}

    public float getVerticalMovement() {
		return verticalMovement;
	}
}

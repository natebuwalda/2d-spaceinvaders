package com.jyc.scorpios;

public class Entity {
    private float horizontalMovement;
    private int x;
    private int y;

    public float getHorizontalMovement() {
        return horizontalMovement;
    }


    public void setHorizontalMovement(float horizontalMovement) {
        this.horizontalMovement = horizontalMovement;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(long delta) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void draw() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public boolean collidesWith(Entity him) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public void collidedWith(Entity him) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void doLogic() {
        //To change body of created methods use File | Settings | File Templates.
    }
}

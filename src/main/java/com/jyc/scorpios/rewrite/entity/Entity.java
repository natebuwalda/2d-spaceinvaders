package com.jyc.scorpios.rewrite.entity;

import com.jyc.scorpios.rewrite.sprite.Sprite;
import com.jyc.scorpios.rewrite.component.Component;
import com.jyc.scorpios.rewrite.component.ComponentMessage;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    protected Sprite sprite;
    protected float xPos;
    protected float yPos;
    protected List<Component> attachedCompomnents = new ArrayList<Component>();


    public Entity(Sprite sprite, Integer xPos, Integer yPos) {
        this.sprite = sprite;
		this.xPos = xPos;
		this.yPos = yPos;
    }

    public void attachComponent(Component component, String componentName, String... arguments){

    }

    public void detachComponent(String componentName) {

    }

    public void updateComponent(String componentName, String... arguments) {

    }

    public void handleMessage(ComponentMessage message) {

    }

    public float getXPos() {
        return xPos;
    }

    public void setXPos(float xPos) {
        this.xPos = xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public void setYPos(float yPos) {
        this.yPos = yPos;
    }


}

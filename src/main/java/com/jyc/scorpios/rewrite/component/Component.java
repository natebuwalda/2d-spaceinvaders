package com.jyc.scorpios.rewrite.component;

public abstract class Component {

    private String name;

    public abstract void startup(String... arguments);
    public abstract void update(String... arguments);
    public abstract void shutdown(String... arguments);
    public abstract void handleMessage(ComponentMessage message);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

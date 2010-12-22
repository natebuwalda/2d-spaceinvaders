package com.jyc.scorpios;

import java.util.ArrayList;
import java.util.List;

public class EntityCache {

    private List<AbstractEntity> allEntities = new ArrayList<AbstractEntity>();
    private List<AbstractEntity> entitiesToRemove = new ArrayList<AbstractEntity>();
    private List<ShotEntity> shots = new ArrayList<ShotEntity>();
    private List<AlienEntity> aliens = new ArrayList<AlienEntity>();
    private ShipEntity ship;


    public void addAlienEntity(AlienEntity alien) {
        allEntities.add(alien);
        aliens.add(alien);
    }

    public void addShotEntity(ShotEntity shot) {
        allEntities().add(shot);
        shots.add(shot);
    }

    public Integer alienCount() {
        return aliens.size();
    }

    public Integer shotCount() {
        return shots.size();
    }

    public Integer entityCount() {
        return allEntities.size();
    }

    public List<AbstractEntity> allEntities() {
        return allEntities;
    }

    public void removeEntity(AbstractEntity entity) {
        entitiesToRemove.add(entity);
    }

    public Integer flushRemovals() {
        int removalCount = 0;
        for (AbstractEntity entity : entitiesToRemove) {
            if (allEntities.contains(entity)) {
                allEntities.remove(entity);
                if (entity instanceof AlienEntity)
                    aliens.remove(entity);
                if (entity instanceof ShotEntity)
                    shots.remove(entity);
                removalCount++;
            }
        }
        entitiesToRemove.clear();
        return removalCount;
    }

    public void addPlayerShipEntity(ShipEntity ship) {
        allEntities.add(ship);
        this.ship = ship;
    }

    public ShipEntity playerShip() {
        return ship;
    }
}

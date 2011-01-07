package com.jyc.scorpios.rewrite;

import com.jyc.scorpios.rewrite.entity.AbstractEntity;
import com.jyc.scorpios.rewrite.entity.GreenGnatEntity;
import com.jyc.scorpios.rewrite.entity.ShipEntity;
import com.jyc.scorpios.rewrite.entity.ShotEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityCache {

    private List<AbstractEntity> allEntities = new ArrayList<AbstractEntity>();
    private List<AbstractEntity> entitiesToRemove = new ArrayList<AbstractEntity>();
    private List<ShotEntity> shots = new ArrayList<ShotEntity>();
    private List<GreenGnatEntity> greenGnats = new ArrayList<GreenGnatEntity>();
    private ShipEntity ship;


    public void addAlienEntity(GreenGnatEntity greenGnat) {
        allEntities.add(greenGnat);
        greenGnats.add(greenGnat);
    }

    public void addShotEntity(ShotEntity shot) {
        allEntities().add(shot);
        shots.add(shot);
    }

    public Integer alienCount() {
        return greenGnats.size();
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
                if (entity instanceof GreenGnatEntity)
                    greenGnats.remove(entity);
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

package com.jyc.scorpios;

import com.jyc.scorpios.entity.GreenGnatEntity;
import com.jyc.scorpios.entity.ShipEntity;
import com.jyc.scorpios.entity.ShotEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class EntityCacheTest {
    private EntityCache cache;

    @Before
    public void setUp() throws Exception {
        cache = new EntityCache();
    }

    @Test
    public void addAlienEntity() {
        GreenGnatEntity greenGnat = mock(GreenGnatEntity.class);
        cache.addAlienEntity(greenGnat);

        assertEquals(1, cache.alienCount().intValue());
        assertEquals(1, cache.entityCount().intValue());
        assertEquals(0, cache.shotCount().intValue());
    }

    @Test
    public void addMultipleAliensAndRemoveCorrectOne() {
        GreenGnatEntity greenGnatA = mock(GreenGnatEntity.class);
        GreenGnatEntity greenGnatB = mock(GreenGnatEntity.class);

        cache.addAlienEntity(greenGnatA);
        cache.addAlienEntity(greenGnatB);

        assertEquals(2, cache.alienCount().intValue());
        assertEquals(2, cache.entityCount().intValue());

        cache.removeEntity(greenGnatB);
        Integer removed = cache.flushRemovals();

        assertEquals(1, removed.intValue());
        assertEquals(1, cache.alienCount().intValue());
        assertEquals(1, cache.entityCount().intValue());
        assertSame(greenGnatA, cache.allEntities().get(0));
    }

    @Test
    public void addShotEntity() {
        ShotEntity shot = mock(ShotEntity.class);
        cache.addShotEntity(shot);

        assertEquals(0, cache.alienCount().intValue());
        assertEquals(1, cache.shotCount().intValue());
        assertEquals(1, cache.entityCount().intValue());
    }

    @Test
    public void addMultipleShotsAndRemoveCorrectOne() {
        ShotEntity shotA = mock(ShotEntity.class);
        ShotEntity shotB = mock(ShotEntity.class);

        cache.addShotEntity(shotA);
        cache.addShotEntity(shotB);

        assertEquals(2, cache.shotCount().intValue());
        assertEquals(2, cache.entityCount().intValue());

        cache.removeEntity(shotB);
        Integer removed = cache.flushRemovals();

        assertEquals(1, removed.intValue());
        assertEquals(1, cache.shotCount().intValue());
        assertEquals(1, cache.entityCount().intValue());
        assertSame(shotA, cache.allEntities().get(0));
    }


    @Test
    public void addPlayerShip() {
        ShipEntity ship = mock(ShipEntity.class);
        cache.addPlayerShipEntity(ship);

        assertNotNull(cache.playerShip());
        assertEquals(0, cache.alienCount().intValue());
        assertEquals(0, cache.shotCount().intValue());
        assertEquals(1, cache.entityCount().intValue());
    }
}

package com.nbuwalda.spaceinvaders;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.nbuwalda.game.resources.ResourceFactory;
import com.nbuwalda.spaceinvaders.resources.java2d.Java2DGameWindow;

public class SpaceInvadersGameStateControllerTest {

	SpaceInvadersGameStateController controller;
	
	@Before
	public void setUp() {
		controller = new SpaceInvadersGameStateController();
		controller.setRenderingType(ResourceFactory.JAVA2D);
	}
	
	@Test
	public void testInitializeGame() {
		controller.initializeGame();
		
		Assert.assertNotNull(controller.getGameWindow());
		Assert.assertTrue(controller.getGameWindow() instanceof Java2DGameWindow);
	}
}

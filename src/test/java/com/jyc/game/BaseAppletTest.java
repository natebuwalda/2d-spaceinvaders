package com.jyc.game;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertNotNull;

public class BaseAppletTest {

    private BaseApplet applet;

    @Before
    public void setUp() {
        applet = new BaseApplet();
    }

    @Test
    public void init() {
        applet.init();
        Canvas parentDisplay = applet.getParentDisplay();
        assertNotNull(parentDisplay);
    }
}

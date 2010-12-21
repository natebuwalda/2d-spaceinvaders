package com.jyc.scorpios;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertNotNull;

public class ScorpiosAppletTest {

    private ScorpiosApplet applet;

    @Before
    public void setUp() {
        applet = new ScorpiosApplet();
    }

    @Test
    public void init() {
        applet.init();
        Canvas parentDisplay = applet.getParentDisplay();
        assertNotNull(parentDisplay);
    }
}

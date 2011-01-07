package com.jyc.scorpios.rewrite;


import com.jyc.scorpios.rewrite.Sprite;
import com.jyc.scorpios.Texture;
import com.jyc.scorpios.TextureLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteCache {
    private TextureLoader textureLoader = new TextureLoader();
    private Map<String, Sprite> cache = new HashMap<String, Sprite>();

    public void clear() {
        cache.clear();
    }

    public void createSprite(String spriteKey) throws IOException {
        Texture texture = textureLoader.getTexture("sprites/" + spriteKey);
        Integer width = texture.getImageWidth();
        Integer height = texture.getImageHeight();
        Sprite sprite = new Sprite(texture, width, height);
        cache.put(spriteKey, sprite);
    }

    public Sprite getSprite(String spriteKey) {
        return cache.get(spriteKey);
    }
}

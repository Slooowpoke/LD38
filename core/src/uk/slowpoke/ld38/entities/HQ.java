package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HQ extends LandEntity{

	public HQ(float x, float y,Player owner) {
		super(x, y,0,0, 20, owner,new Sprite(new Texture("HQ.png")),10);
	}
}

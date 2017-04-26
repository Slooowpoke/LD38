package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Artillery extends LandEntity{

	public Artillery(float x, float y,Player owner) {
		super(x, y,0,0, 20, owner,new Sprite(new Texture("artillery.png")),10);
	}
}

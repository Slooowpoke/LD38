package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bomber extends AirEntity{

	public Bomber(float x, float y,Player owner) {
		super(x, y, 20, owner,new Sprite(new Texture("bomber.png")));
	}
}

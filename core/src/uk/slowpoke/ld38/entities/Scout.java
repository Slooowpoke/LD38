package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Scout extends AirEntity{

	public Scout(float x, float y,Player owner) {
		super(x, y,0,0, 20, owner,new Sprite(new Texture("scout.png")),50);
	}
}

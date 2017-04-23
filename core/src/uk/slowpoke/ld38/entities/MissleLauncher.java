package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MissleLauncher extends LandEntity{

	public MissleLauncher(float x, float y,Player owner) {
		super(x, y, 20, owner,new Sprite(new Texture("misslelauncher.png")));
	}
}

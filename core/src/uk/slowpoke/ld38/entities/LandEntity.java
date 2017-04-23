package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import uk.slowpoke.ld38.world.Map;

public class LandEntity extends Unit{

	Sprite image;
	
	public LandEntity(float x, float y,int size,Player owner,Sprite image) {
		super(x, y, size, owner);
		this.image = image;
	}
	
	public void render(SpriteBatch batch){
		batch.draw(image, getX(), getY());
	}
	
	public void update(float delta,Map map){
		
	}

}

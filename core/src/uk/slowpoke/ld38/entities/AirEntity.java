package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AirEntity extends Unit{

	Sprite image;
	
	public AirEntity(float x, float y,int size,Player owner,Sprite image) {
		super(x, y, size, owner);
		this.image = image;
	}
	
	public void render(SpriteBatch batch){
		image.rotate(100);
		batch.draw(image, getX(), getY(),0,0,10,10,1f,1f,dir.angle()-90);
	}

}

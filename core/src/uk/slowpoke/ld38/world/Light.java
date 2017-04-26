package uk.slowpoke.ld38.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import uk.slowpoke.ld38.entities.Entity;

public class Light extends Entity{

	Sprite image;
	
	float size;
	float originalSize;
	boolean glow;
	
	public Light(float x, float y, float size, Texture texture) {
		super(x, y, size, size,0,0);
		this.size = size;
		originalSize = size;
		this.image = new Sprite(texture);
	}
	
	public void update(float dt){
		float target;
		if(glow){
			target = originalSize+2;
		}else{
			target = originalSize-2;
		}
		
		size+= (target-size)*0.05f;
		
		if(size <= originalSize-4) glow = true;
		if(size >= originalSize+4) glow = false;
	}
	
	public void render(SpriteBatch batch){
		if(image != null) 		batch.draw(image, getX()-size/2+8, getY()-size/2+8, size,size);
	}
	
	public void setLight(Sprite image){
		this.image = image;
	}
	
}

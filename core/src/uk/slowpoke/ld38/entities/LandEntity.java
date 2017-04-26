package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import uk.slowpoke.ld38.world.Map;

public class LandEntity extends Unit{

	Sprite image;
	
	public LandEntity(float x, float y,int width, int height,int range,Player owner,Sprite image,int reloadTime) {
		super(x, y,width,height, range, owner,0f,reloadTime);
		this.image = image;
		// set the width and height
		setWidth(image.getWidth());
		setHeight(image.getHeight());
		light.setX(getX()-getWidth());
		light.setY(getY()-getHeight());
	}
	
	public void render(SpriteBatch batch){
		if(immune){
			image.setColor(1f,1f,1f,0.5f);
		}else{
			image.setColor(1f, 1f, 1f, 1f);
		}
		image.setX(getX()-image.getWidth()/2);
		image.setY(getY()-image.getHeight()/2);
		image.draw(batch);
		light.setX(getX()-getWidth());
		light.setY(getY()-getHeight());
	}
	
	public void update(float delta,Map map){
		if(bulletFired){
			cooldown++;
			if(cooldown > reloadTime){
				cooldown = 0;
				bulletFired = false;
			}
		}
		if(immune){
			immuneTimeout++;
			if(immuneTimeout > 5){
				immune = false;
			}
		}
		
		if(getHp() < 0){
			dead = true;
		}

		
	}

}

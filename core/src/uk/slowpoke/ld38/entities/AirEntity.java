package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AirEntity extends Unit{

	Sprite image;
	
	public AirEntity(float x, float y,int width, int height,int range,Player owner,Sprite image,int reloadTime) {
		super(x, y,width,height, range, owner,2f,reloadTime);
		this.image = image;
		// set the width and height
		setWidth(image.getWidth());
		setHeight(image.getHeight());
		light.setX(getX()-getWidth());
		light.setY(getY()-getHeight());
	}
	
	public void render(SpriteBatch batch){
		image.setRotation(dir.angle()-90);
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

}

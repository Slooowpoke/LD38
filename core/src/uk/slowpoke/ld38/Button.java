package uk.slowpoke.ld38;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import uk.slowpoke.ld38.entities.Unit;

public class Button {
	Sprite image;
	Sprite imageDown;
	boolean buttonPushed = false;
	Unit unit;
	int type;
	float x,y;
	public Button(float x,float y,int type){
		image = new Sprite(new Texture("button.png"));
		imageDown = new Sprite(new Texture("button_down.png"));
		this.x = x;
		this.y = y;
		this.type = type;
		this.unit = unit;
	}
	
	public void pushButton(){
		buttonPushed = true;
	}
	
	public void render(SpriteBatch batch){
		if(buttonPushed){
			batch.draw(imageDown, x, y);
		}else{
			batch.draw(image,x,y);
		}

	}
}

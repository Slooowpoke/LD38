package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import uk.slowpoke.ld38.Game;

public class Airbase extends LandEntity{
	private int totalDeployed = 0;
	
	public Airbase(float x, float y,Player owner) {
		super(x, y,0,0, 20, owner,new Sprite(new Texture("airbase.png")),10);
	}
	
	public AirEntity deployAircraft(int type){
		// add unit and place it near
		if(type == 1){
			setTotalDeployed(getTotalDeployed() + 1);
			return new Bomber(getX(),getY(),this.owner);
		}else if(type == 2){
			setTotalDeployed(getTotalDeployed() + 1);

			return new Scout(getX(),getY(),this.owner);
		}
		return null;

	}

	public int getTotalDeployed() {
		return totalDeployed;
	}

	public void setTotalDeployed(int totalDeployed) {
		this.totalDeployed = totalDeployed;
	}
	
	public void renderQuantity(SpriteBatch batch){
		GlyphLayout layout = new GlyphLayout();
		Game.tiny.setColor(Color.WHITE);
		layout.setText(Game.tiny,totalDeployed + " / 30");	
		Game.tiny.draw(batch, totalDeployed + " / 30", getX()-(layout.width/2),getY()-8);
	}
	
	// render how many planes are left here.
}

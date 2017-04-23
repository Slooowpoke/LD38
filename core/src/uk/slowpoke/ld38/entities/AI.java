package uk.slowpoke.ld38.entities;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import uk.slowpoke.ld38.world.Map;

public class AI extends Player{

	public AI(String name, Color colour) {
		super(name, colour);
		placeUnits();
	}
	
	Random random = new Random();
	public void placeUnits(){
		for(int i = 0; i < 30; i++){
			int x = random.nextInt(Map.WIDTH);
			int y = random.nextInt(Map.HEIGHT);
//			addUnit(x+1,y);
//			addUnit(x-1,y);
//			addUnit(x,y+1);
//			addUnit(x,y);
		}
	}
	
}

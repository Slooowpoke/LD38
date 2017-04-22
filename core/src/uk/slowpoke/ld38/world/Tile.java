package uk.slowpoke.ld38.world;

import com.badlogic.gdx.graphics.Color;

public class Tile {
	
	Color colour;
	
	// the owner of the tile?
	String name;
	
	public Tile(Color colour,String name){
		this.colour = colour;
		this.name = name;
	}
}

package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Color;

// does not technically fall under the entity class but is an entity controller.

public class Player {
	// this will store your choices after each day.
	
	String name;
	Color colour;
	
	public Player(String name,Color colour){
		this.name = name;
		this.colour = colour;
	}
	
	public Color getColour(){
		return colour;
	}

	public String getName(){
		return name;
	}

}

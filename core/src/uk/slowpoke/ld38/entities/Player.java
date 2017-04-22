package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Color;

// does not technically fall under the entity class but is an entity controller.

public class Player {
	// this will store your choices after each day.
	
	
	String name;
	Color color;
	
	public Player(String name,Color color){
		this.name = name;
		this.color = color;
	}

	public String getName(){
		return name;
	}

}

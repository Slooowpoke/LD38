package uk.slowpoke.ld38.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

// does not technically fall under the entity class but is an entity controller.

public class Player {
	// this will store your choices after each day.
	
	String name;
	Color colour;
	
	ArrayList<Unit> units = new ArrayList<Unit>();
	
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
	
	public void addUnit(int x,int y){
		units.add(new Unit(x,y));
	}

	public ArrayList<Unit> getUnits(){
		return units;
	}

	public void selectShips(Rectangle selection) {
		for(Unit unit: units){
			System.out.println(selection);
			
			if(selection.contains(unit.getX(),unit.getY())){
				System.out.println("CONTAINS");
			}
			
		}
	}	
}

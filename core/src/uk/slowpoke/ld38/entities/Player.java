package uk.slowpoke.ld38.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import uk.slowpoke.ld38.Game;

// does not technically fall under the entity class but is an entity controller.

public class Player {
	// this will store your choices after each day.
	
	String name;
	Color colour;
	
	ArrayList<Unit> units = new ArrayList<Unit>();
	
	boolean placedHQ = false;
	
	public Player(String name,Color colour,Map map){
		this.name = name;
		this.colour = colour;
	}
	
	public Color getColour(){
		return colour;
	}

	public String getName(){
		return name;
	}
	
	public void addUnit(int x,int y,int type){
		if(type == Game.HQ){
			
			if(!placedHQ){
				units.add(new HQ(x,y,this));
				placedHQ = true;
			}
			
		}else if(type == Game.BOMBER){
			units.add(new Bomber(x,y,this));
		}else if(type == Game.SCOUT){
			units.add(new Scout(x,y,this));
		}else if(type == Game.MISSLE_LAUNCHER){
			// extra check to make sure its not water.
			
			units.add(new MissleLauncher(x,y,this));
		}else if(type == Game.ARTILLERY){
			units.add(new Artillery(x,y,this));
		}
		
//		units.add(new Unit(x,y,20,this));
	}

	public ArrayList<Unit> getUnits(){
		return units;
	}

	public void selectShips(Rectangle selection) {
		System.out.println(selection);
		for(Unit unit: units){
			Rectangle r = new Rectangle(unit.getX(),unit.getY(),1,1);
			if(selection.overlaps(r)){
				unit.selected = true;
//				System.out.println("CONTAINS");
			}else{
				unit.selected = false;
			}
			
		}
	}
	
	public void update(float delta){
		// update all our units
		for(Unit unit: units){
//			unit.update(delta);
		}
	}

	public void target(float x, float y) {
		for(Unit unit: units){
			if(unit.selected){
				// set the units target to be that funny location
				unit.setTarget(x,y);
			}
		}
	}	
}

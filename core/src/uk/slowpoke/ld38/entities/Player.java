package uk.slowpoke.ld38.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import uk.slowpoke.ld38.Game;
import uk.slowpoke.ld38.world.Map;

// does not technically fall under the entity class but is an entity controller.

public class Player {
	// this will store your choices after each day.
	
	String name;
	Color colour;
	
	ArrayList<Unit> units = new ArrayList<Unit>();
	
	private boolean placedHQ = false;
	Map map;
	public int totalMissleLaunchers = 0;
	public int totalAirbase = 0;
	public int totalArtillery = 0;
	
	private int deployedScouts = 0;
	public boolean hqDead;

	public Player(String name,Color colour,Map map){
		this.name = name;
		this.colour = colour;
		this.map = map;
	}
	
	public Color getColour(){
		return colour;
	}

	public String getName(){
		return name;
	}
	
	public boolean addUnit(int x,int y,int type){
		if(type == Game.HQ){
			
			if(!hasPlacedHQ() && map.checkIfMyLand(x,y,name)){
				units.add(new HQ(x,y,this));
				setPlacedHQ(true);
				return true;
			}
			
		}else if(type == Game.BOMBER){
			// th ese guys get placed in a special way, they boomerang round
			units.add(new Bomber(x,y,this));
			return true;
		}else if(type == Game.SCOUT){
			units.add(new Scout(x,y,this));
			setDeployedScouts(getDeployedScouts() + 1);
			return true;
		}else if(type == Game.MISSLE_LAUNCHER){
			// extra check to make sure its not water.
			if(map.checkIfMyLand(x,y,name)){
				if(totalMissleLaunchers < 5){
					units.add(new MissleLauncher(x,y,this));
					totalMissleLaunchers++;
					return true;
				}else{
					return false;
				}
			}
		}else if(type == Game.ARTILLERY){
			if(map.checkIfMyLand(x,y,name)){
				if(totalArtillery < 5){
					units.add(new Artillery(x,y,this));
					totalArtillery++;
					return true;
				}else{
					return false;
				}
			}
		}else if(type == Game.AIRBASE){
			if(map.checkIfMyLand(x,y,name)){
				if(totalAirbase < 5){
					units.add(new Airbase(x,y,this));
					totalAirbase++;
					return true;
				}else{
					return false;
				}

			}
		}
		return false;
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

	public void setUnits(ArrayList<Unit> newUnits) {
		this.units = newUnits;
	}

	public boolean hasPlacedHQ() {
		return placedHQ;
	}

	public void setPlacedHQ(boolean placedHQ) {
		this.placedHQ = placedHQ;
	}

	public boolean placedAllBuildings() {
		if(totalMissleLaunchers >= 5
				&& totalAirbase >= 5
				&& totalArtillery >= 5){
			return true;
		}else{
			return false;
		}
	}	
	
	public boolean deployedAllScouts(){
		if(getDeployedScouts() >= 5){
			return true;
		}else{
			return false;
		}
	}

	public int getDeployedScouts() {
		return deployedScouts;
	}

	public void setDeployedScouts(int deployedScouts) {
		this.deployedScouts = deployedScouts;
	}
}

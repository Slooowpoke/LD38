package uk.slowpoke.ld38.entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import uk.slowpoke.ld38.world.Map;

public class AI extends Player{

	public AI(String name, Color colour,Map map) {
		super(name, colour,map);
		placeUnits();
	}
	
	public void placeHQ(){
		// find us a random HQ.
		while(!hasPlacedHQ()){
			int x = random.nextInt(Map.WIDTH);
			int y = random.nextInt(Map.HEIGHT);
			// get a random location
			// check if its land 
			if(map.checkIfMyLand(x, y, name)){
				units.add(new HQ(x,y,this));
				setPlacedHQ(true);
			}
		}
	}
	
	public void placeBuildings(){
		for(int i = 0; i < 5; i++){
			boolean placed = false;
			while(!placed){
				int x = random.nextInt(Map.WIDTH);
				int y = random.nextInt(Map.HEIGHT);
				// get a random location
				// check if its land 
				if(map.checkIfMyLand(x, y, name) && isNotNearOtherBuildings(x,y)){
					units.add(new MissleLauncher(x,y,this));
					placed = true;
					totalMissleLaunchers++;
					break;
				}
			}
			
		}
		for(int i = 0; i < 5; i++){
			boolean placed = false;
			while(!placed){
				int x = random.nextInt(Map.WIDTH);
				int y = random.nextInt(Map.HEIGHT);
				// get a random location
				// check if its land 
				if(map.checkIfMyLand(x, y, name)){
					units.add(new Artillery(x,y,this));
					totalArtillery++;
					placed = true;
					break;
				}
			}
			
		}
		for(int i = 0; i < 5; i++){
			boolean placed = false;
			while(!placed){
				int x = random.nextInt(Map.WIDTH);
				int y = random.nextInt(Map.HEIGHT);
				// get a random location
				// check if its land 
				if(map.checkIfMyLand(x, y, name)){
					units.add(new Airbase(x,y,this));
					totalAirbase++;
					placed = true;
					break;
				}
			}
			
		}
	}
	
	private boolean isNotNearOtherBuildings(int x, int y) {
		for(Unit unit: units){
			if(unit.getClass() == Artillery.class){
				if(unit.isNear(x, y, 2)){
					return false;
				}
			}
			if(unit.getClass() == Airbase.class){
				if(unit.isNear(x, y, 2)){
					return false;
				}
			}
			if(unit.getClass() == MissleLauncher.class){
				if(unit.isNear(x, y, 2)){
					return false;
				}
			}
		}
		return true;
	}

	Random random = new Random();
	public void placeUnits(){
		for(int i = 0; i < 30; i++){
			int x = random.nextInt(Map.WIDTH);
			int y = random.nextInt(Map.HEIGHT);
		}
	}

	public void deployScouts() {
		int totalDeployed = 0;
		// fire scouts towards random islands
		ArrayList<Unit> tempUnits = new ArrayList<Unit>();
		for(Unit unit: units){
			if(unit.getClass() == Airbase.class){
				while(totalDeployed < 5 && getDeployedScouts() < 10){
					tempUnits.add(((Airbase) unit).deployAircraft(2));// deploy scout it is 2.
					setDeployedScouts(getDeployedScouts() + 1);
					totalDeployed++;
				}
				if(totalDeployed >= 5){
					totalDeployed = 0;
				}
			}
		}
		
		for(Unit unit: tempUnits){
			// set the targets for all of these
			// search for islands
			boolean placed = false;
			while(!placed){
				int x = random.nextInt(Map.WIDTH);
				int y = random.nextInt(Map.HEIGHT);
				// get a random location
				// check if its land 
				if(map.checkIfMyLand(x, y, "Mali")){
					unit.setTarget(x, y);
					
					System.out.println("Target aquired");
					break;
				}
			}
		}
		
		
		units.addAll(tempUnits);
		
		// after adding the units, 
		
	}
	
	// omg no tactics
	public void deployAllUnits(){
		int totalDeployed = 0;
		// fire scouts towards random islands
		ArrayList<Unit> tempUnits = new ArrayList<Unit>();
		for(Unit unit: units){
			totalDeployed = 0;
			if(unit.getClass() == Airbase.class){
				while(totalDeployed < 30){
					if(random.nextBoolean()){
						tempUnits.add(((Airbase) unit).deployAircraft(2));// deploy scout it is 2.
					}else{
						tempUnits.add(((Airbase) unit).deployAircraft(1));// deploy scout it is 2.
					}

					setDeployedScouts(getDeployedScouts() + 1);
					totalDeployed++;
				}
			}
		}
		
		for(Unit unit: tempUnits){
			// set the targets for all of these
			// search for islands
			boolean placed = false;
			while(!placed){
				int x = random.nextInt(Map.WIDTH);
				int y = random.nextInt(Map.HEIGHT);
				// get a random location
				// check if its land 
				if(map.checkIfMyLand(x, y, "Mali")){
					unit.setTarget(x, y);
					
					System.out.println("Target aquired");
					break;
				}
			}
		}
		
		
		units.addAll(tempUnits);
	}
	
}

package uk.slowpoke.ld38.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import uk.slowpoke.ld38.Game;
import uk.slowpoke.ld38.entities.Player;

public class Map {
	// here we generate the map, from tiles!
	// lets hope we can make it big enough
	
	private final int WIDTH = 800, HEIGHT = 600;
	
	int data[][] = new int[WIDTH][HEIGHT];
	int border[][] = new int[WIDTH][HEIGHT];
	Tile world[][] = new Tile[WIDTH][HEIGHT];
	boolean borderOnly = true;
	
	//Random variable
	Random random = new Random();
		
	private final int WATER = 111, GROUND = 1, TEMP = 69;
	// team colours
	private final int RED = 1, BLUE = 2, GREEN = 3;
	
	// kinda temporary, just used for determining whose is whose.
	List<Island> islands = new ArrayList<Island>();
	
	public Map(){
		SimplexNoise simplexNoise = new SimplexNoise(350,0.38,random.nextInt());

		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				data[x][y] = WATER;
				world[x][y] = null;
			}
		}
		
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				double value = simplexNoise.getNoise(x, y);
				if(value > 0.3){
					data[x][y] = GROUND;
				}
			}
		}

		
		// check through all the ground tiles and create some islands
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(data[x][y] == GROUND){
					islands.add(new Island(getGroundForIsland(x,y)));
				}
			}
		}

		List<Island> sorted = islands;
		
		Collections.sort(sorted, new Comparator<Island>() {
	        @Override public int compare(Island p1, Island p2) {
	            return p2.data.size() - p1.data.size(); // Ascending
	        }
	    });
		sorted = sorted.subList(0, Game.players.size());

		// varying numbers of players
		for(int i = 0; i < sorted.size(); i++){
			Island island = sorted.get(i);
			Player player = Game.players.get(i);
			System.out.println(i + "I IS ");;
			for(Vector2 tile : island.data){
				// go through and assign this island to our players
//				data[(int)tile.x][(int)tile.y] = i;// add one because water is 0
				world[(int)tile.x][(int)tile.y] = new Tile(player.getColour(),player.getName());
			}
		}

		islands = islands.subList(Game.players.size(), islands.size());
		
		int islandsPerPlayer = islands.size()/Game.players.size();
		int totalAssigned = 0;
		for(int start =  0; start < islands.size(); start+=islandsPerPlayer){
			if(totalAssigned < Game.players.size()){
				Player player = Game.players.get(totalAssigned);
				int end = Math.min(start + islandsPerPlayer, islands.size());
				List<Island> sublist = islands.subList(start, end);
				
				for(int i = 0; i < sublist.size(); i++){
					Island island = sublist.get(i);
					
					for(Vector2 tile : island.data){
						// go through and assign this island to our players
						data[(int)tile.x][(int)tile.y] = i;// add one because water is 0
						world[(int)tile.x][(int)tile.y] = new Tile(player.getColour(),player.getName());
					}
				}

				totalAssigned++;
			}

		}
	}
	
	public ArrayList<Vector2> getGroundForIsland(int x, int y){
		ArrayList<Vector2> ground = new ArrayList<Vector2>();
		
		if(data[x][y] == GROUND){
			ground.add(new Vector2(x,y));
			data[x][y] = TEMP;
			
			// check all sides of us to see if they are water
			if(x-1 > 0){
				// we are all good to the left
				if(data[x-1][y] == GROUND){
					ground.addAll(getGroundForIsland(x-1,y));
				}
			}
			if(x+1 < WIDTH){
				// we are all good to the right
				if(data[x+1][y] == GROUND){
					ground.addAll(getGroundForIsland(x+1,y));
				}
			}
			if(y-1 > 0){
				// we are all good above 
				if(data[x][y-1] == GROUND){
					ground.addAll(getGroundForIsland(x,y-1));
				}
			}
			if(y+1 < HEIGHT){
				// we are all good below.
				if(data[x][y+1] == GROUND){
					ground.addAll(getGroundForIsland(x,y+1));
				}
			}

		}

		return ground;
	}
	
	// assign the player an island if its roughly the right size for their choice
	public void assignToPlayer(){
		
	}


	public void checkTile(int x,int y,int player){
		// check all sides to make sure they aren't out of bounds
		if(x-1 > 0){
			// we are all good to the left
			if(data[x-1][y] == WATER){
				// if its water convert us to a border.
				border[x-1][y] = player;
			}
		}
		if(x+1 < WIDTH){
			// we are all good to the right
			if(data[x+1][y] == WATER){
				// if its water convert us to a border.
				border[x+1][y] = player;
			}
		}
		if(y-1 > 0){
			// we are all good above 
			if(data[x][y-1] == WATER){
				// if its water convert us to a border.
				border[x][y-1] = player;
			}
		}
		if(y+1 < HEIGHT){
			// we are all good below.
			if(data[x][y+1] == WATER){
				// if its water convert us to a border.
				border[x][y+1] = player;
			}
		}
	}
	
	public void render(ShapeRenderer batch){
		batch.setColor(Color.WHITE);
		batch.rect(0, 0,800,600);
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
//				if(data[x][y] == TEMP){
//					batch.setColor(Color.RED);
//					batch.rect(x, y,1,1);
//				}
				
				
					if(!borderOnly){
						if(world[x][y] != null){
//							batch.setColor(world[x][y].colour);
//							batch.rect(x, y,1,1);
						}
					}else{
						if(checkForWaterNeighbour(x,y)){
							if(world[x][y] != null){
								batch.setColor(world[x][y].colour);
								batch.rect(x, y,1,1);
							}
						}
					}
				


//				if(world[x][y] != WATER && world[x][y] != TEMP){
////					int tempBorder = border[x][y];
////					if(tempBorder >= Game.players.size()) tempBorder = Game.players.size()-1;
////					batch.setColor(Game.players.get((border[x][y])).getColour());
////					batch.rect(x, y,1,1);
//				}
//				
//				if(border[x][y] == GROUND){
//					
//				}
//				if(border[x][y] == GREEN){
//					batch.setColor(Color.GREEN);
//					batch.rect(x, y,1,1);
//				}
//				if(border[x][y] == BLUE){
//					batch.setColor(Color.BLUE);
//					batch.rect(x, y,1,1);
//				}

			}
		}
		
	}

	private boolean checkForWaterNeighbour(int x, int y) {
		if(x-1 > 0){
			// we are all good to the left
			if(world[x-1][y] == null){
				// if its water convert us to a border.
				return true;
			}
		}
		if(x+1 < WIDTH){
			// we are all good to the right
			if(world[x+1][y] == null){
				// if its water convert us to a border.
				return true;
			}
		}
		if(y-1 > 0){
			// we are all good above 
			if(world[x][y-1] == null){
				// if its water convert us to a border.
				return true;
			}
		}
		if(y+1 < HEIGHT){
			// we are all good below.
			if(world[x][y+1] == null){
				// if its water convert us to a border.
				return true;
			}
		}
		return false;
	}
}

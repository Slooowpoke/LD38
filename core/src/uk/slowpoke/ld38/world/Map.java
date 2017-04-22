package uk.slowpoke.ld38.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Map {
	// here we generate the map, from tiles!
	// lets hope we can make it big enough
	
	private final int WIDTH = 800, HEIGHT = 600;
	
	int data[][] = new int[WIDTH][HEIGHT];
	int border[][] = new int[WIDTH][HEIGHT];
	
	//Random variable
	Random random = new Random();
		
	private final int WATER = 0, GROUND = 1, TEMP = 69;
	// team colours
	private final int RED = 1, BLUE = 2, GREEN = 3;
	
	// kinda temporary, just used for determining whose is whose.
	List<Island> islands = new ArrayList<Island>();
	
	public Map(){
		SimplexNoise simplexNoise = new SimplexNoise(350,0.38,random.nextInt());

		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				data[x][y] = WATER;
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
		
		// create the borders
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(data[x][y] == GROUND ){
					checkTile(x,y,data[x][y]);
				}
			}
		}
		
		// check through all the ground tiles and create some islands
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(data[x][y] == GROUND){
					islands.add(new Island(getGroundForIsland(x,y)));
//					System.out.println(data.size());
				}
			}
		}

		
		Collections.sort(islands, new Comparator<Island>() {
	        @Override public int compare(Island p1, Island p2) {
	            return p2.data.size() - p1.data.size(); // Ascending
	        }
	    });
		
		
		int islandsAssigned = 0;
		for(Island i: islands){
			System.out.println(i.data.size());

			islandsAssigned++;
			if(islandsAssigned >= 3){
				break;
			}
		}
		islands = islands.subList(3, islands.size());
	}
	

	public ArrayList<Vector2> getGroundForIsland(int x, int y){
		ArrayList<Vector2> ground = new ArrayList<Vector2>();
		
		
		if(data[x][y] == GROUND){
			data[x][y] = TEMP;
			ground.add(new Vector2(x,y));
			// check all sides of us to see if they are water
			if(x-1 > 0){
				// we are all good to the left
				if(data[x-1][y] == GROUND){
					ground.add(new Vector2(x-1,y));
					
					ground.addAll(getGroundForIsland(x-1,y));
				}
			}
			if(x+1 < WIDTH){
				// we are all good to the right
				if(data[x+1][y] == GROUND){
					ground.add(new Vector2(x+1,y));
					ground.addAll(getGroundForIsland(x+1,y));
				}
			}
			if(y-1 > 0){
				// we are all good above 
				if(data[x][y-1] == GROUND){
					ground.add(new Vector2(x,y-1));
					ground.addAll(getGroundForIsland(x,y-1));
				}
			}
			if(y+1 < HEIGHT){
				// we are all good below.
				if(data[x][y+1] == GROUND){
					ground.add(new Vector2(x,y+1));
					ArrayList<Vector2> newData = getGroundForIsland(x,y+1);
					ground.addAll(newData);

				}
			}

		}

		return ground;
	}
	
	// assign the player an island if its roughly the right size for their choice
	public void assignToPlayer(){
		
	}


	public void checkTile(int x,int y,int colour){
		// check all sides to make sure they aren't out of bounds
		if(x-1 > 0){
			// we are all good to the left
			if(data[x-1][y] == WATER){
				// if its water convert us to a border.
				border[x-1][y] = colour;
			}
		}
		if(x+1 < WIDTH){
			// we are all good to the right
			if(data[x+1][y] == WATER){
				// if its water convert us to a border.
				border[x+1][y] = colour;
			}
		}
		if(y-1 > 0){
			// we are all good above 
			if(data[x][y-1] == WATER){
				// if its water convert us to a border.
				border[x][y-1] = colour;
			}
		}
		if(y+1 < HEIGHT){
			// we are all good below.
			if(data[x][y+1] == WATER){
				// if its water convert us to a border.
				border[x][y+1] = colour;
			}
		}
	}
	
	public void render(ShapeRenderer batch){
		batch.setColor(Color.WHITE);
		batch.rect(0, 0,800,600);
		for(int x = 0; x < WIDTH; x++){
			for(int y = 0; y < HEIGHT; y++){
				if(data[x][y] == TEMP){
					batch.setColor(Color.RED);
					batch.rect(x, y,1,1);
				}
				
				if(border[x][y] == GROUND){
					batch.setColor(Color.RED);
					batch.rect(x, y,1,1);
				}
				if(border[x][y] == GREEN){
					batch.setColor(Color.GREEN);
					batch.rect(x, y,1,1);
				}
				if(border[x][y] == BLUE){
					batch.setColor(Color.BLUE);
					batch.rect(x, y,1,1);
				}

			}
		}
		
	}
}

package uk.slowpoke.ld38.world;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Island {
	
	public ArrayList<Vector2> data = new ArrayList<Vector2>();
	
	// as in 0 being no owner.
	public int OWNER = 0;
	
	public Island(ArrayList<Vector2> data){
		this.data = data;
	}
	
	public void assign(int OWNER){
		this.OWNER = OWNER;
	}
}

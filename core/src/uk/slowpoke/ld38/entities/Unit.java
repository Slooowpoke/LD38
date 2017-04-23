package uk.slowpoke.ld38.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import uk.slowpoke.ld38.world.Island;
import uk.slowpoke.ld38.world.Map;

public class Unit extends Entity{
	
	boolean selected;
	Vector2 target;
	Unit enemy;
	int range;// the sight the unit has.
	float speed;
	
	// Declared as fields, so they will be reused
	Vector2 position = new Vector2();
	private Vector2 velocity = new Vector2();
	Vector2 movement = new Vector2();
	Vector2 dir = new Vector2();
	
	public Vector2 v1 = new Vector2();
	Vector2 v2 = new Vector2();
	public Vector2 v3 = new Vector2();

	Player owner;
	boolean stopped = false;
	
	public Unit(float x, float y,int range,Player owner) {
		super(x, y, 1, 1, 100, 100);
		this.range = range;
		this.owner = owner;
	}

	public void update(float delta,Map map){

		if(target != null){

			if(!isNear(target.x,target.y,10) && !stopped){
				position.x = getX();
				position.y = getY();
				// if the are not near the target
//				float dx = (float) ((target.x-getX())*0.001);
//				float dy = (float) ((target.y-getY())*0.001);
				

				velocity = new Vector2(dir).scl(3f);
//				velocity.add(v1);
//				velocity.add(v2);
//				velocity.add(v3);
//				velocity.add(dir).scl(3f);
//				System.out.println(velocity);
				
				velocity.limit(5f);
				
				movement.set(velocity).scl(delta);
				
				// this position needs to be checked against the collision map things.
//				for(Island i: map.getIslands()){
//					for(Vector2 v: i.data){
//						Vector2 d = position.add(movement);
//					
//						if(d.dst(v.x, v.y) < 5){
//							
//							stopped = true;
//							break;
//						}
//					}
//				}
//				if(!stopped){
//
//				}
				position.add(movement);
				
				setX(position.x);
				setY(position.y);

			}else{
				// near the target
				System.out.println("Near the target.");
//				target = null;
			}
		}
	}
	
	public void render(SpriteBatch batch){
//		if(selected){
//			batch.setColor(Color.BROWN);
//		}else{
//			batch.setColor(Color.BLUE);
//		}
//		batch.rect(getX(), getY(),getWidth(),getHeight());
	}
	
	public void setTarget(float x,float y){
		target = new Vector2(x,y);
	
		// On touch events, set the touch vector, then do this to get the direction vector
		dir.set(target).sub(position).nor();
	}

	public boolean checkNearUnits(ArrayList<Unit> units) {
		// checks if we are near any of these units
		for(Unit unit: units){
			if(this.isNear(unit.getX(), unit.getY(), range)){
				enemy = unit;
//				System.out.println("I HAVE AN ENEMY");
			}
		
		}
		return false;
	}
	
	public void steerAway(Unit otherUnit){

		if(!otherUnit.equals(this)){
			avoid(otherUnit.position,20);
		}else{
//			System.out.println("ITS THE SAME");
		}
	}

	public void avoid(Vector2 vector,float distance){
		if(position.dst(vector) < distance){
			v2 = vector.sub(position);
//			v2 = vector.sub(position).nor();
		}else{

		}
	}
	
	public void matchVelocity(Unit otherUnit){

	}
	

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void stopMoving() {
		stopped = true;

	}

	
}

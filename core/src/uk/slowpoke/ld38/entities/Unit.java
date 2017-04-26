package uk.slowpoke.ld38.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import uk.slowpoke.ld38.Game;
import uk.slowpoke.ld38.world.Light;
import uk.slowpoke.ld38.world.Map;

public class Unit extends Entity{
	
	boolean selected;
	Vector2 target;
	Unit enemy;
	private int range;// the sight the unit has.
	float speed;
	int reloadTime = 0;
	int cooldown = 0;
	boolean bulletFired = false;
	
	boolean immune;
	int immuneTimeout = 0;
	boolean dead = false;
	
	
	// Declared as fields, so they will be reused
	Vector2 position = new Vector2();
	private Vector2 velocity = new Vector2();
	Vector2 movement = new Vector2();
	Vector2 dir = new Vector2();
	
	public Vector2 v1 = new Vector2();
	Vector2 v2 = new Vector2();
	public Vector2 v3 = new Vector2();

	protected Player owner;
	boolean stopped = false;
	public Light light;
	
	public boolean tempShowDirection = false;
	int directionTimeout = 0;
	
	public Unit(float x, float y,int width,int height,int range,Player owner,float speed,int reloadTime) {
		super(x, y, width, height, 100, 100);
		this.setRange(range);
		this.setOwner(owner);
		this.reloadTime = reloadTime;
		this.speed = speed;
		light = new Light(getX()-getWidth(),getY()-getHeight(),32,new Texture("light.png"));
	}

	public void update(float delta,Map map){

	
		if(bulletFired){
			cooldown++;
			if(cooldown > reloadTime){
				cooldown = 0;
				bulletFired = false;
			}
		}
		if(immune){
			immuneTimeout++;
			if(immuneTimeout > 5){
				immune = false;
			}
		}
		
		if(getHp() < 0){
			dead = true;
		}
		
		
		if(target != null){
			position.x = getX();
			position.y = getY();
			if(position.x > Map.WIDTH){
				// wrap them
				position.x = 0;
			}
			if(position.x < 0){
				position.x = Map.WIDTH;
			}
			if(position.y > Map.HEIGHT){
				position.y = 0;
			}
			if(position.y < 0){
				position.y = Map.HEIGHT;
			}
			
			if(!isNear(target.x,target.y,10) && !stopped){

				
				light.setX(getX());
				light.setY(getY());
				// if the are not near the target
//				float dx = (float) ((target.x-getX())*0.001);
//				float dy = (float) ((target.y-getY())*0.001);
				
//				position.x += (speed/10 * Math.sin(getAngle(target)));
//			    position.y += (speed/10 * Math.cos(getAngle(target)));

				velocity = new Vector2(dir).scl(8f);
//				velocity.add(v1);
//				velocity.add(v2);
//				velocity.add(v3);
//				velocity.add(dir).scl(3f);
//				System.out.println(velocity);
				
				velocity.limit(10f);
				
				movement.set(velocity).scl(delta);
				
				// this position needs to be checked against the collision map things.
//				for(Island i: map.getIslands()){
//					for(Vector2 v: i.data){z
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
//				System.out.println("Near the target.");
//				target = null;
			}
		}
	}
	
	public double getAngle(Vector2 target) {
	    float angle = (float) Math.toDegrees(Math.atan2(target.y - getY(), target.x - getX()));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
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
		System.out.println(target);
		// On touch events, set the touch vector, then do this to get the direction vector
		dir.set(target).sub(position).nor();
	}

	public boolean checkNearUnits(ArrayList<Unit> units) {
		
		// checks if we are near any of these units
		for(Unit unit: units){
			if(this.isNear(unit.getX(), unit.getY(), getRange()) && !unit.isDead()){
				enemy = unit;
								
				Vector2 target = new Vector2(unit.getX(),unit.getY());
				Vector2 direction = new Vector2();
				direction.set(target).sub(new Vector2(getX(),getY()));
				
				if(!bulletFired){
					Game.fireProjectile(getX(), getY(), getOwner(), direction);
					bulletFired = true;
				}

//				System.out.println("I HAVE AN ENEMY");
			}
		
		}
		return false;
	}
	
	public void steerAway(Unit otherUnit){
		if(otherUnit != null){
			if(!otherUnit.equals(this)){
				avoid(otherUnit.position,20);
			}else{
//				System.out.println("ITS THE SAME");
			}
		}
	}
	
	public void renderDirection(ShapeRenderer batch){
		if(tempShowDirection){
			batch.line(position, target);
			
			directionTimeout++;
			if(directionTimeout > 10){
				tempShowDirection = false;
				directionTimeout = 0;
			}
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
	
	public void isHit(Projectile p){
		// if its hit, trigger
		if(p.isNear(getX(),getY(), 2)){
			// its hit!
			if(!immune){
				// if not immune
				immune = true;
				setHp(getHp()-10);
				p.setDead(true);
			}
		}
	}

	public boolean isDead() {
		return dead;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	
}

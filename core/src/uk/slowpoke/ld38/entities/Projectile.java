package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import uk.slowpoke.ld38.world.Map;

public class Projectile extends Entity{

	Sprite image;
	private Player owner;
	Vector2 direction = null;
	Vector2 position = new Vector2();
	int deathCounter = 0;
	private boolean dead = false;
	
	public Projectile(float x, float y,Player owner,Vector2 direction) {
		super(x, y, 1,1,10,5);
		this.image = new Sprite(new Texture("bullet.png"));
		this.setOwner(owner);
		this.direction = direction;
	}
	
	public void render(SpriteBatch batch){
		batch.draw(image, getX(), getY());
	}
	
	public void update(float delta,Map map){
		if(direction != null){
			position.x = getX();
			position.y = getY();
			
			Vector2 velocity = new Vector2(direction).scl(1f);
//			velocity.limit(5f);
			Vector2 movement = new Vector2();
			movement.set(velocity).scl(delta);
			position.add(movement);
			
			setX(position.x);
			setY(position.y);
			deathCounter++;
			if(deathCounter > 200){
				setDead(true);
				// set dead causes explosion?
			}
		}
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

}

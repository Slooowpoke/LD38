package uk.slowpoke.ld38.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Entity {
	private float x,y;
	private float width;
	private float height;
	//
	private int hp;
	protected int damage;
	public boolean immune;
	
	public Entity(float x,float y, float width,float height,int hp,int damage){
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		
		this.setHp(hp);
		this.setDamage(damage);
	}
	
	public void renderCollision(ShapeRenderer batch) {
		batch.rect(x, y,width,height);
	}
	
	public boolean collidesWith(Entity e){
		Rectangle a = new Rectangle(e.getX(),e.getY(),e.getWidth(),e.getHeight());
		Rectangle b = new Rectangle(getX(),getY(),getWidth(),getHeight());
		if(a.overlaps(b)){
			return true;
		}else{
			return false;
		}
	}
	

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isNear(float x,float y,float f,float g,double d){
		double distance = Math.sqrt( (x-f)*(x-f) + (y-g)*(y-g));
		if(distance < d){

			return true;
		}else{
			return false;
		}
	}

	public boolean isNear(float f,float g,double d){
		double distance = Math.sqrt( (getX()-f)*(getX()-f) + (getY()-g)*(getY()-g));

		if(distance < d){
			return true;
		}else{
			return false;
		}
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}
	
}

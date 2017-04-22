package uk.slowpoke.ld38;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import uk.slowpoke.ld38.entities.Player;
import uk.slowpoke.ld38.world.Map;


public class Game extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	ShapeRenderer shapeBatch;
	
	// CAMERA VARIABLES --
	
	OrthographicCamera camera;
	int x,y;
	boolean up,down,left,right;
	float zoomTarget = 0.5f;
	
	// --
	
	Map map;
	
	public static ArrayList<Player> players = new ArrayList<Player>();

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		
		players.add(new Player("Mali",new Color(Color.RED)));
		players.add(new Player("BlueTeam",new Color(Color.BLUE)));
		map = new Map();
		camera = new OrthographicCamera(30, 30 * (Gdx.graphics.getHeight() / Gdx.graphics.getWidth()));
//		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);

	} 

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		shapeBatch.setProjectionMatrix(camera.combined);
		shapeBatch.begin(ShapeType.Filled);
		map.render(shapeBatch);
		shapeBatch.end();
	}
	

	public void update(float d){
		
		// all camera shizzle
		float lerp = 0.05f;
		Vector3 position = camera.position;
		float dx = 0, dy = 0,speed = 3f;
		speed *= (camera.zoom*10)/2f;
		
		if(up){
			dy += speed;
		}else if(down){
			dy -= speed;
		}
		if(left){
			dx -= speed;
		}else if(right){

			dx += speed;
		}
		x += dx*(d*50f);
		y += dy*(d*50f);
		
		
		camera.position.x += (x - position.x) * lerp;
		camera.position.y += (y - position.y) * lerp;

//		camera.position.x += ((camera.position.x+shakeX)-camera.position.x)*0.1;
//		camera.position.y += ((camera.position.y+shakeY)-camera.position.y)*0.1;

//		if(!map.isHover()){
//			camera.position.x = MathUtils.clamp(camera.position.x, Gdx.graphics.getWidth()/2, 100);
//			camera.position.y = MathUtils.clamp(camera.position.y, Gdx.graphics.getHeight()/2, 100);
//		}
		
		camera.zoom += (zoomTarget-camera.zoom)*0.1f;
//		camera.zoom = MathUtils.clamp(camera.zoom, 0.1f, 100/camera.viewportWidth);
		camera.update();
//		float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
//		float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

//		camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2, 800 - effectiveViewportWidth / 2f);
//		camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2, 600 - effectiveViewportHeight / 2f);

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		shapeBatch.dispose();
	}

	@Override
	public boolean keyDown(int key) {
		if(key == Keys.A || key == Keys.LEFT){
			left = true;
		}
		if(key == Keys.D || key == Keys.RIGHT){
			right = true;
		}
		if(key == Keys.S || key == Keys.DOWN){
			down = true;
		}
		if(key == Keys.W || key == Keys.UP){
			up = true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int key) {
		if(key == Keys.A || key == Keys.LEFT){
			left = false;
		}
		if(key == Keys.D || key == Keys.RIGHT){
			right = false;
		}
		if(key == Keys.S || key == Keys.DOWN){
			down = false;
		}
		if(key == Keys.W || key == Keys.UP){
			up = false;
		}

		
		return false;
	}
	
	

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub

		if(amount == 1){
			if(zoomTarget <= 1) zoomTarget+=0.1f;
		}else if(amount == -1){
			if(zoomTarget > 0.2f) zoomTarget-=0.1f;
		}
		return false;
	}
}

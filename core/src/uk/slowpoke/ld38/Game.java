package uk.slowpoke.ld38;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


import uk.slowpoke.ld38.entities.AI;
import uk.slowpoke.ld38.entities.Player;
import uk.slowpoke.ld38.entities.Projectile;
import uk.slowpoke.ld38.entities.Unit;
import uk.slowpoke.ld38.world.Light;
import uk.slowpoke.ld38.world.Map;


public class Game extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	ShapeRenderer shapeBatch;
	
	// UI CONTROLS --
	
	boolean placingUnits = false;
	int unitType = 0;
	public static final int HQ = 0;
	public static final int BOMBER = 1;
	public static final int SCOUT = 2;
	public static final int MISSLE_LAUNCHER = 3;
	public static final int ARTILLERY = 4;
	
	// --
	
	// CAMERA VARIABLES --
	
	OrthographicCamera camera;
	int x,y;
	boolean up,down,left,right;
	float zoomTarget = 0.5f;
	
	// --

	// MOUSE VAIRABLES --
	
	Rectangle selection = new Rectangle(){
	    public boolean overlaps (Rectangle r) {
	        return Math.min(x, x + width) < Math.max(r.x, r.x + r.width) && Math.max(x, x + width) > Math.min(r.x, r.x + r.width) && Math.min(y, y + height) < Math.max(r.y, r.y + r.height) && Math.max(y, y + height) > Math.min(r.y, r.y + r.height);
	    }
	};
	
	Vector2 mouse = new Vector2(0,0);
	int lastButton = 0;

	// --
	
	
	Map map;
	
	public static ArrayList<Player> players = new ArrayList<Player>();

	public Player localPlayer;
	int totalPlayers = 2;
	
	static ArrayList<Projectile> bullets = new ArrayList<Projectile>();

	// LIGHTS / FOG OF WAR --
	
	ArrayList<Light> lights = new ArrayList<Light>();
	
	public static float ambientIntensity = 0.1f;
	public static Vector3 ambientColor = new Vector3(0.2f, 0.1f, 1f);

	String vertexShader;
	String pixelShader;
	String defaultPixelShader;
	private ShaderProgram shader,defaultShader;

	private FrameBuffer fbo;
	private Texture light,playerLight;
	
	Vector2 lightDebug = new Vector2();
	float darkTarget = 1f;
	
	
	// --

	// ANOTHER FRAMEBUFFER FOR RENDERING THE MAP OFFSCREEN -- 
	
	private float m_fboScaler = 1f;
	private boolean m_fboEnabled = true;
	private FrameBuffer m_fbo = null;
	private TextureRegion m_fboRegion = null;
	
	// --
	
	// ROUND COUNTING LOGIC -- 
	
	public int roundCounter = PLACE_HQ;
	public static final int PLACE_HQ = 0;
	public static final int PLACE_BUILDINGS = 1;
	public static final int DEPLOY_SCOUTS = 2;
	public static final int FULL_FIGHTING = 3;
	public static final int GAMEOVER = 4;// when your HQ is gone, or when no other HQ's exist
	public static final int MENU = 5;// twitter, website and game title go here.
	
	// --
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeBatch = new ShapeRenderer();
		map = new Map();
		
		players.add(new Player("Mali",new Color(Color.RED),map));
		players.add(new AI("Player 2",new Color(Color.PURPLE),map));
		map.generateIslands();
		
		for(Player player: players){
			if (player.getClass() == AI.class) {
				((AI) player).placeHQ();
			}
		}
		
		
		camera = new OrthographicCamera(30, 30 * (Gdx.graphics.getHeight() / Gdx.graphics.getWidth()));
//		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.input.setInputProcessor(this);
		
		lights.add(new Light(x,y,20,new Texture("light.png")));
		
		// SHADER INTIALISATION and framebuffer too.
		vertexShader = Gdx.files.internal("shaders/vertexShader.glsl").readString();
		pixelShader  =  Gdx.files.internal("shaders/pixelShader.glsl").readString(); 
		defaultPixelShader  =  Gdx.files.internal("shaders/defaultPixelShader.glsl").readString(); 
		light = new Texture("light.png");

		defaultShader = new ShaderProgram(vertexShader, defaultPixelShader);
		shader = new ShaderProgram(vertexShader, pixelShader);
		
		shader.begin();
		shader.setUniformi("u_lightmap", 1);
		shader.setUniformf("ambientColor", ambientColor.x, ambientColor.y,
				ambientColor.z, ambientIntensity);
		shader.end();
		
	} 

	
	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Matrix4 defaultProjectionMatrix = batch.getProjectionMatrix();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shader.begin();
		shader.setUniformi("u_lightmap", 1);


		ambientIntensity += (darkTarget-ambientIntensity)*0.1f;
		
 
		shader.setUniformf("ambientColor",1f, 1f,
				1f, ambientIntensity);
		shader.end();
		
		fbo.begin();
			
			batch.setProjectionMatrix(camera.combined);
			batch.setShader(defaultShader);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			batch.begin();
		
				for(Light light:lights){
//					light.render(batch);
				}
				for(Unit unit: getPlayerByName("Mali").getUnits()){
					unit.light.render(batch);
//					new Light(unit.getX()-unit.getWidth(),unit.getY()-unit.getHeight(),32,new Texture("light.png")).render(batch);;
				}
			
			batch.setColor(1f, 1f, 1f, 1f);;
			batch.end();
		fbo.end();
	
		int width = Gdx.graphics.getWidth();
	    int height = Gdx.graphics.getHeight();

	    if(m_fboEnabled)      // enable or disable the supersampling
	    {          

	    	 m_fbo = new FrameBuffer(Format.RGB565, (int)(width * m_fboScaler), (int)(height * m_fboScaler), false);
			m_fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
//     
            m_fboRegion.flip(false, true);
	        m_fbo.begin();
	        
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		    // this is the main render function
	        
			shapeBatch.begin(ShapeType.Filled);
			
			map.render(shapeBatch);

			shapeBatch.end();
		    m_fbo.end();
		    m_fboEnabled = false;
	    }
	    
		batch.setProjectionMatrix(camera.combined);


		shapeBatch.setProjectionMatrix(camera.combined);
	
		shapeBatch.begin(ShapeType.Filled);

//		map.render(shapeBatch);

		shapeBatch.end();
		batch.setShader(shader);
		batch.begin();
		fbo.getColorBufferTexture().bind(1); 
		light.bind(0); 
		
		batch.draw(m_fboRegion, 0, 0);     
		
		// do not render units, until round is 2.
		if(roundCounter > PLACE_HQ){
			
			for(Unit ourUnit: getPlayerByName("Mali").getUnits()){
				ourUnit.render(batch);
				
				for(Player player: players){
					if(!player.equals(getPlayerByName("Mali"))){
						
						ArrayList<Unit> units = player.getUnits();
						for(Unit unit: units){
							if(ourUnit.isNear(unit.getX(), unit.getY(), ourUnit.getRange())){
								unit.render(batch);
							}
						}
					}
				}
				
			}
			
//			for(Player player: players){
//				ArrayList<Unit> units = player.getUnits();
//				for(Unit unit: units){
//					unit.render(batch);
//				}
//			}
			for(Projectile p: bullets){
				p.render(batch);
			}
		}

		batch.end();
		batch.setShader(defaultShader);

		
		
		

		batch.begin();

//		batch.draw(fog, 0,0);
		
		batch.end();
		
		shapeBatch.begin(ShapeType.Line);
		shapeBatch.setColor(1,1,1,0.2f);
		shapeBatch.rect(selection.x,selection.y,selection.width,selection.height);
		shapeBatch.end();
		
		// render GUI fonts here
		// placingUnits especially.
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

		
		for(Player player: players){
			player.update(d);
			
		}
		
		// pass units to each unit so it can check if its near any of them
		for(Player player: players){
//			player.update(d);
			
			for(Unit unit: player.getUnits()){
				unit.update(d,map);
				// check is hit
				for(Projectile p: bullets){
					if(!unit.getOwner().equals(p.getOwner())){
						unit.isHit(p);
					}
					
				}
				// we only want to match the velocity's of our boids
				
				
				for(Player differentPlayer: players){
					Vector2 pvJ = new Vector2();
					
					for(Unit otherUnit: differentPlayer.getUnits()){
						// check against all units unless its the same one
						unit.steerAway(otherUnit);
					}
					
					
					// if it does not equal our current player
					if(!differentPlayer.equals(player)){
						// then we can check against this players units.

						if(unit.checkNearUnits(differentPlayer.getUnits())){
							
						}
						
					}else{
						// the same player
						for(Unit otherUnit: differentPlayer.getUnits()){
							if(!otherUnit.equals(this)){
								pvJ.add(otherUnit.getVelocity());
							}
						}
						double totalUnits = 1 / (differentPlayer.getUnits().size());
						
						unit.v1.mulAdd(pvJ, 0.10f);
						
						pvJ.mulAdd(pvJ, (float) totalUnits);
						
						unit.v3.mulAdd((pvJ.sub(unit.getVelocity())), 0.08f);
					}
				}
			}

		}
		
		
		
		for(Projectile p: bullets){
			p.update(d,map);
		}
		
		// clean up dead bullets
		ArrayList<Projectile> newBullets = new ArrayList<Projectile>();
		
		for(Projectile p: bullets){
			if(!p.isDead()){
				newBullets.add(p);
			}
		}
		bullets = newBullets;
		
		for(Player player: players){
			// clean up dead units
			ArrayList<Unit> newUnits = new ArrayList<Unit>();
			
			for(Unit u: player.getUnits()){
				if(!u.isDead()){
					newUnits.add(u);
				}
			}
			player.setUnits(newUnits);
		}
	
		roundHandler();
	}
	
	public void roundHandler(){
		if(roundCounter == 0){
			boolean advanceRound = true;// we can move forward if everyone places their HQ
			// allow HQ's to be placed first
			for(Player p: players){
				if(!p.hasPlacedHQ()){
					advanceRound = false;
				}
			}
			if(advanceRound){
				darkTarget = 0.2f;
			}
			
			if(advanceRound && ambientIntensity <= 0.3f){
				advanceRound();
			}
		}	
	}
	
	public void advanceRound(){
		roundCounter++;
		System.out.println("IT IS NOW ROUND: " + roundCounter);
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
		if(key == Keys.SHIFT_LEFT){
			placingUnits = !placingUnits;
		}
		if(roundCounter == PLACE_HQ){
			if(key == Keys.NUM_1){
				unitType = HQ;
			}
		}else if(roundCounter == PLACE_BUILDINGS){
			if(key == Keys.NUM_1){
				unitType = MISSLE_LAUNCHER;
			}
			if(key == Keys.NUM_2){
				unitType = ARTILLERY;
			}
		}else if(roundCounter == DEPLOY_SCOUTS){
			if(key == Keys.NUM_1){
				unitType = SCOUT;
			}
		}else if(roundCounter == FULL_FIGHTING){// can also launch missles
			if(key == Keys.NUM_1){
				unitType = BOMBER;
			}
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
		Vector3 position = camera.unproject(new Vector3(screenX, screenY, camera.zoom));
		mouse.x = (int) (position.x);
		mouse.y = (int) (position.y);

		if(getPlayerByName("Mali") != null && button == 0 && placingUnits){
			// only add units if in that mode
			getPlayerByName("Mali").addUnit((int)position.x, (int)position.y,unitType);
		}else if(!placingUnits){
			// it could be a command to send them
			getPlayerByName("Mali").target(mouse.x,mouse.y);
		}
		
		// reset selection
		if(button == 1){
			selection.x = position.x;
			selection.y = position.y;
		}else{
			selection.x = 0;
			selection.y = 0;
		}

		lastButton = button;
		selection.width = 0;
		selection.height = 0;

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector3 position = camera.unproject(new Vector3(screenX, screenY, camera.zoom));
		
		if(button == 1){
			selection.width = position.x-selection.x;
			selection.height = position.y-selection.y;
		}

		if(getPlayerByName("Mali") != null){
			getPlayerByName("Mali").selectShips(selection);
		}
		
		selection.width = 0; selection.height = 0;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector3 position = camera.unproject(new Vector3(screenX, screenY, camera.zoom));
		if(lastButton == 1){
			selection.width = position.x-selection.x;
			selection.height = position.y-selection.y;
		}
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector3 position = camera.unproject(new Vector3(screenX, screenY, camera.zoom));
		// TODO Auto-generated method stub
//		lights.get(0).setX(position.x);
//		lights.get(0).setY(position.y);
		lightDebug.x = position.x;
		lightDebug.y = position.y;
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
	
	public Player getPlayerByName(String name){
		for(Player player: players){
			if(player.getName().equals(name)){
				return player;
			}
		}
		return null;
	}
	
	public static void fireProjectile(float x,float y,Player owner,Vector2 direction){
		bullets.add(new Projectile(x,y,owner,direction));
	}
	
	public void resize(final int width, final int height) {
		shader.begin();
		shader.setUniformf("resolution", width, height);
		shader.end();
		fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
	}
}

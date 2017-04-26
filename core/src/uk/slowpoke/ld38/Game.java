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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import uk.slowpoke.ld38.entities.Airbase;
import uk.slowpoke.ld38.entities.Player;
import uk.slowpoke.ld38.entities.Projectile;
import uk.slowpoke.ld38.entities.Unit;
import uk.slowpoke.ld38.world.Light;
import uk.slowpoke.ld38.world.Map;


public class Game extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	ShapeRenderer shapeBatch;
	SpriteBatch guiBatch;
	
	// UI CONTROLS --
	
	boolean placingUnits = true;
	int unitType = 0;
	public static final int HQ = 0;
	public static final int BOMBER = 1;
	public static final int SCOUT = 2;
	public static final int MISSLE_LAUNCHER = 3;
	public static final int ARTILLERY = 4;
	public static final int AIRBASE = 5;
	
	public ArrayList<Button> buttons = new ArrayList<Button>();


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
	public static Vector3 ambientColor = new Vector3(1f, 1f, 1f);

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
	
	public int roundCounter = MENU;
	public static final int PLACE_HQ = 0;
	public static final int PLACE_BUILDINGS = 1;
	public static final int DEPLOY_SCOUTS = 2;
	public static final int FULL_FIGHTING = 3;
	public static final int GAMEOVER = 4;// when your HQ is gone, or when no other HQ's exist
	public static final int MENU = 5;// twitter, website and game title go here.
	
	// --
	
	// FONTS --
	
	public static BitmapFont tiny,small,medium,largeish,large;
		
	// --
	
	float fade = 0f, fadeTarget = 0f;
	boolean COMPLETED_GAME = false;
	
	public void resetEverything(){
		create();
	}
	
	@Override
	public void create () {
		Gdx.graphics.setTitle("LD38 - Slowpoke - It's a small world");
		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
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
		tiny = new BitmapFont(Gdx.files.internal("fonts/font-10.fnt"));
		small = new BitmapFont(Gdx.files.internal("fonts/small.fnt"));
		medium = new BitmapFont(Gdx.files.internal("fonts/font-21.fnt"));
		largeish = new BitmapFont(Gdx.files.internal("fonts/largeish.fnt"));
		large = new BitmapFont(Gdx.files.internal("fonts/font-32.fnt"));
		
		camera = new OrthographicCamera(30, 30 * (Gdx.graphics.getHeight() / Gdx.graphics.getWidth()));
//		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		x = Map.WIDTH/2;
		y = Map.HEIGHT/2;
		
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

		buttons.add(new Button(32,32,HQ));
		buttons.add(new Button(32,32,MISSLE_LAUNCHER));
		buttons.add(new Button(64,32,ARTILLERY));
		buttons.add(new Button(96,32,AIRBASE));
	} 

	
	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		fade += (fadeTarget-fade)*0.1f;
		if(roundCounter == MENU){
			camera.zoom = 1f;
			Gdx.gl.glClearColor(0.13f, 0.13f, 0.13f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			guiBatch.begin();
			GlyphLayout layout = new GlyphLayout();
			large.setColor(Color.WHITE);
			layout.setText(Game.large,"It's a small world.");	
			large.draw(guiBatch, "It's a small world.", 20,50);

			layout.setText(Game.medium,"Press [ SPACE [ to start.");	
	
			medium.draw(guiBatch, "Press [SPACE[ to start.", 20,120);
			guiBatch.end();
			
	
		}else if(roundCounter == GAMEOVER){
			camera.zoom = 1f;
			Gdx.gl.glClearColor(0.13f, 0.13f, 0.13f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			Gdx.gl.glClearColor(0.13f, 0.13f, 0.13f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			guiBatch.begin();
			GlyphLayout layout = new GlyphLayout();

			String text = "";
			if(COMPLETED_GAME){
				text = "You won! To-dah!";
			}else{
				text = "You're HQ was blown up! Rats.";
			}
			layout.setText(Game.medium,text);	
			medium.setColor(Color.WHITE);
			medium.draw(guiBatch, text, (Gdx.graphics.getWidth()/2)-layout.width/2,Gdx.graphics.getHeight()/2);
			
			// if the failed
			if(!COMPLETED_GAME){
				text = "Sorry you're going to have restart the game to play again.";
				layout.setText(Game.medium,text);	
				medium.setColor(Color.WHITE);
				medium.draw(guiBatch, text, (Gdx.graphics.getWidth()/2)-layout.width/2,(Gdx.graphics.getHeight()/2)-50);
			}
			
			
			guiBatch.end();
			
		}else{
			Matrix4 defaultProjectionMatrix = batch.getProjectionMatrix();
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			shader.begin();
			shader.setUniformi("u_lightmap", 1);


			ambientIntensity += (darkTarget-ambientIntensity)*0.1f;
			
	 
			shader.setUniformf("ambientColor",ambientColor.x, ambientColor.y,
					ambientColor.z, ambientIntensity);
			shader.end();
			
			fbo.begin();
				
				batch.setProjectionMatrix(camera.combined);
				batch.setShader(defaultShader);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
				batch.begin();
			
					for(Light light:lights){
//						light.render(batch);
					}
					for(Unit unit: getPlayerByName("Mali").getUnits()){
						unit.light.render(batch);
//						new Light(unit.getX()-unit.getWidth(),unit.getY()-unit.getHeight(),32,new Texture("light.png")).render(batch);;
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

//			map.render(shapeBatch);

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
					if(ourUnit.getClass() == Airbase.class){
						((Airbase) ourUnit).renderQuantity(batch);
					}
					
					for(Player player: players){
						if(!player.equals(getPlayerByName("Mali"))){
							
							ArrayList<Unit> units = player.getUnits();
							for(Unit unit: units){
								if(ourUnit.isNear(unit.getX(), unit.getY(), ourUnit.getRange())){
									unit.render(batch);
								}
								
//								unit.render(batch);// uncomment to view all units
							}
						}
					}
					
				}
				
//				for(Player player: players){
//					ArrayList<Unit> units = player.getUnits();
//					for(Unit unit: units){
//						unit.render(batch);
//					}
//				}
				for(Projectile p: bullets){
					p.render(batch);
				}
			}

			batch.end();
			batch.setShader(defaultShader);

			
			
			

			batch.begin();

//			batch.draw(fog, 0,0);
			
			batch.end();
			
			shapeBatch.begin(ShapeType.Line);
			shapeBatch.setColor(1,1,1,0.2f);
			shapeBatch.rect(selection.x,selection.y,selection.width,selection.height);
			
			for(Unit unit: getPlayerByName("Mali").getUnits()){
				unit.renderDirection(shapeBatch);
			}
			shapeBatch.end();
			
			
			
			
			// render GUI fonts here
			// placingUnits especially.
			guiBatch.begin();

			GlyphLayout layout = new GlyphLayout();
			large.setColor(Color.WHITE);
			if(roundCounter == PLACE_HQ){
				layout.setText(Game.large,"Place your Headquarters");	
				large.draw(guiBatch, "Place your Headquarters", 20,80);
			}else if(roundCounter == PLACE_BUILDINGS){
				layout.setText(Game.large,"Place your Buildings");	
				large.draw(guiBatch, "Place your Buildings", 20,80);
			}else if(roundCounter == DEPLOY_SCOUTS){
				layout.setText(Game.large,"Deploy 5 Scouts");	
				large.draw(guiBatch, "Deploy 5 Scouts", 20,80);
			}else if(roundCounter == FULL_FIGHTING){
				layout.setText(Game.large,"Attack!");	
				large.draw(guiBatch, "Attack!", 20,80);
				
			}
			tiny.setColor(Color.WHITE);
			for(Button button: buttons){
				if(roundCounter == PLACE_HQ && button.type == PLACE_HQ){
					button.render(guiBatch);
				}else if(roundCounter == PLACE_BUILDINGS
						&& (button.type == MISSLE_LAUNCHER
						|| button.type == ARTILLERY
						|| button.type == AIRBASE)){
					button.render(guiBatch);
					Player counterPlayer = getPlayerByName("Mali");
					if(button.type == MISSLE_LAUNCHER){
						// render how many you have
						layout.setText(Game.small,(5-counterPlayer.totalMissleLaunchers) + "/5");	
						small.draw(guiBatch, (5-counterPlayer.totalMissleLaunchers) + "/5", button.x,button.y);
					}else if(button.type == ARTILLERY){
						layout.setText(Game.small,(5-counterPlayer.totalArtillery) + "/5");	
						small.draw(guiBatch, (5-counterPlayer.totalArtillery) + "/5", button.x,button.y);
					}else if(button.type == AIRBASE){
						layout.setText(Game.small,(5-counterPlayer.totalAirbase) + "/5");	
						small.draw(guiBatch,(5-counterPlayer.totalAirbase) + "/5", button.x,button.y);
					}
				}
			}
			
			guiBatch.end();
//			System.out.println(roundCounter);
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeBatch.begin(ShapeType.Filled);
		shapeBatch.setColor(new Color(0f,0f,0f,fade));
		shapeBatch.rect(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
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
				}else{
					if(u.getClass() == uk.slowpoke.ld38.entities.HQ.class){
						u.getOwner().hqDead = true;
					}
				}
			}
			player.setUnits(newUnits);
		}
	
		roundHandler();
		
	}
	
	public void roundHandler(){
		if(roundCounter == MENU){
			// then we need 
			if(fade > 0.9f){
				roundCounter = PLACE_HQ;
			}
		}
		else if(roundCounter == PLACE_HQ){
			fadeToColour();
			boolean advanceRound = true;// we can move forward if everyone places their HQ
			// allow HQ's to be placed first
			for(Player p: players){
				if(!p.hasPlacedHQ()){
					advanceRound = false;
				}
			}
			if(advanceRound){
				darkTarget = 0.4f;
			}
			
			if(advanceRound && ambientIntensity <= 0.5f){
				for(Player player: players){
					if (player.getClass() == AI.class) {
						((AI) player).placeBuildings();
					}
				}
				advanceRound();
			}
		}else if(roundCounter == PLACE_BUILDINGS){
			boolean advanceRound = true;// we can move forward if everyone places their HQ
			// allow HQ's to be placed first
			for(Player p: players){
				if(!p.placedAllBuildings()){
					advanceRound = false;
				}
			}

			
			if(advanceRound){
				for(Player player: players){
					if (player.getClass() == AI.class) {
						((AI) player).deployScouts();
					}
				}
				placingUnits = false;
				advanceRound();
			}
		}else if(roundCounter == DEPLOY_SCOUTS){
			boolean advanceRound = true;// we can move forward if everyone places their HQ
			// allow HQ's to be placed first
			for(Player p: players){
				if(!p.deployedAllScouts()){
					advanceRound = false;
				}
			}

			
			if(advanceRound){
				for(Player player: players){
					if (player.getClass() == AI.class) {
						((AI) player).deployAllUnits();
					}
				}
				
				advanceRound();
			}
		}
		else if(roundCounter == FULL_FIGHTING){
			Player player = getPlayerByName("Mali");
			boolean youwon = true;
			for(Player p: players){
				
				if(!p.equals(player)){
					if(!p.hqDead){
						// if they still have a hq and aren't us then you haven't won yet.
						youwon = false;
					}
				}else{
					if(player.hqDead){
						System.out.println("YOU DEAD");
						fadeToBlack();
						if(fade > 0.9f){
							COMPLETED_GAME = false;
							roundCounter = GAMEOVER;
						}
					}
				}
			}
			if(youwon){
				
				System.out.println("YOU WON!");
				fadeToBlack();
				if(fade > 0.9f){
					roundCounter = GAMEOVER;
					COMPLETED_GAME = true;
				}
			}
		}else if(roundCounter == GAMEOVER){
			fadeToColour();
//			if(fade > 0.9f){
//				resetEverything();
//				roundCounter = MENU;
//			}
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
		if(roundCounter == MENU){
			if(key == Keys.SPACE){
				fadeToBlack();
			}
		}
		if(roundCounter == GAMEOVER){
			if(key == Keys.SPACE){
				fadeToBlack();
				System.out.println(fadeTarget + " RESTART");
			}
		}
		
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
//		if(key == Keys.SHIFT_LEFT){
//			placingUnits = !placingUnits;
//		}
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
			if(key == Keys.NUM_3){
				unitType = AIRBASE;
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
	
	public void fadeToBlack(){
		fadeTarget = 1f;
	}
	public void fadeToColour(){
		fadeTarget = 0f;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		GlyphLayout layout = new GlyphLayout();

//		layout.setText(Game.medium,"Play.");	
//		mouse.x = screenX;
//		mouse.y = 600-screenY;
//		Rectangle rect = new Rectangle(20,120-20,layout.width,layout.height*4);
//		System.out.println(mouse.dst(20,120));
//
//		if(rect.contains(mouse)){
//			
//		}
		Vector3 position = camera.unproject(new Vector3(screenX, screenY, camera.zoom));
		
		
		mouse.x = (int) (position.x);
		mouse.y = (int) (position.y);
		System.out.println(button);

		if(roundCounter >= DEPLOY_SCOUTS){
			// if the player clicks on the airbase
			Player player = getPlayerByName("Mali");
			ArrayList<Unit> tempUnits = new ArrayList<Unit>();
			
			for(Unit unit: player.getUnits()){
				if(unit.getClass() == Airbase.class){
					if(unit.isNear(position.x, position.y, 5)){
						// then we want to deploy a plane
						if(((Airbase)unit).getTotalDeployed() < 30){
							if(roundCounter == DEPLOY_SCOUTS){
								tempUnits.add(((Airbase) unit).deployAircraft(2));
							}else{
								if(button == 0){
									tempUnits.add(((Airbase) unit).deployAircraft(2));
								}else if(button == 1){
									tempUnits.add(((Airbase) unit).deployAircraft(1));
								}

							}
						}
					}
				}
			}
			
			
			for(Unit unit: tempUnits){
				player.setDeployedScouts(player.getDeployedScouts() + 1);
				player.getUnits().add(unit);
			}
		}


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
		
		mouse.x = screenX;
		mouse.y = 600-screenY;
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

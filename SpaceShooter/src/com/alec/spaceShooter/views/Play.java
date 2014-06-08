package com.alec.spaceShooter.views;

import java.util.ArrayList;

import com.alec.spaceShooter.Constants;
import com.alec.spaceShooter.controllers.CameraController;
import com.alec.spaceShooter.controllers.MyContactListener;
import com.alec.spaceShooter.models.Assets;
import com.alec.spaceShooter.models.FuelGauge;
import com.alec.spaceShooter.models.LanderDeath;
import com.alec.spaceShooter.models.LightBolt;
import com.alec.spaceShooter.models.PlayerShip;
import com.alec.spaceShooter.models.RedAlienShip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Play implements Screen {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera cameraGame, cameraUI;
	private CameraController cameraController;
	private SpriteBatch spriteBatch, hudBatch;
	private ShapeRenderer shapeRenderer;
	private Sprite background;
	private FuelGauge fuelGauge;
	private Array<Vector2> fastStars, slowStars;

	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8,
						POSITIONITERATONS = 3;
	private int width, height;

	private PlayerShip playerShip;
	private Array<LightBolt> lightBolts;
	private Array<RedAlienShip> redAliens;
	private LanderDeath landerDeath;
	private ArrayList<Body> destroyQueue = new ArrayList<Body>();
	private ArrayList<Joint> destroyJointQueue = new ArrayList<Joint>();
	
	private float timerRandomFire = 0;

	// initialize
	@Override
	public void show() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		createWorld();
		createBackground();
		createUI();
		createShip();
		createInputAdapter();
	}

	public void update(float delta) {
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATONS);
		playerShip.update(delta);

		cameraController.update(delta);
		cameraController.applyTo(cameraGame);
		
		for (RedAlienShip redAlien : redAliens) {
			if (redAlien.canDelete()) {
				destroyQueue.add(redAlien.getChassis());
				redAliens.removeValue(redAlien, true);
			}
		}
		
		// make a random ship fire
		timerRandomFire += delta;
		if (timerRandomFire > .1f) {
			timerRandomFire = 0;
			redAliens.get((int) (redAliens.size * Math.random())).fireLaser();
		}
		
		
		shapeRenderer.begin(ShapeType.Point);
		for (Vector2 star : fastStars) {
			star.add(0 , -.2f);
			shapeRenderer.point(star.x, star.y, 0);
			// if the star goes off screen
			if (star.y < -height / 2) {
				star.y = height / 2;
				star.x = (float) (-(height / 2) + height * Math.random());
			}
		}
		for (Vector2 star : slowStars) {
			star.add(0 , .07f);
			shapeRenderer.point(star.x, star.y, 0);
			// if the star goes off screen
			if (star.y < -height / 2) {
				star.y = height / 2;
				star.x = (float) (-(height / 2) + height * Math.random());
			}
		}
		shapeRenderer.end();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		update(delta);
		
		// add each sprite
		spriteBatch.setProjectionMatrix(cameraGame.combined);
		spriteBatch.begin();

		//background.draw(spriteBatch);

		// render the car's effects
		playerShip.render(spriteBatch, delta);

		if (landerDeath != null) {
			landerDeath.render(spriteBatch, delta);
		}

		for (LightBolt lightBolt : lightBolts) {
			lightBolt.render(spriteBatch);
		}
		for (RedAlienShip redAlien : redAliens) {
			redAlien.update(delta);
			redAlien.render(spriteBatch, delta);
		}
		
		// draw a scrolling background
		
		
		// debugRenderer.render(world, camera.combined);

		spriteBatch.end();

		hudBatch.begin();
		hudBatch.setProjectionMatrix(cameraUI.combined);
		fuelGauge.render(hudBatch, playerShip.getHealth());
		hudBatch.end();
		
		destroyQueues();
	}

	public void createUI() {
		fuelGauge = new FuelGauge(-(width / 2), (height / 2));
	}

	public void createInputAdapter() {
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.SPACE:
					playerShip.fireLaser();
					break;
				case Keys.W:
					playerShip.moveUp();
					break;
				case Keys.A:
					playerShip.moveLeft();
					break;
				case Keys.D:
					playerShip.moveRight();
					break;
				case Keys.S:
					playerShip.moveDown();
					break;
				default:
					break;
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {
				case Keys.W:
					playerShip.stopUp();
					break;
				case Keys.A:
					playerShip.stopLeft();
					break;
				case Keys.D:
					playerShip.stopRight();
					break;
				case Keys.S:
					playerShip.stopDown();
					break;
				default:
					break;
				}
				return false;
			}

			// click or touch
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				/**
				 * / // convert click point to box2d coords Vector3 testPoint =
				 * new Vector3(); // convert from vector2 to vector3
				 * testPoint.set(screenX, screenY, 0); // convert meters to
				 * pixel cords cameraGame.unproject(testPoint); /
				 **/
				playerShip.fireLaser();

				return false;
			}

			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {

				return false;
			}

			@Override
			public boolean touchDragged(int x, int y, int pointer) {

				return false;
			}
		}); 
	}
	
	public void createWorld() {
		// create the world with surface gravity
		world = new World(new Vector2(0f, 0f), true);
		world.setContactListener(new MyContactListener(this));
		debugRenderer = new Box2DDebugRenderer();

		spriteBatch = new SpriteBatch();
		hudBatch = new SpriteBatch();

		cameraGame = new OrthographicCamera(width, height);
		cameraUI = new OrthographicCamera(width, height);

		cameraController = new CameraController();

		background = new Sprite(Assets.instance.background.stars);
		background.setSize(120, 100);
		background.setPosition(-background.getWidth() / 2,
				-background.getHeight() / 2);

		background.setOrigin(-width / 2, -height / 2);
		lightBolts = new Array<LightBolt>();
	}

	public void createBackground() {
		fastStars = new Array<Vector2>();
		slowStars = new Array<Vector2>();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cameraGame.combined);
		
		// fix the offset to begin from the bottom left of the screen
		float startX = -(width / 2),
				startY = -(height / 2);
		// randomly generate the star field
		for (int index = 0; index < 100; index++) {
			fastStars.add(new Vector2((float)(startX + width * Math.random()), (float)(startY + height * Math.random())));
			slowStars.add(new Vector2((float)(startX + width * Math.random()), (float)(startY + height * Math.random())));
		}
	}
	
	public void createShip() {
		playerShip = new PlayerShip(this, new Vector2(0, 0));
		redAliens = new Array<RedAlienShip>();
		
		float startHeight = Constants.WORLD_HEIGHT - 5;
		for (int index = 0; index < 20; index++) {
			redAliens.add(new RedAlienShip(this, new Vector2(index * 7 - Constants.WORLD_WIDTH, startHeight)));
		}
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		// rayHandler.dispose();
		hudBatch.dispose();
	}

	private void destroyQueues() {
		if (!destroyQueue.isEmpty()) {
			for (Body body : destroyQueue) {
				world.destroyBody(body);
			}
			destroyQueue.clear();
		}
		if (!destroyJointQueue.isEmpty()) {
			for (Joint joint : destroyJointQueue) {
				world.destroyJoint(joint);
			}
			destroyJointQueue.clear();
		}
	}

	public void addLightBolt(LightBolt lightBolt) {
		lightBolts.add(lightBolt);
	}

	public Body createBody(BodyDef bodyDef) {
		return world.createBody(bodyDef);
	}

}
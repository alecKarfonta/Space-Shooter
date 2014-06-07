package com.alec.spaceShooter.views;

import java.util.ArrayList;

import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.RayHandler;

import com.alec.spaceShooter.Constants;
import com.alec.spaceShooter.controllers.CameraController;
import com.alec.spaceShooter.controllers.MyContactListener;
import com.alec.spaceShooter.models.Assets;
import com.alec.spaceShooter.models.FuelGauge;
import com.alec.spaceShooter.models.LanderDeath;
import com.alec.spaceShooter.models.LightBolt;
import com.alec.spaceShooter.models.Ship;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Play implements Screen {
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera cameraGame, cameraUI;
	private CameraController cameraController;
	private SpriteBatch spriteBatch, hudBatch;
	private RayHandler rayHandler;
	private Sprite background;
	private FuelGauge fuelGauge;

	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8;
	private final int POSITIONITERATONS = 3;
	private int width = Gdx.graphics.getWidth();
	private int height = Gdx.graphics.getHeight();
	private int bottom = -(height / 2);
	private int groundHeight = bottom + 1;

	public Ship ship;
	public Array<LightBolt> lightBolts;
	private LanderDeath landerDeath;
	private Body groundBody;
	private Array<Vector2> groundPoints;
	private boolean shouldDestroyLander = false;	
	private ArrayList<Body> destroyQueue = new ArrayList<Body>();
	private ArrayList<Joint> destroyJointQueue = new ArrayList<Joint>();

	@Override
	public void show() {
		// create each part of the screen
		createWorld();
		createUI();
		createShip();
		//createLights();

		// set up the input listnerer
		Gdx.input.setInputProcessor(new InputMultiplexer(
		// anonymous inner class for screen specific input
				new InputAdapter() {

					// Handle keyboard input
					@Override
					public boolean keyDown(int keycode) {
						switch (keycode) {
						case Keys.ESCAPE:
							((Game) Gdx.app.getApplicationListener())
									.setScreen(new MainMenu());
							break;

						case Keys.X:
							shouldDestroyLander = true;

						}
						return false;
					}

					// click or touch
					@Override
					public boolean touchDown(int screenX, int screenY,
							int pointer, int button) {
						Vector3 testPoint = new Vector3();
						// convert from vector2 to vector3
						testPoint.set(screenX, screenY, 0);
						// convert meters to pixel cords
						cameraGame.unproject(testPoint);
						
						lightBolts.add(new LightBolt(world, 
								ship.getChassis().getPosition(), 		// init
								new Vector2(testPoint.x, testPoint.y)	// target
						));
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
				}, ship)); // second input adapter for the input multiplexer
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// increment the world
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATONS);

		cameraController.update(delta);
		cameraController.applyTo(cameraGame);

		// add each sprite
		spriteBatch.setProjectionMatrix(cameraGame.combined);
		spriteBatch.begin();

		background.draw(spriteBatch);

		// render the car's effects
		ship.render(spriteBatch, Gdx.graphics.getDeltaTime());
		cameraController.setTarget(ship.getChassis().getPosition());

		if (landerDeath != null) {
			landerDeath.render(spriteBatch, delta);
		}
		
		for (LightBolt lightBolt : lightBolts) {
			lightBolt.render(spriteBatch);
		}

		// debugRenderer.render(world, camera.combined);

		spriteBatch.end();
		
		//rayHandler.setCombinedMatrix(cameraGame.combined);
		//rayHandler.updateAndRender();

		hudBatch.begin();
		hudBatch.setProjectionMatrix(cameraUI.combined);
		fuelGauge.render(hudBatch, ship.getFuel());
		hudBatch.end();

		destroyQueues();

		update(delta);

	}

	public void createUI() {
		fuelGauge = new FuelGauge(-(width / 2), (height / 2));
	}

	public void update(float delta) {

		ship.update(delta);
		if (shouldDestroyLander) {
			shouldDestroyLander = false;
		}
	}

	@Override
	public void resize(int width, int height) {
		// reset the camera size to the width of the window scaled to the zoom
		// level
		this.width = width;
		this.height = height;
	}

	public void createWorld() {
		// create the world with surface gravity
		world = new World(new Vector2(0f, 0f), true);
		world.setContactListener(new MyContactListener(this));
		debugRenderer = new Box2DDebugRenderer();

		spriteBatch = new SpriteBatch();
		hudBatch = new SpriteBatch();

		// setup a camera with a 1:1 ratio to the screen contents
		cameraGame = new OrthographicCamera(width, height);
		cameraUI = new OrthographicCamera(width, height);

		cameraController = new CameraController();

		background = new Sprite(Assets.instance.background.stars);
		background.setSize(120, 100);
		background.setPosition(-background.getWidth() / 2, -background.getHeight() / 2);

		background.setOrigin(-width / 2, -height / 2);
		lightBolts = new Array<LightBolt>();
	}

	
	public void createLights() {
		// RayHandler.useDiffuseLight(true);
		RayHandler.setGammaCorrection(true);
		rayHandler = new RayHandler(world);

		rayHandler.setCulling(true);

		Color lightColor = Color.WHITE;
		lightColor.a = .07f;
		Light light = new DirectionalLight(rayHandler, 1000, lightColor, -40);
		Filter lightFilter = new Filter();
		lightFilter.categoryBits = Constants.FILTER_LIGHT;
		lightFilter.maskBits = Constants.FILTER_GROUND;
		Light.setContactFilter(lightFilter);

		lightColor.a = .1f;
		rayHandler.setAmbientLight(lightColor);

		ship.createLights(rayHandler);
	}

	public void createShip() {
		ship = new Ship(world, new Vector2(0,0)); 
		
		cameraController.setPosition(new Vector2(ship.getChassis()
				.getPosition()));
	}

	public void destroyLander() {
		if (!ship.isDead) {
			ship.die();
			shouldDestroyLander = true;
			ship.isDead = true;
			landerDeath = new LanderDeath(ship.getChassis().getPosition(),
					rayHandler);
			destroyJointQueue.add(ship.jointChassisRocket);
			destroyJointQueue.add(ship.jointLeftLeg);
			destroyJointQueue.add(ship.jointRightLeg);
		}
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
		//rayHandler.dispose();
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

}
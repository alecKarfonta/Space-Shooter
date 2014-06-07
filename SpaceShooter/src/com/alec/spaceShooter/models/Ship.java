package com.alec.spaceShooter.models;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.alec.spaceShooter.Constants;
import com.alec.spaceShooter.MyMath;
import com.alec.spaceShooter.controllers.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Ship extends InputAdapter {
	private static final String TAG = Ship.class.getName();
	private Body chassis;
	public RevoluteJoint jointChassisRocket;
	public PrismaticJoint jointLeftLeg, jointRightLeg;
	private ParticleEmitter mainExhaust, sideExhaust;
	private Light spotLight, smallLight;
	private Sprite chassisSprite;
	private float x, y;
	private float fuel;
	private int smallLightDistance = 70, spotLightDistance = 250;
	private float smallLightPulseTimer = 0.0f;
	private float width, height, worldWidth, worldHeight;
	private boolean isMovingLeft, isMovingRight, isMovingUp, isMovingDown;

	// movement
	private float maxSpeed = 30;

	public boolean isFiringMainRocket = false, isFiringLeftRocket = false,
			isFiringRightRocket = false, isDead = false;

	public Ship(World world, Vector2 initPos) {
		this.x = initPos.x;
		this.y = initPos.y;
		this.width = 5;
		this.height = 5;
		this.worldWidth = 58;
		this.worldHeight = 30;
		fuel = 1.0f;

		FixtureDef chassisFixtureDef = new FixtureDef();
		chassisFixtureDef.density = .3f;
		chassisFixtureDef.friction = .32f;
		chassisFixtureDef.restitution = .1f;
		chassisFixtureDef.filter.categoryBits = Constants.FILTER_LANDER;
		chassisFixtureDef.filter.maskBits = Constants.FILTER_GROUND;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.linearDamping = 6.75f;
		bodyDef.allowSleep = false;
		bodyDef.fixedRotation = true;

		// create the chassis
		PolygonShape chassisShape = new PolygonShape();
		chassisShape.setAsBox(width / 2, height / 2);
		chassisFixtureDef.shape = chassisShape;

		chassis = world.createBody(bodyDef);
		chassis.createFixture(chassisFixtureDef);

		chassisSprite = new Sprite(Assets.instance.ship.ship);
		chassisSprite.setSize(width, height);
		chassisSprite.setOrigin(width / 2, height / 2);

		// exhaust
		mainExhaust = new ParticleEmitter();
		try {
			mainExhaust.load(Gdx.files.internal("exhaust.pfx").reader(2024));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Sprite particle = new Sprite(new Texture("images/particle.png"));
		mainExhaust.setSprite(particle);

		sideExhaust = new ParticleEmitter();
		try {
			sideExhaust
					.load(Gdx.files.internal("sideExhaust.pfx").reader(2024));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		sideExhaust.setSprite(particle);
	}

	public void update(float delta) {
		System.out.println(chassis.getPosition().x + " , " + chassis.getPosition().y);
		
		if (isMovingLeft) {
			if (chassis.getLinearVelocity().x > -maxSpeed && chassis.getPosition().x > -worldWidth) {
				chassis.setLinearVelocity(-maxSpeed, chassis.getLinearVelocity().y);
			}
		}
		if (isMovingRight) {
			if (chassis.getLinearVelocity().x < maxSpeed && chassis.getPosition().x < worldWidth) {
				chassis.setLinearVelocity(maxSpeed, chassis.getLinearVelocity().y);
			}
		}
		if (isMovingUp) {
			if (chassis.getLinearVelocity().y < maxSpeed && chassis.getPosition().y < worldHeight) {
				chassis.setLinearVelocity(chassis.getLinearVelocity().x, maxSpeed);
			} 
		}
		if (isMovingDown) {
			if (chassis.getLinearVelocity().y > -maxSpeed && chassis.getPosition().y > -worldHeight) {
				chassis.setLinearVelocity(chassis.getLinearVelocity().x, -maxSpeed);
			}
		}

		if (isFiringMainRocket) {
			chassis.applyLinearImpulse(MyMath.getRectCoords(
					.5f * chassis.getMass(),
					(float) (Math.toDegrees(chassis.getAngle()) + -270)),
					chassis.getPosition(), false);
			fuel -= .001f;
			if (fuel < 0.0f) {
				isFiringMainRocket = false;
			}
		}
		if (isFiringLeftRocket) {
			chassis.applyAngularImpulse(.25f * chassis.getMass(), false);
		}
		if (isFiringRightRocket) {
			chassis.applyAngularImpulse(-.25f * chassis.getMass(), false);
		}
		// updateLights(delta);
	}

	private void updateLights(float delta) {

		// update lights
		smallLightPulseTimer += delta / 2;
		if (smallLightPulseTimer > 1) {
			smallLightPulseTimer = 0.0f;
		}
		smallLight.setDistance(smallLightDistance);
		spotLight.setPosition(chassis.getPosition());
		spotLight
				.setDirection((float) (270 + Math.toDegrees(chassis.getAngle())));
	}

	public void render(SpriteBatch spriteBatch, float delta) {
		x = chassis.getPosition().x;
		y = chassis.getPosition().y;

		if (isMovingLeft) {

		}

		if (!mainExhaust.isComplete()) {
			Vector2 pos = chassis
					.getPosition()
					.add(MyMath.getRectCoords(3f,
							(float) (Math.toDegrees(chassis.getAngle()) + 270)));
			mainExhaust.setPosition(pos.x, pos.y);
			setMainExhaustRotation();
			mainExhaust.draw(spriteBatch, delta);
		}

		if (!sideExhaust.isComplete()) {
			Vector2 pos = chassis
					.getPosition()
					.add(MyMath.getRectCoords(
							width * .4f,
							(float) (Math.toDegrees(chassis.getAngle()) + (isFiringLeftRocket ? 45
									: 135))));
			sideExhaust.setPosition(pos.x, pos.y);
			setSideExhaustRotation(isFiringLeftRocket);
			sideExhaust.draw(spriteBatch, delta);
		}

		chassisSprite.setPosition(x - width / 2, y - height / 2);
		chassisSprite.setRotation((float) Math.toDegrees(chassis.getAngle()));
		chassisSprite.draw(spriteBatch);

	}

	public void createLights(RayHandler rayHandler) {
		// lander lights
		Color lightColor = Color.WHITE;
		lightColor.a = .25f;
		spotLight = new ConeLight(rayHandler, 100, lightColor,
				spotLightDistance, 0, 0, 0, 55);
		lightColor.a = .18f;
		smallLight = new PointLight(rayHandler, 10, lightColor,
				smallLightDistance, 0, 0);
		smallLight.attachToBody(chassis, 0, 0);
	}

	public void flash() {
		// TODO : flash lights really bright
	}

	private void setMainExhaustRotation() {
		float angle = (float) Math.toDegrees(chassis.getAngle());
		mainExhaust.getAngle().setLow(angle + 270);
		mainExhaust.getAngle().setHighMin(angle + 240);
		mainExhaust.getAngle().setHighMax(angle + 300);
	}

	private void setSideExhaustRotation(boolean isLeft) {
		float angle = (float) Math.toDegrees(chassis.getAngle());
		sideExhaust.getAngle().setLow(angle + (isLeft ? 15 : 180));
		sideExhaust.getAngle().setHighMin(angle + (isLeft ? 0 : 170));
		sideExhaust.getAngle().setHighMax(angle + (isLeft ? -20 : 190));
	}

	public void die() {
		isDead = true;
		isFiringMainRocket = false;
		isFiringLeftRocket = false;
		isFiringRightRocket = false;
		AudioManager.instance.stopSound(Assets.instance.sounds.mainExhaust);
		AudioManager.instance.stopSound(Assets.instance.sounds.sideExhaust);
		AudioManager.instance.play(Assets.instance.sounds.playerDeath);
		mainExhaust.allowCompletion();
		sideExhaust.allowCompletion();
		destroyLights();
	}

	public void destroyLights() {
		spotLight.setActive(false);
		smallLight.setActive(false);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean keyDown(int keycode) {

		switch (keycode) {
		case Keys.W:
			isMovingUp = true;
			break;
		case Keys.A:
			isMovingLeft = true;
			break;
		case Keys.D:
			isMovingRight = true;
			break;
		case Keys.S:
			isMovingDown = true;
			break;
		default:
			break;

		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.W:
			isMovingUp = false;
			break;
		case Keys.A:
			isMovingLeft = false;
			break;
		case Keys.D:
			isMovingRight = false;
			break;
		case Keys.S:
			isMovingDown = false;
			break;
		case Keys.SPACE:

			break;
		default:
			break;
		}
		return super.keyUp(keycode);
	}

	public Body getChassis() {
		return chassis;
	}

	public float getFuel() {
		return fuel;
	}

}
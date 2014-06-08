package com.alec.spaceShooter.models;

import com.alec.spaceShooter.Constants;
import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Ship {
	private static final String TAG = Ship.class.getName();
	private Body chassis;
	private float health = 1.0f;
	private float x, y;
	private float width = 5, height = 5;
	private boolean isMovingLeft, isMovingRight, isMovingUp, isMovingDown;

	// movement
	protected float speed = 30;	// default speed

	public boolean isDead = false;

	public Ship(Play play, Vector2 initPos) {
		this.x = initPos.x;
		this.y = initPos.y;

		FixtureDef chassisFixtureDef = new FixtureDef();
		chassisFixtureDef.density = .3f;
		chassisFixtureDef.friction = .32f;
		chassisFixtureDef.restitution = .1f;

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

		chassis = play.createBody(bodyDef);
		chassis.createFixture(chassisFixtureDef);
		chassis.setUserData(this);
	}

	public void update(float delta) {
		if (isMovingLeft) {
			if (chassis.getLinearVelocity().x > -speed
					&& chassis.getPosition().x > -Constants.WORLD_WIDTH) {
				chassis.setLinearVelocity(-speed,
						chassis.getLinearVelocity().y);
			}
		} else if (isMovingRight) {
			if (chassis.getLinearVelocity().x < speed
					&& chassis.getPosition().x < Constants.WORLD_WIDTH) {
				chassis.setLinearVelocity(speed,
						chassis.getLinearVelocity().y);
			}
		}
		if (isMovingUp) {
			if (chassis.getLinearVelocity().y < speed
					&& chassis.getPosition().y < Constants.WORLD_HEIGHT) {
				chassis.setLinearVelocity(chassis.getLinearVelocity().x,
						speed);
			}
		} else if (isMovingDown) {
			if (chassis.getLinearVelocity().y > -speed
					&& chassis.getPosition().y > -Constants.WORLD_HEIGHT) {
				chassis.setLinearVelocity(chassis.getLinearVelocity().x,
						-speed);
			}
		}
	}

	public void render(SpriteBatch spriteBatch, float delta) {	}

	public void damage(float amount) {
		health -= amount;
		if (health <= 0) {
			die();
		}
	}
	
	public void die() {
		isDead = true;
	}

	// getters / setters
	public Body getChassis() {
		return chassis;
	}
	public void moveLeft() {
		isMovingLeft = true;
	}
	public void moveRight() {
		isMovingRight = true;
	}
	public void moveUp() {
		isMovingUp = true;
	}
	public void moveDown() {
		isMovingDown = true;
	}
	public void stopLeft() {
		isMovingLeft = false;
	}
	public void stopRight() {
		isMovingRight = false;
	}
	public void stopUp() {
		isMovingUp = false;
	}
	public void stopDown() {
		isMovingDown = false;
	}
	public float getHealth() {
		return health;
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
}
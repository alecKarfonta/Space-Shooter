package com.alec.spaceShooter.models;

import com.alec.spaceShooter.MyMath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LightBolt {
	
	private Vector2 target;
	private float initVel = 100000;
	private float width = .1f;
	private float height = 1f;
	private Sprite sprite;
	private Body body;

	public LightBolt (World world, Vector2 initPos, Vector2 target) {
		this.target = target;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(initPos);
		bodyDef.bullet = true;
		bodyDef.fixedRotation = true;
		bodyDef.gravityScale = 0;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;
		fixtureDef.density = 0.001f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.friction = 0.0f;
		
		sprite = new Sprite(Assets.instance.weapons.blueLaser);
		sprite.setScale(6, 2);
		sprite.setSize(width,
				height);
		sprite.setOrigin(width/2, height/2);
		
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.getFixtureList().get(0).setUserData(this);
		
		float angle = MyMath.getAngleBetween(initPos, target);
		body.setTransform(initPos, (float) Math.toRadians(90 + angle));
		
		Vector2 forceVector = new Vector2();	// polar
		forceVector.x = initVel;
		forceVector.y = angle;
		body.setLinearVelocity(MyMath.getRectCoords(forceVector));
	}

	public void render(SpriteBatch batch) {
		sprite.setPosition(body.getPosition().x - width/2 , 
				body.getPosition().y - height/2);
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.draw(batch);
	}
	


	public void setTarget(Vector2 newTarget) {
		this.target = newTarget;
		float angle = MyMath.getAngleBetween(body.getPosition(), target);
		body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
		Vector2 forceVector = new Vector2();	// polar
		forceVector.x = initVel;
		forceVector.y = angle;
		body.setLinearVelocity(MyMath.getRectCoords(forceVector));
	}
	
}
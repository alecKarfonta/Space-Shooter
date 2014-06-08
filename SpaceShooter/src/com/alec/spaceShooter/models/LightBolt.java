package com.alec.spaceShooter.models;

import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class LightBolt {
	private float initVel = 50;
	private float width = .2f;
	private float height = 1f;
	private Sprite sprite;
	private Body body;

	public LightBolt (Play play, Vector2 initPos) {
		this(play, initPos, 1);
	}
	
	public LightBolt (Play play, Vector2 initPos, int direction) {
		initVel *= direction;
		
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
		fixtureDef.density = 0.01f;
		fixtureDef.restitution = 0.1f;
		fixtureDef.friction = 0.1f;
		
		sprite = new Sprite(Assets.instance.weapons.blueLaser);
		sprite.setScale(6, 2);
		sprite.setSize(width,
				height);
		sprite.setOrigin(width/2, height/2);
		
		body = play.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setUserData(this);
		
		body.setLinearVelocity(new Vector2(0,initVel));
	}

	public void render(SpriteBatch batch) {
		sprite.setPosition(body.getPosition().x - width/2 , 
				body.getPosition().y - height/2);
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.draw(batch);
	}
	
}
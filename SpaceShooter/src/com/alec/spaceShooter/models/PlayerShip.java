package com.alec.spaceShooter.models;

import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerShip extends Ship {
	
	private Play play; 	// keep a reference in play for easily creating and destroying objects from ship
	private Sprite chassisSprite;
	private float width = 5,
			height = 5;
	private float x, y;
	
	public PlayerShip(Play play, Vector2 initPos) {
		super(play, initPos);
		this.play = play;

		chassisSprite = new Sprite(Assets.instance.ships.playerShip);
		chassisSprite.setSize(width,height);
		chassisSprite.setOrigin(width / 2, width / 2);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
	}

	@Override
	public void render(SpriteBatch spriteBatch, float delta) {
		super.render(spriteBatch, delta);
		x = getChassis().getPosition().x;
		y = getChassis().getPosition().y;
		chassisSprite.setPosition(x - width / 2, y - height / 2);
		chassisSprite.setRotation((float) Math.toDegrees(getChassis().getAngle()));
		chassisSprite.draw(spriteBatch);
		
	}
	
	public void fireLaser() {
		play.addLightBolt(new LightBolt(play, 
				getChassis().getPosition()));	// target

	}

	@Override
	public void die() {
		super.die();
		
	}
	
}

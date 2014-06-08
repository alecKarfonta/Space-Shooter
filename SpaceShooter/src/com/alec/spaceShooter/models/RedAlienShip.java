package com.alec.spaceShooter.models;

import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class RedAlienShip extends Ship {
	private Play play; // keep a reference in play for easily creating and
						// destroying objects from ship
	private Sprite chassisSprite;
	private float width = 3, height = 3;
	private float x, y;
	private ParticleEffect explosion;
	private boolean canDelete = false;

	public RedAlienShip(Play play, Vector2 initPos) {
		super(play, initPos);
		this.play = play;

		chassisSprite = new Sprite(Assets.instance.ships.redAlien);
		chassisSprite.setSize(width, height);
		chassisSprite.setOrigin(width / 2, width / 2);
		
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal("particles/explosion.pfx"),
				Gdx.files.internal("particles"));

	}

	@Override
	public void update(float delta) {
		super.update(delta);

	}

	@Override
	public void render(SpriteBatch spriteBatch, float delta) {
		if (!isDead) {
			super.render(spriteBatch, delta);
			x = getChassis().getPosition().x;
			y = getChassis().getPosition().y;
			chassisSprite.setPosition(x - width / 2, y - height / 2);
			chassisSprite.setRotation((float) Math.toDegrees(getChassis()
					.getAngle()));
			chassisSprite.draw(spriteBatch);
		} else {
			explosion.draw(spriteBatch, delta);
			if (explosion.isComplete()) {
				canDelete = true;
			}
		}

	}

	@Override
	public void die() {
		super.die();
		explosion.setPosition(getChassis().getPosition().x, getChassis().getPosition().y);
		explosion.start();
	}
	
	public void fireLaser() {
		play.addLightBolt(new LightBolt(play, getChassis().getPosition().add(0, -height - 1), -1)); // target

	}
	
	public boolean canDelete() {
		return canDelete;
	}
}

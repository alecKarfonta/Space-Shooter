package com.alec.spaceShooter.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;

public class FuelGauge {
	private TextureRegion fill, border;
	private float displayFuel = 1.0f;
	private int width, height;
	private int x, y;
	
	public FuelGauge (int x , int y) {
		this.width = Assets.instance.ui.fuelGauge.getRegionWidth();
		this.height = Assets.instance.ui.fuelGauge.getRegionHeight();

		this.x = x ;
		this.y = y - height;
	}
	
	public void render(SpriteBatch spriteBatch, float fuel) {
		displayFuel = Interpolation.linear.apply(displayFuel, fuel, .25f);
		spriteBatch.draw(Assets.instance.ui.fuelGauge, 
				x, y, 		
				width, height);	
		spriteBatch.draw(Assets.instance.ui.fuelGaugeFill, 
				x, y,
				0, 0,
				width, height, 
				displayFuel, 1,
				0);	
		
	}
	
}

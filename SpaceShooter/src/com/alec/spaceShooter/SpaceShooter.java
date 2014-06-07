package com.alec.spaceShooter;

import com.alec.spaceShooter.controllers.AudioManager;
import com.alec.spaceShooter.models.Assets;
import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class SpaceShooter extends Game {
	public final static String TITLE = "Physics Play";
	
	@Override
	public void create() {		
		
		Assets.instance.init(new AssetManager());
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.intro);
		
		setScreen(new Play());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}

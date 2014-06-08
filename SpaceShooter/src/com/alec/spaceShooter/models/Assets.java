package com.alec.spaceShooter.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;


public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();	// singleton
	private AssetManager assetManager;
	private TextureAtlas atlas;
	
	private Assets() {}
	
	public AssetFonts fonts;
	public AssetSounds sounds;
	public AssetMusic music;
	public AssetShips ships;
	public AssetBackground background;
	public AssetUI ui;
	public AssetWeapons weapons;
	
	
	public void init( AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.setErrorListener(this);
		assetManager.load("images/atlas.pack", TextureAtlas.class);
		assetManager.load("sounds/mainExhaust.mp3", Sound.class);
		assetManager.load("sounds/sideExhaust.wav", Sound.class);
		assetManager.load("sounds/playerDeath.wav", Sound.class);
		assetManager.load("music/intro.ogg", Music.class);
		assetManager.finishLoading();
		
		//assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.finishLoading();
		
		// log all the assets there were loaded
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String asset : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + asset);
		}
		
		atlas = assetManager.get("images/atlas.pack");
		
		// create the game resources (inner Asset~ classes)
		ships = new AssetShips(atlas);
		weapons = new AssetWeapons(atlas);
		background = new AssetBackground(atlas);
		ui = new AssetUI(atlas);
		fonts = new AssetFonts();
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
	}
	
	public class AssetShips {
		public final AtlasRegion playerShip, redAlien;
		
		public AssetShips(TextureAtlas atlas) {
			playerShip = atlas.findRegion("ships/playerShip");
			redAlien = atlas.findRegion("ships/redAlien");
		}
	}
	
	public class AssetWeapons {
		public final AtlasRegion blueLaser;
		public AssetWeapons (TextureAtlas atlas) {
			blueLaser = atlas.findRegion("weapons/laserBlue");
		}
	}
	
	public class AssetBackground {
		public final AtlasRegion stars, blueNebula, redNebula, rainbowNebula;
		public AssetBackground (TextureAtlas atlas) {
			stars = atlas.findRegion("background/itsFullOfStars");
			blueNebula = atlas.findRegion("blueNebula");
			redNebula = atlas.findRegion("redNebula");
			rainbowNebula = atlas.findRegion("rainbowNebula");
			
		}
	}
	
	public class AssetUI {
		public final AtlasRegion fuelGauge, fuelGaugeFill;
		public AssetUI (TextureAtlas atlas) {
			fuelGauge = atlas.findRegion("ui/border");
			fuelGaugeFill = atlas.findRegion("ui/fill");
		}
	}
	
	public class AssetMusic {
		public final Music intro;
		
		public AssetMusic (AssetManager am) {
			intro = am.get("music/intro.ogg", Music.class);
			intro.setLooping(true);
			
		}
	}
	
	public class AssetSounds {
		public final Sound mainExhaust, sideExhaust, playerDeath;
		
		public AssetSounds (AssetManager am) {
			mainExhaust = am.get("sounds/mainExhaust.mp3", Sound.class);
			sideExhaust = am.get("sounds/sideExhaust.wav", Sound.class);
			playerDeath = am.get("sounds/playerDeath.wav", Sound.class);
		}
	}
		
	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		
		public AssetFonts () {
			defaultSmall = new BitmapFont(Gdx.files.internal("fonts/white16.fnt"), true);
			defaultNormal = new BitmapFont(Gdx.files.internal("fonts/white16.fnt"), true);
			defaultBig = new BitmapFont(Gdx.files.internal("fonts/white16.fnt"), true);
			
			defaultSmall.setScale(.1f);
			defaultNormal.setScale(1.0f);
			defaultBig.setScale(2.0f);
			
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset: '" + asset.fileName + "' " + (Exception)throwable);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}
	
}

package com.alec.spaceShooter.controllers;

import com.alec.spaceShooter.models.LightBolt;
import com.alec.spaceShooter.models.RedAlienShip;
import com.alec.spaceShooter.views.Play;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MyContactListener implements ContactListener {
	private static final String TAG = MyContactListener.class.getName();
	
	private Play play; // reference play so you can call functions
	
	public MyContactListener(Play play) {
		this.play = play;
	}
	
	@Override
	public void beginContact(Contact contact) {
		System.out.println(contact.getFixtureA().getBody().getUserData() + " - " + contact.getFixtureB().getBody().getUserData());
		// Light Bolt - Red Alien
		if (contact.getFixtureA().getBody().getUserData() instanceof LightBolt
				&& contact.getFixtureB().getBody().getUserData() instanceof RedAlienShip) {
			((RedAlienShip)contact.getFixtureB().getBody().getUserData()).damage(.5f);
		} else if (contact.getFixtureA().getBody().getUserData() instanceof RedAlienShip
				&& contact.getFixtureB().getBody().getUserData() instanceof LightBolt) {
			((RedAlienShip)contact.getFixtureA().getBody().getUserData()).damage(.5f);
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if (impulse.getNormalImpulses()[0] > 200) {
		}
	}

}

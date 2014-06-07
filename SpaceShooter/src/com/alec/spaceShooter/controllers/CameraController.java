package com.alec.spaceShooter.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraController {
	private static final String TAG = CameraController.class.getName();
	
	// settings
	private final float MAX_ZOOM_IN = .01f;
	private final float MAX_ZOOM_OUT = 100.0f;
	private final float FOLLOW_SPEED = .05f;
	private final float ZOOM_SPEED = .01f;

	private Vector2 position;	
	private Vector2 target;
	private Vector2 zoom;	// vectors just so you can lerp with it's function
	private Vector2 targetZoom;

	public CameraController() {
		position = new Vector2(0,0);
		target = new Vector2(0,0);
		zoom = new Vector2();
		targetZoom = new Vector2();
		zoom.x = .01f;
		targetZoom.x = .1f; 			// init zoom
	}

	public void update(float deltaTime) {
		position.lerp(target, FOLLOW_SPEED); 
		if (targetZoom.x != zoom.x) {
			zoom.lerp(targetZoom, ZOOM_SPEED);
		}
	}

	public void applyTo(OrthographicCamera camera) {
		camera.position.set(position.x, position.y, 0);
		camera.zoom = zoom.x;
		camera.update();
	}

	public boolean hasTarget() {
		return target != null;
	}

	public boolean hasTarget(Vector2 target) {
		return hasTarget() && this.target.equals(target);
	}

	public void setTarget(Vector2 target) {
		this.target = target;
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	public Vector2 getPosition() {
		return position;
	}

	public void addZoom(float amount) {
		targetZoom.x = MathUtils.clamp(targetZoom.x + amount, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}

	public float getZoom() {
		return zoom.x;
	}

	public void setZoom(float zoom) {
		this.zoom.x = zoom;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
}

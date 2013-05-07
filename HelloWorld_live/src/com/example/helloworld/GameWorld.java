package com.example.helloworld;

import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

public class GameWorld {

	public static String T = "HelloWorld";
	private GameEngine gameEngine;
	public static Activity activity;
	private static Bitmap fireBitmap;
	private static Bitmap turretBitmap;
	public Turret turret;
	private static Bitmap bulletBitmap;
	private float bulletSpeed = 1200;
	public FireButton fireButton;
	float touchDownX = 0;
	// float lastRot = 0;
	Random r;

	public GameWorld(Activity activity) {
		GameWorld.activity = activity;
		r = new Random();
		fireBitmap = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.firebutton);
		turretBitmap = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.turret);
		bulletBitmap = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.bullet);
		turret = new Turret();
		fireButton = new FireButton();
	}

	public void initialize() {
		gameEngine = new GameEngine(activity);
		gameEngine.initialize();
		turret.initialize();
		fireButton.initialize();
	}

	public void onResume() {
		gameEngine.onResume();
	}

	public void onPause() {
		gameEngine.onPause();
	}

	public synchronized Entity addEntity(Entity entity) {
		gameEngine.addEntity(entity);
		return entity;
	}

	public class Turret extends Entity {

		public Turret() {
			this(turretBitmap, 0, 0, 0, 0, 0);
		}

		private Turret(Bitmap bitmap, float positionLeft, float positionTop,
				float velocityX, float velocityY, int collideFilter) {
			super(bitmap, positionLeft, positionTop, velocityX, velocityY,
					collideFilter);
		}

		public void initialize() {
			this.setPositionXY(GameEngine.screenWidth / 2,
					GameEngine.screenHeight);
			gameEngine.addTopLayerEntity(this);
		}

		public void setAim(float degrees) {
			if (degrees < -90)
				degrees = -90;
			if (degrees > 90)
				degrees = 90;
			this.setRotation(degrees);
		}

		public void fireBullet() {
			Entity bullet1 = new Entity(bulletBitmap, this.getPositionX()
					- (bulletBitmap.getWidth() / 2), this.getPositionY()
					- (bulletBitmap.getHeight() / 2), (float) Math.cos((this
					.getRotation() - 90) * (Math.PI / 180))
					* bulletSpeed, (float) Math.sin((this.getRotation() - 90)
					* (Math.PI / 180))
					* bulletSpeed, 1);
			Entity bullet2 = new Entity(bulletBitmap, this.getPositionX()
					- (bulletBitmap.getWidth() / 2), this.getPositionY()
					- (bulletBitmap.getHeight() / 2), (float) Math.cos((this
					.getRotation() - 100) * (Math.PI / 180))
					* bulletSpeed, (float) Math.sin((this.getRotation() - 100)
					* (Math.PI / 180))
					* bulletSpeed, 1);
			Entity bullet3 = new Entity(bulletBitmap, this.getPositionX()
					- (bulletBitmap.getWidth() / 2), this.getPositionY()
					- (bulletBitmap.getHeight() / 2), (float) Math.cos((this
					.getRotation() - 80) * (Math.PI / 180))
					* bulletSpeed, (float) Math.sin((this.getRotation() - 80)
					* (Math.PI / 180))
					* bulletSpeed, 1);
			gameEngine.addEntity(bullet1);
			gameEngine.addEntity(bullet2);
			gameEngine.addEntity(bullet3);
		}

	}

	public class FireButton extends Entity {

		public FireButton() {
			this(fireBitmap, 0, 0, 0, 0, 0);
		}

		private FireButton(Bitmap bitmap, float positionLeft,
				float positionTop, float velocityX, float velocityY,
				int collideFilter) {
			super(bitmap, positionLeft, positionTop, velocityX, velocityY,
					collideFilter);
		}

		public void initialize() {
			this.setPositionXY(GameEngine.screenWidth
					- this.getBitmap().getWidth() / 2, GameEngine.screenHeight
					- this.getBitmap().getHeight() / 2);
			gameEngine.addTopLayerEntity(this);
		}

		public void onTouch() {
			turret.fireBullet();
		}

	}

	private static int touchID;

	public void onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			Log.d(T, "ACTION_DOWN");

			if (fireButton.getBounds().contains((int) event.getX(),
					(int) event.getY())) {
				fireButton.onTouch();
				touchID = -1;
				break;
			}

			touchID = event.getPointerId(0);

			// touchDownX = event.getX();
			// lastRot = turret.getRotation();
			addEntity(
					new Entity(r.nextInt(GameEngine.screenWidth), r
							.nextInt(GameEngine.screenHeight),
							r.nextInt(100) - 50, r.nextInt(100) - 50, 2))
					.setAngularVelocity(-720);

			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN: {
			Log.d(T, "ACTION_POINTER_DOWN");

			if (fireButton.getBounds().contains((int) event.getX(1),
					(int) event.getY(1))) {
				fireButton.onTouch();
				break;
			}

			break;
		}
		case MotionEvent.ACTION_POINTER_UP: {
			Log.d(T, "ACTION_POINTER_UP");

			break;
		}
		case MotionEvent.ACTION_MOVE: {
			Log.d(T, "ACTION_MOVE");

			// if (fireButton.getBounds().contains((int) event.getX(),
			// (int) event.getY())) {
			// break;
			// }

			if (event.getPointerId(0) == touchID)
				if (event.getY() > (GameEngine.screenHeight*2/3))
					turret.setAim((event.getX() / 2) - 90);

			// if((event.getX() - touchDownX) > 90)
			// touchDownX = event.getX()-90;
			// if((event.getX() - touchDownX) < -90)
			// touchDownX = event.getX()+90;
			// turret.setAim(lastRot + ((event.getX() - touchDownX)));

			break;
		}
		case MotionEvent.ACTION_UP: {
			Log.d(T, "ACTION_UP");

			break;
		}
		}
	}

}

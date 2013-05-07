package com.example.helloworld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class GameEngine {

	public static String T = "GameEngine";
	public static Activity activity;
	GameThread gameThread;
	MySurfaceView surfaceView;
	CollisionThread collisionThread;
	public static int screenWidth;
	public static int screenHeight;
	public static ArrayList<Entity> entities;
	public static ArrayList<Entity> topLayerEntities;
	public static Bitmap defaultBitmap;
	public static int backgroundColor;
	public static Bitmap backgroundImage;

	public GameEngine(Activity activity) {
		GameEngine.activity = activity;
		updateScreenSize();
		defaultBitmap = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.ic_launcher);
		backgroundImage = BitmapFactory.decodeResource(activity.getResources(),
				R.drawable.landscape1);
		backgroundImage = Bitmap.createBitmap(backgroundImage, 0, 0,
				screenWidth, screenHeight);
		backgroundColor = Color.argb(255, 200, 235, 255);
	}

	public synchronized Entity addEntity(Entity entity) {
		entities.add(entity);
		return entity;
	}

	public synchronized Entity addTopLayerEntity(Entity entity) {
		topLayerEntities.add(entity);
		return entity;
	}

	public static synchronized ArrayList<Entity> getEntityListCopy() {
		ArrayList<Entity> newList = new ArrayList<Entity>(entities);
		return newList;
	}

	public static synchronized ArrayList<Entity> getTopLayerEntityListCopy() {
		ArrayList<Entity> newList = new ArrayList<Entity>(topLayerEntities);
		return newList;
	}

	public void initialize() {
		entities = new ArrayList<Entity>();
		topLayerEntities = new ArrayList<Entity>();
		surfaceView = new MySurfaceView(activity);
		RelativeLayout layout = new RelativeLayout(activity);
		layout.addView(surfaceView);
		activity.setContentView(layout);
		gameThread = new GameThread(activity, 180);
		collisionThread = new CollisionThread(activity, 60);
	}

	protected void onResume() {
		surfaceView.onResume();
		gameThread.onResume();
		collisionThread.onResume();
	}

	protected void onPause() {
		surfaceView.onPause();
		gameThread.onPause();
		collisionThread.onPause();
	}

	private void updateScreenSize() {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
	}

	class GameThread implements Runnable {

		private float refreshRate;
		Context context;
		Thread thread = null;
		volatile boolean running = false;

		public GameThread(Context context, int refreshRate) {
			this.context = context;
			this.refreshRate = refreshRate;
		}

		public void onResume() {
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPause() {
			boolean retry = true;
			running = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public synchronized void run() {
			while (running) {
				ArrayList<Entity> list = getEntityListCopy();
				ArrayList<Entity> topLayerList = getTopLayerEntityListCopy();

				try {
					Thread.sleep((long) (1000 / this.refreshRate));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (Entity entity : list) {
					if (entity == null)
						break;
					entity.update(refreshRate);
					if (entity.isOffScreen())
						entities.remove(entity);
				}

				for (Entity entity : topLayerList) {
					if (entity == null)
						break;
					entity.update(refreshRate);
					if (entity.isOffScreen())
						topLayerEntities.remove(entity);
				}

			}
		}
	}

	class CollisionThread implements Runnable {

		private float refreshRate;
		Context context;
		Thread thread = null;
		volatile boolean running = false;

		public CollisionThread(Context context, int refreshRate) {
			this.context = context;
			this.refreshRate = refreshRate;
		}

		public void onResume() {
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPause() {
			boolean retry = true;
			running = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public synchronized void run() {
			while (running) {
				boolean colliding;
				ArrayList<Entity> list = getEntityListCopy();
				// for(Entity entity : list)
				// entity.setAcuteBounds();

				if (list != null)
					for (Entity entity : list) {
						if (entity == null)
							break;
						colliding = false;
						for (Entity otherEntity : list) {
							if (!entity.equals(otherEntity))
								if (entity.intersects(otherEntity)) {
									entity.setColliding(true);
									otherEntity.setColliding(true);
									colliding = true;
									entities.remove(entity);
									entities.remove(otherEntity);
									break;
								}
						}
						if (!colliding)
							entity.setColliding(false);
					}

				try {
					Thread.sleep((long) (1000 / this.refreshRate));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class MySurfaceView extends SurfaceView implements Runnable {

		Context context;
		Thread thread = null;
		SurfaceHolder surfaceHolder;
		volatile boolean running = false;
		private Paint paint = new Paint();

		public MySurfaceView(Context context) {
			super(context);
			this.context = context;
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			surfaceHolder = getHolder();
		}

		public void onResume() {
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPause() {
			boolean retry = true;
			running = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public synchronized void run() {
			while (running) {
				if (surfaceHolder.getSurface().isValid()) {
					Canvas canvas = surfaceHolder.lockCanvas();
					canvas.drawColor(Color.RED);
					canvas.drawBitmap(backgroundImage, 0, 0, null);
					ArrayList<Entity> list = getEntityListCopy();
					for (Entity entity : list) {
						if (entity == null)
							break;

							// if (entity.isColliding()) {
							// paint.setColorFilter(new LightingColorFilter(
							// Color.RED, 1));
							// paint.setColor(Color.RED);
							// } else {
							// paint.setColorFilter(null);
							// paint.setColor(Color.GRAY);
							// }

							Matrix matrix = new Matrix();
							matrix.postRotate(entity.getRotation(), entity
									.getBitmap().getWidth() / 2, entity
									.getBitmap().getHeight() / 2);
							matrix.postTranslate(entity.getPositionLeft(),
									entity.getPositionTop());

							canvas.drawBitmap(entity.getBitmap(), matrix, paint);

							// canvas.drawRect(entity.getBounds(), paint);

							// canvas.drawCircle(entity.getPositionX(),
							// entity.getPositionY(),
							// entity.getWidth() / 2, paint);
					}

					list = getTopLayerEntityListCopy();
					for (Entity entity : list) {
						if (entity == null)
							break;

							// if (entity.isColliding()) {
							// paint.setColorFilter(new LightingColorFilter(
							// Color.RED, 1));
							// paint.setColor(Color.RED);
							// } else {
							// paint.setColorFilter(null);
							// paint.setColor(Color.GRAY);
							// }

							Matrix matrix = new Matrix();
							matrix.postRotate(entity.getRotation(), entity
									.getBitmap().getWidth() / 2, entity
									.getBitmap().getHeight() / 2);
							matrix.postTranslate(entity.getPositionLeft(),
									entity.getPositionTop());

							canvas.drawBitmap(entity.getBitmap(), matrix, paint);

							// canvas.drawRect(entity.getBounds(), paint);

							// canvas.drawCircle(entity.getPositionX(),
							// entity.getPositionY(),
							// entity.getWidth() / 2, paint);
					}

					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

	}

}

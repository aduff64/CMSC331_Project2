package com.example.helloworld;

import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.app.Activity;

public class MainActivity extends Activity {

	public static String T = "HelloWorld";
	private GameWorld gameWorld;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameWorld = new GameWorld(this);
		gameWorld.initialize();
	}

	@Override
	protected void onResume() {
		super.onResume();
		gameWorld.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		gameWorld.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gameWorld.onTouchEvent(event);
		return true;
	}

}

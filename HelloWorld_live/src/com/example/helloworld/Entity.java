package com.example.helloworld;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Entity {

	private Bitmap bitmap;
	private float positionLeft;
	private float positionTop;
	private float velocityX;
	private float velocityY;
	private float width;
	private float height;
	private boolean colliding;
	private Rect acuteBounds;
	private int collideFilter;// 0 = no collide, >0 = collide with all but same
								// value
	private float rotation;
	private float angularVelocity;

	public Entity(float positionLeft, float positionTop, float velocityX,
			float velocityY) {
		this(GameEngine.defaultBitmap, positionLeft, positionTop, velocityX,
				velocityY, 1);
	}

	public Entity(float positionLeft, float positionTop, float velocityX,
			float velocityY, int collideFilter) {
		this(GameEngine.defaultBitmap, positionLeft, positionTop, velocityX,
				velocityY, collideFilter);
	}

	public Entity(Bitmap bitmap, float positionLeft, float positionTop,
			float velocityX, float velocityY, int collideFilter) {
		this.bitmap = bitmap;
		this.positionLeft = positionLeft;
		this.positionTop = positionTop;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.collideFilter = collideFilter;
		this.rotation = 0;
		this.angularVelocity = 0;
	}

	public void setRotation(float degrees) {
		this.rotation = (degrees % 360);
	}

	public float getRotation() {
		return this.rotation;
	}

	public void rotate(float degrees) {
		setRotation(this.rotation + degrees);
	}

	public void setVelocity(float x, float y) {
		this.velocityX = x;
		this.velocityY = y;
	}

	public void translate(float x, float y) {
		setPositionXY(this.getPositionX() + x, this.getPositionY() + y);
	}

	public void setAngularVelocity(float dps) {
		this.angularVelocity = dps;
	}

	public void setPositionXY(float x, float y) {
		this.positionLeft = x - (bitmap.getWidth() / 2);
		this.positionTop = y - (bitmap.getHeight() / 2);
	}

	public float getPositionX() {
		return this.positionLeft + (bitmap.getWidth() / 2);
	}

	public float getPositionY() {
		return this.positionTop + (bitmap.getHeight() / 2);
	}

	public float getPositionLeft() {
		return this.positionLeft;
	}

	public float getPositionTop() {
		return this.positionTop;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public Bitmap getBitmap() {
		return this.bitmap;
	}

	public boolean isOffScreen() {
		if ((this.positionLeft + this.bitmap.getWidth()) < 0)
			return true;
		if (this.positionLeft > GameEngine.screenWidth)
			return true;
		if ((this.positionTop + this.bitmap.getHeight()) < 0)
			return true;
		if (this.positionTop > GameEngine.screenHeight)
			return true;
		return false;
	}

	public boolean intersects(Entity entity) {
		if ((this.collideFilter > 0) && (entity.collideFilter > 0))
			if (this.collideFilter != entity.collideFilter)
				if (this.getBounds().intersect(entity.getBounds()))
					return true;
		return false;
	}

	public boolean intersectsAcuteBounds(Entity entity) {
		if (this.acuteBounds.intersect(entity.acuteBounds))
			return true;
		return false;
	}

	public boolean intersectsAny(LinkedList<Rect> list) {
		for (Rect bound : list)
			if (this.getBounds().intersect(bound))
				return true;
		return false;
	}

	public Rect getBounds() {
		return new Rect((int) this.positionLeft, (int) this.positionTop,
				(int) (this.positionLeft + this.getWidth()),
				(int) (this.positionTop + this.getHeight()));
	}

	public void setAcuteBounds() {
		this.acuteBounds = this.getBounds();
	}

	public Rect getAcuteBounds() {
		return this.acuteBounds;
	}

	public boolean isColliding() {
		return this.colliding;
	}

	public void setColliding(boolean colliding) {
		this.colliding = colliding;
	}
	
	public int getCollideFilter(){
		return this.collideFilter;
	}

	public void update(float refreshRate) {
		this.translate(this.velocityX / refreshRate, this.velocityY
				/ refreshRate);
		this.rotate(angularVelocity / refreshRate);
	}

}

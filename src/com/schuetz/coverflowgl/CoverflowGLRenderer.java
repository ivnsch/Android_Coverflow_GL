package com.schuetz.coverflowgl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.example.android.basicglsurfaceview.R;

public class CoverflowGLRenderer implements Renderer {
	int maxAngle = 80;
	CoverflowGLActivity context;

	private List<CoverflowGLSlide> slides = new ArrayList<CoverflowGLSlide>();

	private float separation;

	private int slidesCount = 10;
	private float ratio;
	
	private int pressX = -99;
	private int pressY = -99;
	private int thisWidth;
	private int thisHeight;
	
	public CoverflowGLRenderer(CoverflowGLActivity context, float separation) {
		this.context = context;
		this.separation = separation;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		int[] ds = new int[] {R.drawable.mario1, R.drawable.mario2, R.drawable.mario3};
		
		for (int i = 0; i < slidesCount; i++) {
			CoverflowGLSlide slide = new CoverflowGLSlide(context, i, context);

			//use images from drawable folder
			int d = ds[i % ds.length];
			slide.initTexture(gl, context.getResources().openRawResource(d));

			//use images from web 
//			slide.initView(gl, context, i);
			
			slide.setXAndRotateY((separation * i) / 3);
			slides.add(slide);
		}
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		Ray ray = null;
		if (pressX != -99) {
			ray = new Ray(gl, thisWidth, thisHeight, pressX, pressY);
		}
		
		for (CoverflowGLSlide slide : slides) {
			slide.draw(gl, ray);
		}
	}

	private void checkGlError(String op, GL10 gl) {
		int error;
		while ((error = gl.glGetError()) != GL10.GL_NO_ERROR) {
			Log.e("huhu", op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}
	
	public void selectSlide(int index) {
		CoverflowGLSlide slide = slides.get(index);
		
		float offset = -slide.getX(); 
	
		for (CoverflowGLSlide s : slides) {
			s.setXAndRotateY(s.getX() + offset);
		}
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		thisWidth = width;
		thisHeight = height;
		
		ratio = (float)width / height;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 27);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void onXDelta(float xDelta) {
		float coverflowXDelta = xDelta / 4000f;
		
		for (CoverflowGLSlide slide : slides) {
			slide.moveXAndRotateY(coverflowXDelta);
		}
	}
	
	public int getSlideForTouch(int touchX, int touchY) {
		//?
		return -1;
	}

	public void onPress(int x, int y) {
		pressX = x;
		pressY = y;
	}
}
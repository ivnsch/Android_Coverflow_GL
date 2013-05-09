package com.schuetz.coverflowgl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.basicglsurfaceview.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class CoverflowGLSlide {

	private int MAX_ROT_Y = 80;

	private FloatBuffer verticesBuffer;
	private short[] indices;
	private ShortBuffer indicesBuffer;

	private FloatBuffer textureBuffer;
	private int textureId = -1;

	private float rotY;
	private float x;

	private Bitmap bitmap;

	CoverflowGLActivity activity;

	static int counter;

	private float vertices[];

	private int index;

	private TouchListener touchListener;

	public static interface TouchListener {
		public void onTouch(int index);
	}

	public CoverflowGLSlide(CoverflowGLActivity activity, int index, TouchListener touchListener) {
		this.activity = activity;
		this.index = index;

		this.touchListener = touchListener;

		vertices = new float[] {
				-0.5f,  0.5f, 0.0f,   // top left
				-0.5f, -0.5f, 0.0f,   // bottom left
				0.5f, -0.5f, 0.0f,   // bottom right
				0.5f,  0.5f, 0.0f  // top right
		};

		indices = new short[] {0, 1, 2, 0, 2, 3};

		float textCoords[] = {
				0f, 0f,
				0f, 1f,
				1f, 1f,
				1f, 0f
		};

		textureBuffer = ByteBuffer.allocateDirect(textCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		textureBuffer.put(textCoords).position(0);

		//initialize vertex Buffer for triangle  
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4); // (# of coordinate values * 4 bytes per float)

		vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
		verticesBuffer = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
		verticesBuffer.put(vertices);    // add the coordinates to the FloatBuffer
		verticesBuffer.position(0);            // set the buffer to read the first coordinate

		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2); // (# of coordinate values * 2 bytes per short)
		dlb.order(ByteOrder.nativeOrder());
		indicesBuffer = dlb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
	}

	public void initTexture(GL10 gl, InputStream bitmapInputStream) {


		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(bitmapInputStream);
		} finally {
			try {
				bitmapInputStream.close();
			} catch(IOException e) {}
		}

		initTexture(gl, bitmap);
	}

	public void initTexture(GL10 gl, Bitmap bitmap) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		int[] texture = new int[1];
		gl.glGenTextures(1, texture, 0);


		textureId = texture[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
		//		bitmap.recycle();
	}

	public void draw(GL10 gl, Ray ray) {
		gl.glLoadIdentity();

		float zoom = (float) (Math.abs(rotY) / 30);

		GLU.gluLookAt(gl, 0, 0, 3f + zoom, 0, 0, 0, 0, 1, 0);

		gl.glTranslatef(x, 0, 0);
		gl.glRotatef(-rotY, 0, 1, 0);


		///////////////////////////////////////////////////////////////////////////
		if (ray != null) {

			MatrixGrabber matrixGrabber = new MatrixGrabber();
			matrixGrabber.getCurrentState(gl);

			int coordCount = vertices.length;
			float[] convertedSquare = new float[coordCount];
			float[] resultVector = new float[4];
			float[] inputVector = new float[4];

			for(int i = 0; i < coordCount; i = i + 3){
				inputVector[0] = vertices[i];
				inputVector[1] = vertices[i+1];
				inputVector[2] = vertices[i+2];
				inputVector[3] = 1;
				Matrix.multiplyMV(resultVector, 0, matrixGrabber.mModelView, 0, inputVector,0);
				convertedSquare[i] = resultVector[0]/resultVector[3];
				convertedSquare[i+1] = resultVector[1]/resultVector[3];
				convertedSquare[i+2] = resultVector[2]/resultVector[3];
			}

			Triangle t1 = new Triangle(new float[] {convertedSquare[0], convertedSquare[1], convertedSquare[2]}, new float[] {convertedSquare[3], convertedSquare[4], convertedSquare[5]}, new float[] {convertedSquare[6], convertedSquare[7], convertedSquare[8]});
			Triangle t2 = new Triangle(new float[] {convertedSquare[0], convertedSquare[1], convertedSquare[2]}, new float[] {convertedSquare[6], convertedSquare[7], convertedSquare[8]}, new float[] {convertedSquare[9], convertedSquare[10], convertedSquare[11]});

			float[] point1 = new float[3];
			int intersects1 = Triangle.intersectRayAndTriangle(ray, t1, point1);
			float[] point2 = new float[3];
			int intersects2 = Triangle.intersectRayAndTriangle(ray, t2, point2);

			if (intersects1 == 1 || intersects1 == 2) {
				touchListener.onTouch(index);
			}
			else if (intersects2 == 1 || intersects2 == 2) {
				touchListener.onTouch(index);
			}
		}

		///////////////////////////////////////////////////////////////////////////


		if (bitmap != null && textureId == -1) {
			initTexture(gl, bitmap);
		}

		float d = 1- (Math.abs(rotY) / 130f); //actual value should be 80f but we dont want the slides in the extremes black
		gl.glColor4f(d, d, d, 1f);


		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glFrontFace(GL10.GL_CW);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glDrawElements(GLES20.GL_TRIANGLES, indices.length,  GLES10.GL_UNSIGNED_SHORT, indicesBuffer);
	}

	public void setX(float x) {
		this.x = x;
	}

	public void moveX(float delta) {
		this.x += delta;
	}

	public void moveXAndRotateY(float deltaX) {
		this.x += deltaX;

		this.rotY = x * MAX_ROT_Y;

		rotY = Math.max(-MAX_ROT_Y, rotY);
		rotY = Math.min(MAX_ROT_Y, rotY);
	}

	public void setXAndRotateY(float x) {
		this.x = x;

		this.rotY = x * MAX_ROT_Y;

		rotY = Math.max(-MAX_ROT_Y, rotY);
		rotY = Math.min(MAX_ROT_Y, rotY);
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotY() {
		return rotY;
	}

	public float getX() {
		return x;
	}

	public void initView(GL10 gl, Context context, int position) {
		initViewBitmap(gl, context, position);
	}

	private void initViewBitmap(final GL10 gl, Context context, final int position) {

		String[] urls = new String[] {
				"http://media.tumblr.com/tumblr_lmfsq2JV7d1qb8x3g.jpg",
//				"http://img4-1.cookinglight.timeinc.net/i/Oxmoor/oh3320p31-small-apple-l.jpg%3F400:400",
				"http://static.ddmcdn.com/gif/what-is-lemon-zest-1.jpg",
				"http://qmixalot.com/wp-content/uploads/2010/04/cherry.jpg",
				"http://static.ddmcdn.com/gif/what-is-lemon-zest-1.jpg",
				"http://media.tumblr.com/tumblr_lmfsq2JV7d1qb8x3g.jpg",
				"http://s3.amazonaws.com/readers/2012/09/21/buahalpukat_1.png"
		};

		final String url = urls[counter++ % urls.length];

		LayoutInflater i = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final ViewGroup v = (ViewGroup)i.inflate(R.layout.test, null);

		final ImageView img = (ImageView)v.findViewById(R.id.img);


		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				ImageLoader.getInstance().displayImage(url, img, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted() {}
					@Override
					public void onLoadingFailed(FailReason failReason) {}
					@Override
					public void onLoadingComplete(Bitmap loadedImage) {

						img.setImageBitmap(loadedImage);

						TextView textView2 = (TextView)v.findViewById(R.id.text2);
						textView2.setText("Text " + position);

						v.setDrawingCacheEnabled(true);
						v.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
						v.measure(
								MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
								MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
						v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
						v.buildDrawingCache(true);

						if (v.getDrawingCache() != null) {
							bitmap = Bitmap.createBitmap(v.getDrawingCache());
							v.setDrawingCacheEnabled(false);

						}

					}
					@Override
					public void onLoadingCancelled() {}
				});
			}

		});

	}
	private Bitmap createViewBitmap(Context context, int position) {
		LayoutInflater i = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup v = (ViewGroup)i.inflate(R.layout.test, null);

		TextView textView2 = (TextView)v.findViewById(R.id.text2);
		textView2.setText("Text " + position);

		v.setDrawingCacheEnabled(true);
		v.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		v.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.buildDrawingCache(true);

		if (v.getDrawingCache() != null) {
			Bitmap viewCapture = Bitmap.createBitmap(v.getDrawingCache());

			return viewCapture;

		}

		return null;
	}
}
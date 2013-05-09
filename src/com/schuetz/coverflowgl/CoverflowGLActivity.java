package com.schuetz.coverflowgl;

import javax.microedition.khronos.opengles.GL;

import com.example.android.basicglsurfaceview.R;
import com.schuetz.coverflowgl.CoverflowGLSlide.TouchListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CoverflowGLActivity extends Activity implements TouchListener {
    private CoverflowGLView view;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.main);
        view = (CoverflowGLView)findViewById(R.id.glView);
        
        view.setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }
        });
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

	@Override
	public void onTouch(int index) {
		//enable this for picking
//		view.selectSlide(index);
	}
}
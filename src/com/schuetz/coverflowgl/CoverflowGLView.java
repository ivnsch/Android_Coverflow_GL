/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schuetz.coverflowgl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class CoverflowGLView extends GLSurfaceView {
	
    private int lastX;
	private int lastY;
	private CoverflowGLRenderer triangleRenderer;

	private float separation = 2f;
	
	public CoverflowGLView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(1);
		triangleRenderer = new CoverflowGLRenderer((CoverflowGLActivity)context, separation);
		setRenderer(triangleRenderer);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public CoverflowGLView(CoverflowGLActivity context) {
        super(context);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			
			
			switch(event.getAction() & MotionEvent.ACTION_MASK) {
			
			case MotionEvent.ACTION_DOWN:
				lastX = x;
				lastY = y;
				
				triangleRenderer.onPress(x, y);
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				triangleRenderer.onXDelta(x - lastX);
				
			break;

		}
		return super.onTouchEvent(event);
	}

    
    public void selectSlide(int slide) {
    	triangleRenderer.selectSlide(slide);
    }
    
    public void setModelObjects(int[] drawableIds) {
    	
    }
}
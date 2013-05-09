package com.schuetz.coverflowgl;

import android.app.Application;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
public class GLApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
        .threadPoolSize(5)
        .build();
		
		ImageLoader.getInstance().init(config);
	}
}
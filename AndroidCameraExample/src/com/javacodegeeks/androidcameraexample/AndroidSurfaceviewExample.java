package com.javacodegeeks.androidcameraexample;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidSurfaceviewExample extends Activity implements SurfaceHolder.Callback {
	TextView testView;

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;

	PictureCallback rawCallback;
	ShutterCallback shutterCallback;
	PictureCallback jpegCallback;
	int screenWidth,screenHeight;
	int viewWidth,viewHeight;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_ex);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		surfaceHolder.addCallback(this);
		YourCustomSurfaceView(this);
		// deprecated setting, but required on Android versions prior to 3.0
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				FileOutputStream outStream = null;
				try {
					outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
					outStream.write(data);
					outStream.close();
					Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				Toast.makeText(getApplicationContext(), "Picture Saved", 2000).show();
				refreshCamera();
			}
		};
	}

	public void captureImage(View v) throws IOException {
		//take the picture
		camera.takePicture(null, null, jpegCallback);
	}

	public void refreshCamera() {
		if (surfaceHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		try {
			camera.setPreviewDisplay(surfaceHolder);
			
			camera.startPreview();
		} catch (Exception e) {

		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		//Toast.makeText(getApplicationContext(), ""+, Toast.LENGTH_SHORT).show();
		DisplayMetrics metrics=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		System.out.println(metrics.widthPixels+"   "+metrics.heightPixels);
		Camera.Parameters param;
		param = camera.getParameters();
		List<Size> allSizes=param.getSupportedPreviewSizes();
		for(Size s:allSizes){
			System.out.println("All Size Formats :"+s.width+s.height);
		}
		Camera.Size sizes=param.getPreviewSize();
		System.out.println("Surfaceview  "+w+"Height :"+h);
		param.set("orientation", "po");
		camera.setDisplayOrientation(90);
		//param.set("rotation", 180);
		//param.setRotation(90);
		System.out.println("Camera "+sizes.width+"Height :"+sizes.height);
		param.setPreviewSize(sizes.width,sizes.height);
		//param.setZoom(10);
		camera.setParameters(param);
		refreshCamera();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// open the camera
			camera = Camera.open();
			
		} catch (RuntimeException e) {
			// check for exceptions
			System.err.println(e);
			return;
		}
		Camera.Parameters param;
		param = camera.getParameters();
		//param.set("rotation",0);
		// modify parameter
		param.setPreviewSize(640, 480);
		
		camera.setParameters(param);
		try {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			 List<Size> localSizes = camera.getParameters().getSupportedPreviewSizes();
			 
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (Exception e) {
			// check for exceptions
			System.err.println(e);
			return;
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Integer.parseInt(Build.VERSION.SDK) >= 8){
			//setDisplayOrientation(camera, 90);
		}
			else { 
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { 
				//	p.set("orientation", "portrait"); p.set("rotation", 90);
					setDisplayOrientation(camera, 0);
					} 
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { 
				//	p.set("rotation", 90); 
					setDisplayOrientation(camera,90);
					}
				} 
		}

	private void setDisplayOrientation(Camera mycam, int i) {
		Camera.Parameters params=mycam.getParameters();
		params.set("rotation", i);
		 DisplayMetrics metrics=new DisplayMetrics();
	      //requestWindowFeature(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
	       getWindowManager().getDefaultDisplay().getMetrics(metrics);
	       System.out.println("Width And Height  :"+metrics.widthPixels+"  "+metrics.heightPixels);
		params.setPreviewSize(metrics.widthPixels, metrics.heightPixels);
		mycam.setParameters(params);
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop preview and release camera
		camera.stopPreview();
		camera.release();
		camera = null;
	}
	public  void YourCustomSurfaceView(Context context) {
	    
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    int rotation = display.getRotation();

	    // If vertical, we fill 2/3 the height and all the width. If horizontal,
	    // fill the entire height and 2/3 the width
	    if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
	        screenWidth = display.getWidth();
	        screenHeight = display.getHeight();
	        viewHeight = 2 *  (screenHeight / 3);
	        viewWidth = screenWidth;
	    } else {
	        screenWidth = display.getWidth();
	        screenHeight = display.getHeight();
	        viewWidth = 2 * (screenWidth / 3);
	        viewHeight = screenHeight;
	    }
	    // Enter rest of code here
	}
}